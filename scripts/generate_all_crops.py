from __future__ import annotations

import argparse
import json
import re
import shutil
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable

import requests
from bs4 import BeautifulSoup


WIKI_URL = "https://zh.stardewvalleywiki.com/%E5%86%9C%E4%BD%9C%E7%89%A9"

# Wiki display names / slugs sometimes differ from our canonical naming.
SLUG_OVERRIDES: dict[str, str] = {
    "cranberries": "cranberry",
    "powdermelon": "powder_melon",
}

# Some crops can be planted in multiple seasons; zh wiki groups them under one section.
# 0=spring,1=summer,2=fall,3=winter
SEASON_OVERRIDES: dict[str, set[int]] = {
    "corn": {1, 2},
    "sunflower": {1, 2},
    "sun_flower": {1, 2},
    "wheat": {1, 2},
    "coffee_bean": {0, 1},
    "ancient_fruit": {0, 1, 2},
}

# Seedbag texture names sometimes differ from crop slug.
SEED_TEXTURE_OVERRIDES: dict[str, str] = {
    "ancient_fruit": "ancient_seeds",
}

# Crops we should not generate as plantable crops.
IGNORE_SLUGS: set[str] = {
    "mixed_seeds",
    "mixed_flower_seeds",
    "spring_seeds",
    "summer_seeds",
    "fall_seeds",
    "winter_seeds",
    "grass",
    "weeds",
    "dead_crop",
    # Not present in StardewRes crop textures (and table layout differs on wiki)
    "fiber",
    "tea_leaves",
    "taro_root",
    "qi_fruit",
    # Missing in provided StardewRes texture set
    "cactus_fruit",
    "pineapple",
    "sweet_gem_berry",
    "unmilled_rice",
}


@dataclass(frozen=True)
class CropInfo:
    cn_name: str
    slug: str
    section: str
    cn_desc: str
    phase_days: tuple[int, int, int, int]
    growth_days: int
    regrow_days: int | None
    edible: bool
    price: tuple[int, int, int, int]
    energy: tuple[int, int, int, int]
    health: tuple[int, int, int, int]
    seed_buy_price: int | None

    @property
    def seasons(self) -> set[int]:
        override = SEASON_OVERRIDES.get(self.slug)
        if override is not None:
            return set(override)
        # fall back to section name
        if "春季作物" in self.section:
            return {0}
        if "夏季作物" in self.section:
            return {1}
        if "秋季作物" in self.section:
            return {2}
        if "冬季作物" in self.section:
            return {3}
        return set()

    @property
    def primary_season_code(self) -> int:
        seasons = sorted(self.seasons)
        return seasons[0] if seasons else 0

    @property
    def primary_season_folder(self) -> str:
        return {0: "spring", 1: "summer", 2: "fall", 3: "winter"}.get(self.primary_season_code, "spring")


def crop_item_folder_for_packages(crop: CropInfo) -> str:
    seasons = crop.seasons
    if len(seasons) == 1:
        s = next(iter(seasons))
        return {0: "spring", 1: "summer", 2: "fall", 3: "winter"}.get(s, "spring")
    return "other"


def canonicalize_slug(slug: str) -> str:
    return SLUG_OVERRIDES.get(slug, slug)


def slugify(display_name: str) -> str:
    s = display_name.strip().lower()
    s = s.replace("’", "").replace("'", "")
    s = re.sub(r"[^a-z0-9\s_-]", "", s)
    s = re.sub(r"[\s-]+", "_", s)
    s = re.sub(r"_+", "_", s)
    return s.strip("_")


def normalize_spaces(s: str) -> str:
    s = re.sub(r"\[\d+\]", "", s)
    s = re.sub(r"\s+", " ", s)
    return s.strip()


def parse_ints(text: str) -> list[int]:
    return [int(x.replace(",", "")) for x in re.findall(r"\d[\d,]*", text)]


def parse_total_days(text: str) -> int | None:
    m = re.search(r"(?:共|总计)\s*[:：]\s*(\d+)(?:\s*[-~]\s*(\d+))?\s*天", text)
    if not m:
        return None
    a = int(m.group(1))
    b = int(m.group(2)) if m.group(2) else None
    return max(a, b) if b else a


def parse_regrow_days(text: str) -> int | None:
    if "持续收获" not in text and "持续产出" not in text:
        return None
    if "每天" in text:
        return 1
    m = re.search(r"每\s*(\d+)\s*天", text)
    if m:
        return int(m.group(1))
    return None


