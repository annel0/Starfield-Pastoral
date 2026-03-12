package com.stardew.craft;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.capability.UtilityAutomationCapabilities;
import com.stardew.craft.entity.ModEntities;
import com.stardew.craft.effect.ModMobEffects;
import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.item.SpecificBaitItem;
import com.stardew.craft.item.StardewQualityItem;
import com.stardew.craft.sound.ModSounds;
import com.stardew.craft.item.artisan.ArtisanDrinkItem;
import com.stardew.craft.item.artisan.DehydratorIngredientHelper;
import com.stardew.craft.item.artisan.SmokedFishItem;
import com.stardew.craft.client.weapon.WeaponShaderRegistry;
import com.stardew.craft.blockentity.ModBlockEntities;
import com.stardew.craft.network.PacketHandler;
import com.stardew.craft.event.WildTreeChopEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(StardewCraft.MODID)
public class StardewCraft {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "stardewcraft";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "stardewcraft" namespace
    @SuppressWarnings("null")
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Stardew Valley专用创造标签
    @SuppressWarnings("null")
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> STARDEW_TAB = CREATIVE_MODE_TABS.register("stardew_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.stardewcraft.stardew"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.PARSNIP_SEEDS.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                // 野草（按季节分类）
                // 春季野草
                output.accept(ModItems.WILD_WEEDS_SPRING_0.get());
                output.accept(ModItems.WILD_WEEDS_SPRING_1.get());
                output.accept(ModItems.WILD_WEEDS_SPRING_2.get());
                // 夏季野草
                output.accept(ModItems.WILD_WEEDS_SUMMER_0.get());
                output.accept(ModItems.WILD_WEEDS_SUMMER_1.get());
                output.accept(ModItems.WILD_WEEDS_SUMMER_2.get());
                // 秋季野草
                output.accept(ModItems.WILD_WEEDS_FALL_0.get());
                output.accept(ModItems.WILD_WEEDS_FALL_1.get());
                output.accept(ModItems.WILD_WEEDS_FALL_2.get());
                // 冬季野草
                output.accept(ModItems.WILD_WEEDS_WINTER_0.get());
                output.accept(ModItems.PASTURE_GRASS.get());
                output.accept(ModItems.BLUE_PASTURE_GRASS.get());

                // 矿井
                output.accept(ModItems.MINE_BARRIER.get());

                output.accept(ModItems.EARTH_SHALE.get());
                output.accept(ModItems.FROST_GNEISS.get());
                output.accept(ModItems.LAVA_BASALT.get());

                output.accept(ModItems.DARK_EARTH_SHALE.get());
                output.accept(ModItems.DARK_FROST_GNEISS.get());
                output.accept(ModItems.DARK_LAVA_BASALT.get());

                // 主石头变体
                output.accept(ModItems.EARTH_SHALE_SLAB.get());
                output.accept(ModItems.EARTH_SHALE_STAIRS.get());
                output.accept(ModItems.EARTH_SHALE_WALL.get());
                output.accept(ModItems.FROST_GNEISS_SLAB.get());
                output.accept(ModItems.FROST_GNEISS_STAIRS.get());
                output.accept(ModItems.FROST_GNEISS_WALL.get());
                output.accept(ModItems.LAVA_BASALT_SLAB.get());
                output.accept(ModItems.LAVA_BASALT_STAIRS.get());
                output.accept(ModItems.LAVA_BASALT_WALL.get());
                output.accept(ModItems.DARK_EARTH_SHALE_SLAB.get());
                output.accept(ModItems.DARK_EARTH_SHALE_STAIRS.get());
                output.accept(ModItems.DARK_EARTH_SHALE_WALL.get());
                output.accept(ModItems.DARK_FROST_GNEISS_SLAB.get());
                output.accept(ModItems.DARK_FROST_GNEISS_STAIRS.get());
                output.accept(ModItems.DARK_FROST_GNEISS_WALL.get());
                output.accept(ModItems.DARK_LAVA_BASALT_SLAB.get());
                output.accept(ModItems.DARK_LAVA_BASALT_STAIRS.get());
                output.accept(ModItems.DARK_LAVA_BASALT_WALL.get());

                output.accept(ModItems.BANDED_MARBLE.get());
                output.accept(ModItems.LIMESTONE.get());
                output.accept(ModItems.MOSSY_SANDSTONE.get());
                output.accept(ModItems.CRACKED_SLATE.get());
                output.accept(ModItems.SCORIA.get());
                output.accept(ModItems.SALT_ROCK.get());

                // 装饰石材变体
                output.accept(ModItems.BANDED_MARBLE_SLAB.get());
                output.accept(ModItems.BANDED_MARBLE_STAIRS.get());
                output.accept(ModItems.BANDED_MARBLE_WALL.get());
                output.accept(ModItems.LIMESTONE_SLAB.get());
                output.accept(ModItems.LIMESTONE_STAIRS.get());
                output.accept(ModItems.LIMESTONE_WALL.get());
                output.accept(ModItems.MOSSY_SANDSTONE_SLAB.get());
                output.accept(ModItems.MOSSY_SANDSTONE_STAIRS.get());
                output.accept(ModItems.MOSSY_SANDSTONE_WALL.get());
                output.accept(ModItems.CRACKED_SLATE_SLAB.get());
                output.accept(ModItems.CRACKED_SLATE_STAIRS.get());
                output.accept(ModItems.CRACKED_SLATE_WALL.get());
                output.accept(ModItems.SCORIA_SLAB.get());
                output.accept(ModItems.SCORIA_STAIRS.get());
                output.accept(ModItems.SCORIA_WALL.get());
                output.accept(ModItems.SALT_ROCK_SLAB.get());
                output.accept(ModItems.SALT_ROCK_STAIRS.get());
                output.accept(ModItems.SALT_ROCK_WALL.get());

                output.accept(ModItems.ELEVATOR.get());
                output.accept(ModItems.MINE_LADDER.get());

                output.accept(ModItems.EARTH_COPPER_ORE.get());
                output.accept(ModItems.FROST_COPPER_ORE.get());
                output.accept(ModItems.LAVA_COPPER_ORE.get());

                output.accept(ModItems.EARTH_IRON_ORE.get());
                output.accept(ModItems.FROST_IRON_ORE.get());
                output.accept(ModItems.LAVA_IRON_ORE.get());

                output.accept(ModItems.EARTH_GOLD_ORE.get());
                output.accept(ModItems.FROST_GOLD_ORE.get());
                output.accept(ModItems.LAVA_GOLD_ORE.get());

                output.accept(ModItems.EARTH_IRIDIUM_ORE.get());
                output.accept(ModItems.FROST_IRIDIUM_ORE.get());
                output.accept(ModItems.LAVA_IRIDIUM_ORE.get());

                output.accept(ModItems.EARTH_COAL_ORE.get());
                output.accept(ModItems.FROST_COAL_ORE.get());
                output.accept(ModItems.LAVA_COAL_ORE.get());

                // 直接采集矿物（放置为方块）
                output.accept(ModItems.QUARTZ.get());
                output.accept(ModItems.EARTH_CRYSTAL.get());
                output.accept(ModItems.FROZEN_TEAR.get());
                output.accept(ModItems.FIRE_QUARTZ.get());

                // 宝石矿石（挖掉掉宝石）
                output.accept(ModItems.AMETHYST_ORE.get());
                output.accept(ModItems.AQUAMARINE_ORE.get());
                output.accept(ModItems.DIAMOND_ORE.get());
                output.accept(ModItems.EMERALD_ORE.get());
                output.accept(ModItems.JADE_ORE.get());
                output.accept(ModItems.RUBY_ORE.get());
                output.accept(ModItems.TOPAZ_ORE.get());

                // 野生树（橡树原型）建筑组件
                output.accept(ModBlocks.WILD_OAK_TRUNK0.get());
                output.accept(ModBlocks.WILD_OAK_TRUNK1.get());
                output.accept(ModBlocks.WILD_OAK_BRANCH1.get());
                output.accept(ModBlocks.WILD_OAK_BRANCH2.get());
                output.accept(ModBlocks.WILD_OAK_LEAVES.get());

                // 野生树（枫树）建筑组件
                output.accept(ModBlocks.WILD_MAPLE_TRUNK0.get());
                output.accept(ModBlocks.WILD_MAPLE_TRUNK1.get());
                output.accept(ModBlocks.WILD_MAPLE_BRANCH1.get());
                output.accept(ModBlocks.WILD_MAPLE_BRANCH2.get());
                output.accept(ModBlocks.WILD_MAPLE_LEAVES.get());

                // 野生树（松树）建筑组件
                output.accept(ModBlocks.WILD_PINE_TRUNK0.get());
                output.accept(ModBlocks.WILD_PINE_TRUNK1.get());
                output.accept(ModBlocks.WILD_PINE_BRANCH1.get());
                output.accept(ModBlocks.WILD_PINE_BRANCH2.get());
                output.accept(ModBlocks.WILD_PINE_LEAVES.get());

                // 野生树（桃花心木）建筑组件
                output.accept(ModBlocks.WILD_MAHOGANY_TRUNK0.get());
                output.accept(ModBlocks.WILD_MAHOGANY_TRUNK1.get());
                output.accept(ModBlocks.WILD_MAHOGANY_BRANCH1.get());
                output.accept(ModBlocks.WILD_MAHOGANY_BRANCH2.get());
                output.accept(ModBlocks.WILD_MAHOGANY_LEAVES.get());

                // 野生树（神秘树）建筑组件
                output.accept(ModBlocks.WILD_MYSTIC_TREE_TRUNK0.get());
                output.accept(ModBlocks.WILD_MYSTIC_TREE_TRUNK1.get());
                output.accept(ModBlocks.WILD_MYSTIC_TREE_BRANCH1.get());
                output.accept(ModBlocks.WILD_MYSTIC_TREE_BRANCH2.get());
                output.accept(ModBlocks.WILD_MYSTIC_TREE_LEAVES.get());

				// 树种子
				output.accept(ModItems.ACORN.get());
				output.accept(ModItems.MAPLE_SEED.get());
				output.accept(ModItems.PINE_CONE.get());
				output.accept(ModItems.MAHOGANY_SEED.get());
				output.accept(ModItems.MYSTIC_TREE_SEED.get());

                // 水壶
                output.accept(ModItems.WATERING_CAN.get());
                output.accept(ModItems.COPPER_WATERING_CAN.get());
                output.accept(ModItems.STEEL_WATERING_CAN.get());
                output.accept(ModItems.GOLD_WATERING_CAN.get());
                output.accept(ModItems.IRIDIUM_WATERING_CAN.get());

                // 洒水器
                output.accept(ModItems.SPRINKLER.get());
                output.accept(ModItems.QUALITY_SPRINKLER.get());
                output.accept(ModItems.IRIDIUM_SPRINKLER.get());

                // 锄头
                output.accept(ModItems.HOE.get());
                output.accept(ModItems.COPPER_HOE.get());
                output.accept(ModItems.STEEL_HOE.get());
                output.accept(ModItems.GOLD_HOE.get());
                output.accept(ModItems.IRIDIUM_HOE.get());

                // 斧头
                output.accept(ModItems.AXE.get());
                output.accept(ModItems.COPPER_AXE.get());
                output.accept(ModItems.STEEL_AXE.get());
                output.accept(ModItems.GOLD_AXE.get());
                output.accept(ModItems.IRIDIUM_AXE.get());

                // 镐子
                output.accept(ModItems.PICKAXE.get());
                output.accept(ModItems.COPPER_PICKAXE.get());
                output.accept(ModItems.STEEL_PICKAXE.get());
                output.accept(ModItems.GOLD_PICKAXE.get());
                output.accept(ModItems.IRIDIUM_PICKAXE.get());

				// 镰刀
				output.accept(ModItems.SCYTHE.get());
				output.accept(ModItems.GOLDEN_SCYTHE.get());
				output.accept(ModItems.IRIDIUM_SCYTHE.get());
                output.accept(ModItems.MILK_PAIL.get());
                output.accept(ModItems.SHEARS.get());

                // 钓鱼竿
                output.accept(ModItems.FISHING_ROD.get());
                output.accept(ModItems.TRAINING_ROD.get());
                output.accept(ModItems.FIBERGLASS_ROD.get());
                output.accept(ModItems.IRIDIUM_ROD.get());
                output.accept(ModItems.ADVANCED_IRIDIUM_ROD.get());

                // 武器 - 剑类
                output.accept(ModItems.RUSTY_SWORD.get());
                output.accept(ModItems.STEEL_SMALLSWORD.get());
                output.accept(ModItems.CARVING_KNIFE.get());
                output.accept(ModItems.IRON_DIRK.get());
                output.accept(ModItems.WIND_SPIRE.get());
                output.accept(ModItems.ELF_BLADE.get());
                output.accept(ModItems.BURGLARS_SHANK.get());
                output.accept(ModItems.CRYSTAL_DAGGER.get());
                output.accept(ModItems.SHADOW_DAGGER.get());
                output.accept(ModItems.WICKED_KRIS.get());
                output.accept(ModItems.GALAXY_DAGGER.get());
                output.accept(ModItems.DWARF_DAGGER.get());
                output.accept(ModItems.IRIDIUM_NEEDLE.get());
                output.accept(ModItems.INFINITY_DAGGER.get());
                output.accept(ModItems.BROKEN_TRIDENT.get());
                output.accept(ModItems.FEMUR.get());
                output.accept(ModItems.WOODEN_BLADE.get());
                output.accept(ModItems.PIRATE_SWORD.get());
                output.accept(ModItems.SILVER_SABER.get());
                output.accept(ModItems.CUTLASS.get());
                output.accept(ModItems.FOREST_SWORD.get());
                output.accept(ModItems.IRON_EDGE.get());
                output.accept(ModItems.MEOWMERE.get());
                output.accept(ModItems.BONE_SWORD.get());
                output.accept(ModItems.CLAYMORE.get());
                output.accept(ModItems.NEPTUNES_GLAIVE.get());
                output.accept(ModItems.TEMPLARS_BLADE.get());
                output.accept(ModItems.INSECT_HEAD.get());
                output.accept(ModItems.OBSIDIAN_EDGE.get());
                output.accept(ModItems.OSSIFIED_BLADE.get());
                output.accept(ModItems.HOLY_BLADE.get());
                output.accept(ModItems.TEMPERED_BROADSWORD.get());
                output.accept(ModItems.YETI_TOOTH.get());
                output.accept(ModItems.STEEL_FALCHION.get());
                output.accept(ModItems.DARK_SWORD.get());
                output.accept(ModItems.LAVA_KATANA.get());
                output.accept(ModItems.DRAGONTOOTH_CUTLASS.get());
                output.accept(ModItems.DWARF_SWORD.get());
                output.accept(ModItems.GALAXY_SWORD.get());
                output.accept(ModItems.INFINITY_BLADE.get());
                // 种子/作物：自动把所有已注册作物系统物品加入标签
                java.util.List<Item> fruitItems = new java.util.ArrayList<>();
                java.util.List<Item> forageItems = new java.util.ArrayList<>();
                java.util.List<Item> cookingIngredientItems = new java.util.ArrayList<>();
                for (var holder : ModItems.ITEMS.getEntries()) {
                    Item item = holder.get();
                    if (!(item instanceof IStardewItem stardewItem)) {
                        continue;
                    }

                    if (isHiddenTintedBaseItem(item)) {
                        // Keep creative tab focused on flavored/targeted variants, not empty placeholders.
                        continue;
                    }

                    String typeKey = stardewItem.getItemTypeKey();
                    if ("stardewcraft.type.seed".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.crop_seed".equals(typeKey)) {
                        // 咖啡豆等既是作物又是种子的物品，显示所有品质变体
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                    } else if ("stardewcraft.type.resource".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.mineral".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.artifact".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.artifact_quality".equals(typeKey)) {
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                    } else if ("stardewcraft.type.artisan_goods".equals(typeKey)) {
                        if (item instanceof ArtisanDrinkItem drink && drink.supportsQuality()) {
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                        } else if (item instanceof SmokedFishItem) {
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                        } else {
                            output.accept(item);
                        }
                    } else if ("stardewcraft.type.artisan_animal_quality".equals(typeKey)) {
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                    } else if ("stardewcraft.type.fishing".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.utility".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.furniture".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.cooking".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.fruit".equals(typeKey)) {
                        fruitItems.add(item);
                    } else if ("stardewcraft.type.forage".equals(typeKey)) {
                        forageItems.add(item);
                    } else if ("stardewcraft.type.cooking_ingredient".equals(typeKey)) {
                        cookingIngredientItems.add(item);
                    } else if ("stardewcraft.type.tool".equals(typeKey) || typeKey.startsWith("stardewcraft.tool.")) {
                        output.accept(item);
                    } else if ("stardewcraft.type.craftable".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.fertilizer".equals(typeKey)) {
                        output.accept(item);
                    } else if ("stardewcraft.type.animal_product".equals(typeKey)) {
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                    } else if ("stardewcraft.type.crop".equals(typeKey)) {
                        Integer colorCount = getFlowerColorVariantCount(item);
                        if (colorCount != null && colorCount > 0) {
                            for (int color = 0; color < colorCount; color++) {
                                output.accept(createFlowerVariantItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL, color));
                                output.accept(createFlowerVariantItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER, color));
                                output.accept(createFlowerVariantItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD, color));
                                output.accept(createFlowerVariantItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM, color));
                            }
                        } else {
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                            output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                        }
                    } else if ("stardewcraft.type.fish".equals(typeKey)) {
                        // 鱼类也支持品质变体
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                    } else if ("stardewcraft.type.crabpot".equals(typeKey)) {
                        // 蟹笼物品也支持品质变体
                        // 普通蟹笼: 普通品质
                        // 高级鱼饵: 可获得银星
                        // 采集贝类(蛤蜊、鸟蛤、贻贝、牡蛎): 可获得金星和铱星
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                    } else if ("stardewcraft.type.legendary_fish".equals(typeKey)) {
                        // 传说鱼类也支持品质变体
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                        output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
                    } else if ("stardewcraft.type.misc".equals(typeKey)) {
                        // 杂项物品（藻类等）不支持品质
                        output.accept(item);
                    } else if ("stardewcraft.type.trash".equals(typeKey)) {
                        // 垃圾物品不支持品质
                        output.accept(item);
                    }
                }

                addGroupedCategoryItems(output, cookingIngredientItems, false);
                addGroupedCategoryItems(output, fruitItems, true);
                addGroupedCategoryItems(output, forageItems, true);

                addPreserveVariants(output);
                addSpecificBaitVariants(output);
            }).build());

    private static boolean isHiddenTintedBaseItem(Item item) {
        return item == ModItems.TARGETED_BAIT.get()
                || item == ModItems.JELLY.get()
                || item == ModItems.PICKLES.get()
                || item == ModItems.ROE.get()
                || item == ModItems.AGED_ROE.get()
                || item == ModItems.CAVIAR.get()
                || item == ModItems.DRIED_FRUIT.get()
                || item == ModItems.DRIED_MUSHROOMS.get();
    }
    
    /**
     * 创建带品质的物品
     */
    @SuppressWarnings("null")
    private static ItemStack createQualityItem(Item item, int quality) {
        @SuppressWarnings("null")
        ItemStack stack = new ItemStack(item);
        com.stardew.craft.item.quality.QualityHelper.setQuality(stack, quality);
        return stack;
    }

    @SuppressWarnings("null")
    private static void addGroupedCategoryItems(CreativeModeTab.Output output, java.util.List<Item> items, boolean forceQualityVariants) {
        items.sort(java.util.Comparator.comparing(i -> BuiltInRegistries.ITEM.getKey(i).getPath()));
        for (Item item : items) {
            boolean supportsQuality = item instanceof StardewQualityItem qualityItem && qualityItem.supportsQuality();
            if (forceQualityVariants || supportsQuality) {
                output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.NORMAL));
                output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.SILVER));
                output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.GOLD));
                output.accept(createQualityItem(item, com.stardew.craft.item.quality.QualityHelper.IRIDIUM));
            } else {
                output.accept(item);
            }
        }
    }

    @SuppressWarnings("null")
    private static ItemStack createFlowerVariantItem(Item item, int quality, int color) {
        @SuppressWarnings("null")
        ItemStack stack = new ItemStack(item);
        com.stardew.craft.item.quality.QualityHelper.setQuality(stack, quality);
        @SuppressWarnings("null")
        var customData = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY);
        var tag = customData.copyTag();
        tag.putInt("FlowerColor", Math.max(0, color));
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag));
        int cmd = 100 + (quality * 10) + Math.max(0, color);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA,
                new net.minecraft.world.item.component.CustomModelData(cmd));
        return stack;
    }

    private static Integer getFlowerColorVariantCount(Item item) {
        if (item == ModItems.BLUE_JAZZ.get()) return 6;
        if (item == ModItems.POPPY.get()) return 3;
        if (item == ModItems.TULIP.get()) return 5;
        if (item == ModItems.SUMMER_SPANGLE.get()) return 6;
        if (item == ModItems.FAIRY_ROSE.get()) return 6;
        return null;
    }

    private static void addPreserveVariants(CreativeModeTab.Output output) {
        for (var holder : ModItems.ITEMS.getEntries()) {
            Item item = holder.get();
            if (!(item instanceof IStardewItem stardewItem)) {
                continue;
            }
            var id = BuiltInRegistries.ITEM.getKey(item);
            if (!com.stardew.craft.item.artisan.PreservesIngredientDataManager.hasData(id)) {
                continue;
            }

            String typeKey = stardewItem.getItemTypeKey();
            if (isPreserveCropIngredient(typeKey)) {
                var preserveType = com.stardew.craft.item.artisan.PreservesCropTypeHelper.getCropPreserveType(id);
                if (preserveType != null) {
                    addPreserveVariant(output, preserveType, item, getPreserveBaseItem(preserveType));
                }
            } else if (isPreserveFishIngredient(typeKey)) {
                addPreserveVariant(output, com.stardew.craft.item.artisan.PreserveType.ROE, item, ModItems.ROE.get());
                addPreserveVariant(output, com.stardew.craft.item.artisan.PreserveType.AGED_ROE, item, ModItems.AGED_ROE.get());
                if (isSturgeon(id)) {
                    addPreserveVariant(output, com.stardew.craft.item.artisan.PreserveType.CAVIAR, item, ModItems.CAVIAR.get());
                }
            }

            if (isDehydratorFruitIngredient(typeKey, id)) {
                addPreserveVariant(output, com.stardew.craft.item.artisan.PreserveType.DRIED_FRUIT, item, ModItems.DRIED_FRUIT.get());
            } else if (isDehydratorMushroomIngredient(id)) {
                addPreserveVariant(output, com.stardew.craft.item.artisan.PreserveType.DRIED_MUSHROOMS, item, ModItems.DRIED_MUSHROOMS.get());
            }
        }
    }

    @SuppressWarnings("null")
    private static void addSpecificBaitVariants(CreativeModeTab.Output output) {
        int variantIndex = 0;
        for (var holder : ModItems.ITEMS.getEntries()) {
            Item item = holder.get();
            if (!(item instanceof IStardewItem stardewItem)) {
                continue;
            }

            String typeKey = stardewItem.getItemTypeKey();
            if (!isPreserveFishIngredient(typeKey)) {
                continue;
            }

            // Show pre-configured Specific Bait entries in creative tab so they are discoverable.
            ItemStack fishStack = new ItemStack(item);
            if (fishStack.isEmpty()) {
                continue;
            }
            ItemStack baitStack = SpecificBaitItem.createForFish(fishStack, 1);
            SpecificBaitItem.applyCreativeVariantMarker(baitStack, variantIndex);
            output.accept(baitStack);
            variantIndex++;
        }
    }

    private static boolean isPreserveCropIngredient(String typeKey) {
        return "stardewcraft.type.crop".equals(typeKey);
    }

    private static boolean isPreserveFishIngredient(String typeKey) {
        return "stardewcraft.type.fish".equals(typeKey)
                || "stardewcraft.type.crabpot".equals(typeKey)
                || "stardewcraft.type.legendary_fish".equals(typeKey);
    }

    private static boolean isDehydratorFruitIngredient(String typeKey, net.minecraft.resources.ResourceLocation id) {
        if (!"stardewcraft.type.crop".equals(typeKey)) {
            return false;
        }
        if (isGrape(id)) {
            return false;
        }
        return DehydratorIngredientHelper.isFruitCrop(id);
    }

    private static boolean isDehydratorMushroomIngredient(net.minecraft.resources.ResourceLocation id) {
        return DehydratorIngredientHelper.isMushroom(id);
    }

    private static boolean isSturgeon(net.minecraft.resources.ResourceLocation id) {
        return id != null && "sturgeon".equals(id.getPath());
    }

    private static boolean isGrape(net.minecraft.resources.ResourceLocation id) {
        return id != null && "grape".equals(id.getPath());
    }

    @SuppressWarnings("null")
    private static void addPreserveVariant(CreativeModeTab.Output output,
                                           com.stardew.craft.item.artisan.PreserveType type,
                                           Item ingredient,
                                           Item baseItem) {
        ItemStack ingredientStack = new ItemStack(ingredient);
        ItemStack resultStack = new ItemStack(baseItem);
        com.stardew.craft.item.artisan.PreservesItem.createFlavored(type, ingredientStack, resultStack);
        output.accept(resultStack);
    }

    private static Item getPreserveBaseItem(com.stardew.craft.item.artisan.PreserveType type) {
        return switch (type) {
            case JELLY -> ModItems.JELLY.get();
            case PICKLES -> ModItems.PICKLES.get();
            case ROE -> ModItems.ROE.get();
            case AGED_ROE -> ModItems.AGED_ROE.get();
            case CAVIAR -> ModItems.CAVIAR.get();
            case DRIED_FRUIT -> ModItems.DRIED_FRUIT.get();
            case DRIED_MUSHROOMS -> ModItems.DRIED_MUSHROOMS.get();
        };
    }

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public StardewCraft(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(PacketHandler::register);
        modEventBus.addListener(WeaponShaderRegistry::onRegisterShadersSafe);
        modEventBus.addListener(UtilityAutomationCapabilities::registerCapabilities);
        modEventBus.addListener(ModEntities::onEntityAttributeCreation);

        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
        
        // 注册作物系统的物品和方块
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
		ModEntities.ENTITY_TYPES.register(modEventBus);
		ModSounds.SOUND_EVENTS.register(modEventBus);

		// 注册自定义 Buff（状态效果）
		ModMobEffects.MOB_EFFECTS.register(modEventBus);
		
		// 注册自定义粒子
		com.stardew.craft.weather.ModParticles.PARTICLES.register(modEventBus);
		
		// 注册菜单类型
		com.stardew.craft.menu.ModMenuTypes.MENU_TYPES.register(modEventBus);


        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (StardewCraft) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
        
        // 手动注册事件（确保事件被正确注册）
        NeoForge.EVENT_BUS.register(com.stardew.craft.event.MinePickaxeEvents.class);
        NeoForge.EVENT_BUS.register(WildTreeChopEvents.class);
        NeoForge.EVENT_BUS.register(com.stardew.craft.combat.WeaponCombatEvents.class);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.register(com.stardew.craft.client.ModClientEvents.class);
        }

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("Initializing StardewCraft systems...");
        
        // 初始化维度和作物系统
        event.enqueueWork(() -> {
            com.stardew.craft.core.ModDimensions.register();

        });
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SuppressWarnings("null")
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");

        var server = event.getServer();
        LOGGER.info("[VALLEY_MAP] Startup: trying prebuilt region install");
        var result = com.stardew.craft.dimension.StardewValleyPrebuiltRegionInstaller.installIfAvailable(server);
        if (result == com.stardew.craft.dimension.StardewValleyPrebuiltRegionInstaller.InstallResult.INSTALLED
            || result == com.stardew.craft.dimension.StardewValleyPrebuiltRegionInstaller.InstallResult.ALREADY_PRESENT) {
            var stardewLevel = server.getLevel(com.stardew.craft.core.ModDimensions.STARDEW_VALLEY);
            if (stardewLevel != null) {
                com.stardew.craft.dimension.StardewValleyMapBootstrap.markAsPreGenerated(stardewLevel);
            } else {
                LOGGER.info("[VALLEY_MAP] Stardew level not loaded at startup, will mark pre-generated on first travel.");
            }
        } else {
            LOGGER.error("[VALLEY_MAP] Prebuilt region package missing or invalid. Stardew Valley travel will be blocked until fixed.");
        }
    }
}