def fetch_html(url: str) -> str:
    resp = requests.get(
        url,
        headers={
            "User-Agent": "stardewcraft-dev/1.0 (+https://example.invalid)",
            "Accept-Language": "zh-CN,zh;q=0.9,en;q=0.6",
        },
        timeout=30,
    )
    resp.raise_for_status()
    return resp.text


def iter_crop_sections(soup: BeautifulSoup) -> Iterable[tuple[str, str, BeautifulSoup]]:
    h2 = None
    for tag in soup.select("h2, h3"):
        if tag.name == "h2":
            h2 = normalize_spaces(tag.get_text(" ", strip=True))
            continue
        if tag.name != "h3" or not h2:
            continue
        img = tag.find("img")
        if not img or not img.get("alt"):
            continue
        alt = img.get("alt")
        if not alt.endswith(".png"):
            continue
        display = alt[: -len(".png")]
        yield h2, display, tag


def parse_crop_from_h3(section: str, display_en: str, h3) -> CropInfo:
    raw_slug = slugify(display_en)
    slug = canonicalize_slug(raw_slug)

    # Chinese name: first non-file link text in h3
    cn_name = ""
    for a in h3.find_all("a"):
        href = a.get("href") or ""
        text = normalize_spaces(a.get_text(" ", strip=True))
        if not text:
            continue
        if "File:" in href or href.startswith("/File:"):
            continue
        cn_name = text
        break

    cn_desc = ""
    table = None
    for el in h3.next_elements:
        name = getattr(el, "name", None)
        if name in {"h2", "h3"} and el is not h3:
            break
        if cn_desc == "" and name == "p":
            cn_desc = normalize_spaces(el.get_text(" ", strip=True))
        if table is None and name == "table":
            t = normalize_spaces(el.get_text(" ", strip=True))
            if "售价" in t and ("恢复" in t or "能量" in t):
                table = el

    if table is None:
        raise ValueError(f"No data table for {slug}")

    def row_cells(tr) -> list[str]:
        return [normalize_spaces(td.get_text(" ", strip=True)) for td in tr.find_all("td")]

    prices: list[int] = []
    restores: list[tuple[int, int]] = []
    seed_buy_price: int | None = None
    phase_days: tuple[int, int, int, int] | None = None
    growth_days: int | None = None
    edible = True
    regrow_days: int | None = None

    for tr in table.select("tr"):
        cells = row_cells(tr)
        if not cells:
            continue
        joined = " ".join(cells)

        # Seed buy price (accept only if unambiguous)
        if seed_buy_price is None:
            if "免费" in joined:
                seed_buy_price = 0
            else:
                nums = parse_ints(joined)
                if ("杂货店" in joined or "Joja" in joined or "乔家" in joined) and len(nums) == 1:
                    seed_buy_price = nums[0]

        # Growth phase row: contains "X 天" 4 times + total
        if phase_days is None:
            day_nums = [int(m.group(1)) for m in re.finditer(r"(\d+)\s*天", joined)]
            if len(day_nums) >= 4 and ("共" in joined or "总计" in joined):
                phase_days = (day_nums[0], day_nums[1], day_nums[2], day_nums[3])
                total = parse_total_days(joined)
                growth_days = total if total is not None else sum(phase_days)

        # Sell price rows: typically 2 cells and exactly one "X金"
        if len(prices) < 4:
            gold_nums = [int(m.group(1).replace(",", "")) for m in re.finditer(r"(\d[\d,]*)\s*金", joined)]
            if len(cells) == 2 and len(gold_nums) == 1:
                prices.append(gold_nums[0])

        # Restore rows: 2 integers (energy, health) and no 金
        if edible and len(restores) < 4:
            if "不可食用" in joined or re.search(r"Not\s*edible", joined, re.I):
                edible = False
                restores = []
            elif "金" not in joined:
                nums = parse_ints(joined)
                if len(nums) == 2:
                    restores.append((nums[0], nums[1]))

        # Regrow info
        rd = parse_regrow_days(joined)
        if rd is not None:
            regrow_days = rd

    if growth_days is None or phase_days is None:
        raise ValueError(f"Missing growth days/phase for {slug}")
    if len(prices) != 4:
        raise ValueError(f"Sell prices not complete for {slug}: {prices}")

    if not edible:
        energy = (0, 0, 0, 0)
        health = (0, 0, 0, 0)
    else:
        if len(restores) != 4:
            raise ValueError(f"Restore values not complete for {slug}: {restores}")
        energy = (restores[0][0], restores[1][0], restores[2][0], restores[3][0])
        health = (restores[0][1], restores[1][1], restores[2][1], restores[3][1])

    return CropInfo(
        cn_name=cn_name or display_en,
        slug=slug,
        section=section,
        cn_desc=cn_desc,
        phase_days=phase_days,
        growth_days=growth_days,
        regrow_days=regrow_days,
        edible=edible,
        price=(prices[0], prices[1], prices[2], prices[3]),
        energy=energy,
        health=health,
        seed_buy_price=seed_buy_price,
    )


def pascal_case_from_slug(slug: str) -> str:
    parts = [p for p in slug.split("_") if p]
    return "".join(p[:1].upper() + p[1:] for p in parts)


def upper_const(slug: str) -> str:
    return slug.upper()


def ensure_dir(path: Path) -> None:
    path.mkdir(parents=True, exist_ok=True)


def write_text(path: Path, content: str) -> None:
    ensure_dir(path.parent)
    path.write_text(content, encoding="utf-8")


def copy_file(src: Path, dst: Path) -> None:
    ensure_dir(dst.parent)
    shutil.copy2(src, dst)


def split_desc_flavor(cn_desc: str) -> tuple[str, str | None]:
    s = cn_desc.strip()
    if not s:
        return "", None
    # Prefer Chinese full stop split.
    parts = [p.strip() for p in s.split("。") if p.strip()]
    if not parts:
        return s, None
    if len(parts) == 1:
        return parts[0] + "。", None
    return parts[0] + "。", "。".join(parts[1:]) + "。"


def seasons_to_zh(seasons: set[int]) -> str:
    mapping = {0: "春", 1: "夏", 2: "秋", 3: "冬"}
    ordered = [mapping[s] for s in sorted(seasons) if s in mapping]
    if not ordered:
        return ""
    if len(ordered) == 1:
        return ordered[0]
    return "或".join(ordered)


def seasons_to_en(seasons: set[int]) -> str:
    mapping = {0: "Spring", 1: "Summer", 2: "Fall", 3: "Winter"}
    ordered = [mapping[s] for s in sorted(seasons) if s in mapping]
    if not ordered:
        return ""
    if len(ordered) == 1:
        return ordered[0]
    return " or ".join(ordered)


def update_lang_json(path: Path, updates: dict[str, str]) -> None:
    data = json.loads(path.read_text(encoding="utf-8"))
    # Preserve existing values; only add missing keys.
    for k, v in updates.items():
        if k not in data:
            data[k] = v
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser(description="Generate all StardewCraft crops (Java + assets + textures) from zh wiki.")
    parser.add_argument("--wiki", default=WIKI_URL)
    parser.add_argument(
        "--resourcepack",
        default=r"d:\\MC\\.minecraft\\versions\\1.21.3-Fabric 0.18.1\\resourcepacks\\StardewRes",
        help="Path to StardewRes resource pack root.",
    )
    parser.add_argument("--modroot", default=str(Path(__file__).resolve().parents[1]), help="Path to mod workspace root.")
    args = parser.parse_args()

    modroot = Path(args.modroot).resolve()
    rp_root = Path(args.resourcepack).resolve()

    crops_root = rp_root / "assets" / "minecraft" / "textures" / "item" / "crops"
    seedbag_root = rp_root / "assets" / "minecraft" / "textures" / "item" / "seedbag"

    if not crops_root.exists() or not seedbag_root.exists():
        print(f"ERROR: resourcepack path invalid: {rp_root}")
        print(f"Expected: {crops_root}")
        print(f"Expected: {seedbag_root}")
        return 2

    html = fetch_html(args.wiki)
    soup = BeautifulSoup(html, "html.parser")

    crops: list[CropInfo] = []
    errors: list[str] = []

    for section, display_en, h3 in iter_crop_sections(soup):
        slug = canonicalize_slug(slugify(display_en))
        if slug in IGNORE_SLUGS:
            continue
        try:
            info = parse_crop_from_h3(section, display_en, h3)
        except Exception as e:
            errors.append(f"{slug}: {e}")
            continue
        if info.slug in IGNORE_SLUGS:
            continue
        crops.append(info)

    if errors:
        print("ERROR: failed to parse some crops:")
        for e in errors:
            print(" -", e)
        return 1

    # Deduplicate by slug
    by_slug: dict[str, CropInfo] = {}
    for c in crops:
        by_slug[c.slug] = c
    crops = [by_slug[k] for k in sorted(by_slug.keys())]

    java_item_base_dir = modroot / "src" / "main" / "java" / "com" / "stardew" / "craft" / "item"
    java_block_dir = modroot / "src" / "main" / "java" / "com" / "stardew" / "craft" / "block" / "crop"

    assets_root = modroot / "src" / "main" / "resources" / "assets" / "stardewcraft"
    data_root = modroot / "src" / "main" / "resources" / "data" / "stardewcraft"

    # Generate per-crop files
    missing_textures: list[str] = []

    for crop in crops:
        class_base = pascal_case_from_slug(crop.slug)
        item_class = f"{class_base}Item"
        seed_class = f"{class_base}SeedItem"
        block_class = f"{class_base}CropBlock"

        season_folder = crop.primary_season_folder
        item_folder = crop_item_folder_for_packages(crop)
        item_package = f"com.stardew.craft.item.crop.{item_folder}"
        java_crop_item_dir = java_item_base_dir / "crop" / item_folder

        # --- Java: crop item ---
        sell_n, sell_s, sell_g, sell_i = crop.price
        en_n, en_s, en_g, en_i = crop.energy
        hp_n, hp_s, hp_g, hp_i = crop.health

        edible_block = "true" if crop.edible else "false"
        has_food_props = crop.edible

        constructor_code = ""
        finish_using_code = ""
        is_food_override = "false"
        get_energy_body = "return 0;"
        get_health_body = "return 0;"

        if has_food_props:
            is_food_override = "true"
            constructor_code = f"""
    public {item_class}(Item.Properties properties) {{
        super(properties
                .food(new FoodProperties.Builder()
                        .nutrition(2)
                        .saturationModifier(0.3f)
                        .alwaysEdible()
                        .build())
        );
    }}
"""
            finish_using_code = """
    @Override
    public ItemStack finishUsingItem(ItemStack stack, net.minecraft.world.level.Level level, net.minecraft.world.entity.LivingEntity livingEntity) {
        int quality = QualityHelper.getQuality(stack);
        int health = getHealthRestoration(quality);
        int energy = getEnergyRestoration(quality);

        ItemStack result = super.finishUsingItem(stack, level, livingEntity);

        if (!level.isClientSide && livingEntity instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            if (health > 0) {
                int currentSDHealth = com.stardew.craft.player.PlayerStardewDataAPI.getHealth(serverPlayer);
                int maxSDHealth = com.stardew.craft.player.PlayerStardewDataAPI.getMaxHealth(serverPlayer);
                com.stardew.craft.player.PlayerStardewDataAPI.setHealth(serverPlayer, Math.min(maxSDHealth, currentSDHealth + health));
            }

            if (energy > 0) {
                com.stardew.craft.player.PlayerStardewDataAPI.restoreEnergy(serverPlayer, energy);
            }
        }

        return result;
    }
"""
            get_energy_body = "return getEnergyRestoration(QualityHelper.getQuality(stack));"
            get_health_body = "return getHealthRestoration(QualityHelper.getQuality(stack));"
        else:
            constructor_code = f"""
    public {item_class}(Item.Properties properties) {{
        super(properties);
    }}
"""

        java_item = f"""package {item_package};

    import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.item.quality.QualityHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * {crop.cn_name}
 */
public class {item_class} extends Item implements IStardewItem {{

    private static final int[] SELL_PRICE_BY_QUALITY = new int[]{{{sell_n}, {sell_s}, {sell_g}, {sell_i}}};
    private static final int[] ENERGY_BY_QUALITY = new int[]{{{en_n}, {en_s}, {en_g}, {en_i}}};
    private static final int[] HEALTH_BY_QUALITY = new int[]{{{hp_n}, {hp_s}, {hp_g}, {hp_i}}};

{constructor_code}

    @Override
    public Component getName(ItemStack stack) {{
        int quality = QualityHelper.getQuality(stack);
        Component prefix = QualityHelper.getQualityPrefix(quality);
        Component baseName = Component.translatable(this.getDescriptionId(stack))
                .withStyle(ChatFormatting.WHITE);

        var customData = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA,
                net.minecraft.world.item.component.CustomModelData.DEFAULT);
        if (quality != QualityHelper.NORMAL && customData.equals(net.minecraft.world.item.component.CustomModelData.DEFAULT)) {{
            stack.set(net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA,
                    new net.minecraft.world.item.component.CustomModelData(quality));
        }}

        if (quality == QualityHelper.NORMAL) {{
            return baseName;
        }}

        return Component.empty().append(prefix).append(baseName);
    }}
{finish_using_code}
    @Override
    public String getItemTypeKey() {{
        return "stardewcraft.type.crop";
    }}

    @Override
    public int getSellPrice(ItemStack stack) {{
        return getSellPrice(QualityHelper.getQuality(stack));
    }}

    @Override
    public boolean isFood() {{
        return {is_food_override};
    }}

    @Override
    public int getHealth(ItemStack stack) {{
        {get_health_body}
    }}

    @Override
    public int getEnergy(ItemStack stack) {{
        {get_energy_body}
    }}

    public static int getHealthRestoration(int quality) {{
        return HEALTH_BY_QUALITY[Math.max(0, Math.min(3, quality))];
    }}

    public static int getEnergyRestoration(int quality) {{
        return ENERGY_BY_QUALITY[Math.max(0, Math.min(3, quality))];
    }}

    public static int getSellPrice(int quality) {{
        return SELL_PRICE_BY_QUALITY[Math.max(0, Math.min(3, quality))];
    }}
}}
"""

        write_text(java_crop_item_dir / f"{item_class}.java", java_item)

        # --- Java: seed item ---
        seasons_set = crop.seasons
        season_check = " || ".join([f"season == {s}" for s in sorted(seasons_set)]) if seasons_set else "season == 0"
        # Seed sell price is not reliably available from the crop table on zh wiki.
        # Do not guess or infer it.
        seed_sell_price = 0

        java_seed = f"""package {item_package};

import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * {crop.cn_name}种子
 */
public class {seed_class} extends Item implements IStardewItem {{

    public {seed_class}(Item.Properties properties) {{
        super(properties);
    }}

    @Override
    public String getItemTypeKey() {{
        return "stardewcraft.type.seed";
    }}

    @Override
    public int getSellPrice(ItemStack stack) {{
        return {seed_sell_price};
    }}

    @Override
    public InteractionResult useOn(UseOnContext context) {{
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(pos);

        if (!isFarmland(clickedState)) {{
            return InteractionResult.PASS;
        }}

        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        if (!aboveState.isAir()) {{
            return InteractionResult.PASS;
        }}

        if (!level.isClientSide) {{
            int season = StardewTimeManager.get().getCurrentSeason();
            if (!({season_check})) {{
                if (context.getPlayer() != null) {{
                    context.getPlayer().displayClientMessage(
                            net.minecraft.network.chat.Component.translatable("stardewcraft.message.seed.wrong_season"),
                            true);
                }}
                return InteractionResult.FAIL;
            }}
        }}

        if (!level.isClientSide) {{
            level.setBlock(abovePos, ModBlocks.{upper_const(crop.slug)}_CROP.get().defaultBlockState(), 3);
            level.playSound(null, abovePos,
                    net.minecraft.sounds.SoundEvents.HOE_TILL,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    1.0F, 1.0F);
            context.getItemInHand().shrink(1);
        }}

        return InteractionResult.sidedSuccess(level.isClientSide);
    }}

    private boolean isFarmland(BlockState state) {{
        Block block = state.getBlock();
        if (block instanceof FarmBlock) {{
            return true;
        }}
        String blockId = block.builtInRegistryHolder().key().location().toString().toLowerCase();
        return blockId.contains("farmland");
    }}
}}
"""

        write_text(java_crop_item_dir / f"{seed_class}.java", java_seed)

        # --- Java: crop block ---
        can_regrow = crop.regrow_days is not None
        regrow_age = 2 if can_regrow else 0
        regrow_days = crop.regrow_days if crop.regrow_days is not None else 0
        is_in_season = " || ".join([f"timeManager.getCurrentSeason() == {s}" for s in sorted(seasons_set)]) if seasons_set else "timeManager.getCurrentSeason() == 0"

        java_block = f"""package com.stardew.craft.block.crop;

import com.stardew.craft.item.ModItems;
import com.stardew.craft.item.quality.QualityHelper;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Supplier;

/**
 * {crop.cn_name}作物
 */
public class {block_class} extends StardewCropBlock {{

    private static final int[] PHASE_DAYS = new int[]{{{crop.phase_days[0]}, {crop.phase_days[1]}, {crop.phase_days[2]}, {crop.phase_days[3]}}};

    public {block_class}() {{
        super(Properties.of()
                .mapColor(MapColor.PLANT)
                .pushReaction(PushReaction.DESTROY)
                .sound(SoundType.CROP));
    }}

    @Override
    protected Supplier<Item> getSeedsItem() {{
        return ModItems.{upper_const(crop.slug)}_SEEDS;
    }}

    @Override
    protected Supplier<Item> getCropItem() {{
        return ModItems.{upper_const(crop.slug)};
    }}

    @Override
    protected boolean isInSeason(Level level) {{
        if (level.isClientSide()) {{
            return true;
        }}
        StardewTimeManager timeManager = StardewTimeManager.get();
        return {is_in_season};
    }}

    @Override
    protected int[] getPhaseDays() {{
        return PHASE_DAYS;
    }}

    @Override
    protected ItemStack getHarvestItem(int quality) {{
        ItemStack stack = new ItemStack(ModItems.{upper_const(crop.slug)}.get());
        QualityHelper.setQuality(stack, quality);
        return stack;
    }}

    @Override
    protected boolean canRegrow() {{
        return {str(can_regrow).lower()};
    }}

    @Override
    protected int getRegrowAge() {{
        return {regrow_age};
    }}

    @Override
    protected int getRegrowDays() {{
        return {regrow_days};
    }}

    @Override
    public String getCropDisplayName() {{
        return "{crop.cn_name}";
    }}
}}
"""

        write_text(java_block_dir / f"{block_class}.java", java_block)

        # --- Resources: models & blockstates ---
        item_models_dir = assets_root / "models" / "item"
        blockstates_dir = assets_root / "blockstates"
        block_models_dir = assets_root / "models" / "block" / "crops" / season_folder

        write_text(
            item_models_dir / f"{crop.slug}.json",
            json.dumps(
                {
                    "parent": "minecraft:item/generated",
                    "textures": {"layer0": f"stardewcraft:item/crops/{season_folder}/{crop.slug}"},
                    "overrides": [
                        {"predicate": {"custom_model_data": 1}, "model": f"stardewcraft:item/{crop.slug}_silver"},
                        {"predicate": {"custom_model_data": 2}, "model": f"stardewcraft:item/{crop.slug}_gold"},
                        {"predicate": {"custom_model_data": 3}, "model": f"stardewcraft:item/{crop.slug}_iridium"},
                    ],
                },
                ensure_ascii=False,
                indent=2,
            )
            + "\n",
        )

        for q, star in [("silver", "silver_star"), ("gold", "gold_star"), ("iridium", "iridium_star")]:
            write_text(
                item_models_dir / f"{crop.slug}_{q}.json",
                json.dumps(
                    {
                        "parent": "minecraft:item/generated",
                        "textures": {
                            "layer0": f"stardewcraft:item/crops/{season_folder}/{crop.slug}",
                            "layer1": f"stardewcraft:item/quality/{star}",
                        },
                    },
                    ensure_ascii=False,
                    indent=2,
                )
                + "\n",
            )

        write_text(
            item_models_dir / f"{crop.slug}_seeds.json",
            json.dumps(
                {
                    "parent": "minecraft:item/generated",
                    "textures": {"layer0": f"stardewcraft:item/crops/{season_folder}/{crop.slug}_seeds"},
                },
                ensure_ascii=False,
                indent=2,
            )
            + "\n",
        )

        write_text(
            blockstates_dir / f"{crop.slug}_crop.json",
            json.dumps(
                {
                    "variants": {
                        "age=0": {"model": f"stardewcraft:block/crops/{season_folder}/{crop.slug}_stage0"},
                        "age=1": {"model": f"stardewcraft:block/crops/{season_folder}/{crop.slug}_stage1"},
                        "age=2": {"model": f"stardewcraft:block/crops/{season_folder}/{crop.slug}_stage2"},
                        "age=3": {"model": f"stardewcraft:block/crops/{season_folder}/{crop.slug}_stage3"},
                    }
                },
                ensure_ascii=False,
                indent=2,
            )
            + "\n",
        )

        for i in range(4):
            write_text(
                block_models_dir / f"{crop.slug}_stage{i}.json",
                json.dumps(
                    {
                        "parent": "minecraft:block/cross",
                        "textures": {"cross": f"stardewcraft:block/crops/{season_folder}/{crop.slug}_stage{i}"},
                    },
                    ensure_ascii=False,
                    indent=2,
                )
                + "\n",
            )

        # --- Loot tables ---
        loot_dir = data_root / "loot_tables" / "blocks"
        write_text(
            loot_dir / f"{crop.slug}_crop.json",
            json.dumps(
                {
                    "type": "minecraft:block",
                    "functions": [{"function": "minecraft:explosion_decay"}],
                    "pools": [
                        {
                            "bonus_rolls": 0.0,
                            "entries": [
                                {
                                    "type": "minecraft:alternatives",
                                    "children": [
                                        {
                                            "type": "minecraft:item",
                                            "conditions": [
                                                {
                                                    "condition": "minecraft:block_state_property",
                                                    "block": f"stardewcraft:{crop.slug}_crop",
                                                    "properties": {"age": "3"},
                                                }
                                            ],
                                            "name": f"stardewcraft:{crop.slug}",
                                        },
                                        {"type": "minecraft:item", "name": f"stardewcraft:{crop.slug}_seeds"},
                                    ],
                                }
                            ],
                            "rolls": 1.0,
                        }
                    ],
                },
                ensure_ascii=False,
                indent=2,
            )
            + "\n",
        )

        # --- Textures ---
        crop_folder = crops_root / crop.slug
        if not crop_folder.exists():
            missing_textures.append(f"crops/{crop.slug}/*")
        else:
            item_src = crop_folder / "item.png"
            if not item_src.exists():
                missing_textures.append(f"crops/{crop.slug}/item.png")
            else:
                copy_file(item_src, assets_root / "textures" / "item" / "crops" / season_folder / f"{crop.slug}.png")

            for i in range(4):
                stage_src = crop_folder / f"stage{i}.png"
                if not stage_src.exists():
                    missing_textures.append(f"crops/{crop.slug}/stage{i}.png")
                else:
                    copy_file(
                        stage_src,
                        assets_root / "textures" / "block" / "crops" / season_folder / f"{crop.slug}_stage{i}.png",
                    )

        seed_key = SEED_TEXTURE_OVERRIDES.get(crop.slug, crop.slug)
        seed_src = seedbag_root / f"{seed_key}.png"
        if seed_src.exists():
            copy_file(seed_src, assets_root / "textures" / "item" / "crops" / season_folder / f"{crop.slug}_seeds.png")
        else:
            # Some items on the crop page don't have seeds (or use special acquisition).
            missing_textures.append(f"seedbag/{seed_key}.png")

    if missing_textures:
        print("ERROR: Missing textures in resourcepack (not generating partial output):")
        for m in sorted(set(missing_textures)):
            print(" -", m)
        return 1

    # --- Update registries ---
    mod_items_path = java_item_base_dir / "ModItems.java"
    mod_blocks_path = modroot / "src" / "main" / "java" / "com" / "stardew" / "craft" / "block" / "ModBlocks.java"

    item_lines: list[str] = []
    item_lines.append("package com.stardew.craft.item;\n")
    item_lines.append("\n")
    item_lines.append("import com.stardew.craft.StardewCraft;\n")
    item_lines.append("import com.stardew.craft.item.tool.WateringCanItem;\n")

    # crop items/seeds are now in subpackages: com.stardew.craft.item.crop.<season|other>
    for crop in crops:
        folder = crop_item_folder_for_packages(crop)
        pkg = f"com.stardew.craft.item.crop.{folder}"
        base = pascal_case_from_slug(crop.slug)
        item_lines.append(f"import {pkg}.{base}Item;\n")
        item_lines.append(f"import {pkg}.{base}SeedItem;\n")

    item_lines.append("import net.minecraft.world.item.Item;\n")
    item_lines.append("import net.neoforged.neoforge.registries.DeferredItem;\n")
    item_lines.append("import net.neoforged.neoforge.registries.DeferredRegister;\n")
    item_lines.append("\n")
    item_lines.append("/**\n * 物品注册管理器\n */\n")
    item_lines.append("public class ModItems {\n")
    item_lines.append("    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(StardewCraft.MODID);\n")
    item_lines.append("    \n")
    item_lines.append("    // 工具 - 喷壶\n")
    item_lines.append("    public static final DeferredItem<Item> WATERING_CAN = ITEMS.register(\"watering_can\",\n")
    item_lines.append("            () -> new WateringCanItem(WateringCanItem.Tier.STARTER, new Item.Properties()));\n")
    item_lines.append("            \n")
    item_lines.append("    public static final DeferredItem<Item> COPPER_WATERING_CAN = ITEMS.register(\"copper_watering_can\",\n")
    item_lines.append("            () -> new WateringCanItem(WateringCanItem.Tier.COPPER, new Item.Properties()));\n")
    item_lines.append("            \n")
    item_lines.append("    public static final DeferredItem<Item> STEEL_WATERING_CAN = ITEMS.register(\"steel_watering_can\",\n")
    item_lines.append("            () -> new WateringCanItem(WateringCanItem.Tier.STEEL, new Item.Properties()));\n")
    item_lines.append("            \n")
    item_lines.append("    public static final DeferredItem<Item> GOLD_WATERING_CAN = ITEMS.register(\"gold_watering_can\",\n")
    item_lines.append("            () -> new WateringCanItem(WateringCanItem.Tier.GOLD, new Item.Properties()));\n")
    item_lines.append("\n")
    item_lines.append("    public static final DeferredItem<Item> IRIDIUM_WATERING_CAN = ITEMS.register(\"iridium_watering_can\",\n")
    item_lines.append("            () -> new WateringCanItem(WateringCanItem.Tier.IRIDIUM, new Item.Properties()));\n")
    item_lines.append("\n")

    item_lines.append("    // 种子\n")
    for crop in crops:
        item_lines.append(
            f"    public static final DeferredItem<Item> {upper_const(crop.slug)}_SEEDS = ITEMS.register(\"{crop.slug}_seeds\",\n"
        )
        item_lines.append(
            f"            () -> new {pascal_case_from_slug(crop.slug)}SeedItem(new Item.Properties().stacksTo(999)));\n"
        )
        item_lines.append("    \n")

    item_lines.append("    // 作物 (使用品质系统)\n")
    for crop in crops:
        item_lines.append(f"    public static final DeferredItem<Item> {upper_const(crop.slug)} = ITEMS.register(\"{crop.slug}\",\n")
        item_lines.append(
            f"            () -> new {pascal_case_from_slug(crop.slug)}Item(new Item.Properties().stacksTo(999)));\n"
        )
        item_lines.append("    \n")

    item_lines.append("}\n")

    mod_items_path.write_text("".join(item_lines), encoding="utf-8")

    block_lines: list[str] = []
    block_lines.append("package com.stardew.craft.block;\n\n")
    block_lines.append("import com.stardew.craft.StardewCraft;\n")
    block_lines.append("import net.minecraft.world.level.block.Block;\n")
    block_lines.append("import net.neoforged.neoforge.registries.DeferredBlock;\n")
    block_lines.append("import net.neoforged.neoforge.registries.DeferredRegister;\n\n")
    block_lines.append("/**\n * 方块注册管理器\n */\n")
    block_lines.append("public class ModBlocks {\n")
    block_lines.append("    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(StardewCraft.MODID);\n\n")
    block_lines.append("    // 作物方块\n")
    for crop in crops:
        block_lines.append(
            f"    public static final DeferredBlock<Block> {upper_const(crop.slug)}_CROP = BLOCKS.register(\"{crop.slug}_crop\",\n"
        )
        block_lines.append(
            f"            () -> new com.stardew.craft.block.crop.{pascal_case_from_slug(crop.slug)}CropBlock());\n\n"
        )

    block_lines.append("    public static final DeferredBlock<Block> DEAD_CROP = BLOCKS.register(\"dead_crop\",\n")
    block_lines.append("            () -> new com.stardew.craft.block.crop.DeadCropBlock(Block.Properties.of()\n")
    block_lines.append("                    .mapColor(net.minecraft.world.level.material.MapColor.PLANT)\n")
    block_lines.append("                    .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY)\n")
    block_lines.append("                    .sound(net.minecraft.world.level.block.SoundType.GRASS)\n")
    block_lines.append("                    .noCollission()\n")
    block_lines.append("                    .instabreak()));\n")
    block_lines.append("}\n")

    mod_blocks_path.write_text("".join(block_lines), encoding="utf-8")

    # --- Update language files (add missing keys only) ---
    zh_path = assets_root / "lang" / "zh_cn.json"
    en_path = assets_root / "lang" / "en_us.json"

    zh_updates: dict[str, str] = {}
    en_updates: dict[str, str] = {}

    for crop in crops:
        # names
        zh_updates[f"item.stardewcraft.{crop.slug}"] = crop.cn_name
        zh_updates[f"item.stardewcraft.{crop.slug}_seeds"] = f"{crop.cn_name}种子"
        zh_updates[f"block.stardewcraft.{crop.slug}_crop"] = f"{crop.cn_name}作物"

        en_name = pascal_case_from_slug(crop.slug)
        en_updates[f"item.stardewcraft.{crop.slug}"] = en_name
        en_updates[f"item.stardewcraft.{crop.slug}_seeds"] = f"{en_name} Seeds"
        en_updates[f"block.stardewcraft.{crop.slug}_crop"] = f"{en_name} Crop"

        # desc/flavor
        desc, flavor = split_desc_flavor(crop.cn_desc)
        if desc:
            zh_updates[f"item.stardewcraft.{crop.slug}.desc"] = desc
        if flavor:
            zh_updates[f"item.stardewcraft.{crop.slug}.flavor"] = flavor

        season_zh = seasons_to_zh(crop.seasons)
        if season_zh:
            zh_updates[f"item.stardewcraft.{crop.slug}_seeds.desc"] = f"{season_zh}天种下。{crop.growth_days}天后产出。"

        season_en = seasons_to_en(crop.seasons)
        if season_en:
            en_updates[f"item.stardewcraft.{crop.slug}_seeds.desc"] = f"Plant in {season_en}. Takes {crop.growth_days} days to mature."

    update_lang_json(zh_path, zh_updates)
    update_lang_json(en_path, en_updates)

    print(f"Generated {len(crops)} crops.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
