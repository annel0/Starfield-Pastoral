package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import com.stardew.craft.shop.MonsterSlayerGoalRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.Collection;
import java.util.Set;

/**
 * 矿井怪物掉落事件处理器
 *
 * 当矿井维度中带有 sd_mob_* 标签的怪物死亡时，
 * 清除原版掉落并按 SDV 概率表生成掉落物。
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
@SuppressWarnings("null")
public class MineMonsterDropHandler {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        if (!serverLevel.dimension().equals(ModMiningDimensions.STARDEW_MINING)) return;

        Set<String> tags = entity.getTags();
        if (tags.stream().noneMatch(t -> t.startsWith("sd_mob_"))) return;

        // 清除原版掉落
        Collection<ItemEntity> drops = event.getDrops();
        drops.clear();

        RandomSource random = serverLevel.getRandom();

        // 根据怪物类型生成SDV掉落
        if (tags.contains("sd_mob_slime")) {
            dropSlime(drops, entity, random, tags);
        } else if (tags.contains("sd_mob_bat")) {
            dropBat(drops, entity, random, tags);
        } else if (tags.contains("sd_mob_fly")) {
            dropFly(drops, entity, random);
        } else if (tags.contains("sd_mob_grub")) {
            dropGrub(drops, entity, random);
        } else if (tags.contains("sd_mob_bug")) {
            dropBug(drops, entity, random);
        } else if (tags.contains("sd_mob_dust_sprite")) {
            dropDustSprite(drops, entity, random);
        } else if (tags.contains("sd_mob_skeleton")) {
            dropSkeleton(drops, entity, random);
        } else if (tags.contains("sd_mob_ghost")) {
            dropGhost(drops, entity, random);
        } else if (tags.contains("sd_mob_crab")) {
            dropCrab(drops, entity, random);
        } else if (tags.contains("sd_mob_golem")) {
            dropGolem(drops, entity, random);
        } else if (tags.contains("sd_mob_shadow")) {
            dropShadow(drops, entity, random);
        } else if (tags.contains("sd_mob_duggy")) {
            dropDuggy(drops, entity, random);
        } else if (tags.contains("sd_mob_metal_head")) {
            dropMetalHead(drops, entity, random);
        } else if (tags.contains("sd_mob_squid")) {
            dropSquidKid(drops, entity, random);
        } else if (tags.contains("sd_mob_mummy")) {
            dropMummy(drops, entity, random);
        } else if (tags.contains("sd_mob_serpent")) {
            dropSerpent(drops, entity, random);
        }

        // ---- Monster Slayer kill tracking (SDV Gil goals) ----
        if (event.getSource() != null && event.getSource().getEntity() instanceof ServerPlayer player) {
            for (String tag : tags) {
                String goalKey = MonsterSlayerGoalRegistry.getGoalKeyForTag(tag);
                if (goalKey != null) {
                    PlayerStardewData data = PlayerDataManager.getPlayerData(player);
                    data.addMonsterKills(goalKey, 1);
                    break; // only count once per kill
                }
            }
            // ---- Quest: monster slain ----
            for (String tag : tags) {
                if (tag.startsWith("sd_mob_")) {
                    com.stardew.craft.quest.StardewQuestEvents.fireMonsterSlain(player, tag);
                }
            }
        }
    }

    // ======================== 掉落表（SDV 原版精确概率）========================
    // 数据源：MINE_MONSTER_SYSTEM_DESIGN.md §4.3 — parseMonsterInfo + getExtraDropItems
    // 2026-04-08 修订：补全所有矮人卷轴、鱿鱼墨汁、绿藻、南瓜汤、稀有圆盘等

    private static void dropSlime(Collection<ItemEntity> drops, LivingEntity e, RandomSource r, Set<String> tags) {
        // 75% slime
        if (r.nextFloat() < 0.75f) addDrop(drops, e, ModItems.SLIME_ITEM.get(), 1);
        // 5% 额外 slime
        if (r.nextFloat() < 0.05f) addDrop(drops, e, ModItems.SLIME_ITEM.get(), 1);
        // 10% green_algae (tier 1 only)
        if (!tags.contains("sd_tier_2") && !tags.contains("sd_tier_3")) {
            if (r.nextFloat() < 0.10f) addDrop(drops, e, ModItems.GREEN_ALGAE.get(), 1);
        }
        // 15% sap (所有层级)
        if (r.nextFloat() < 0.15f) addDrop(drops, e, ModItems.SAP.get(), 1);
        // 1.5% amethyst (tier 1)
        if (!tags.contains("sd_tier_2") && !tags.contains("sd_tier_3")) {
            if (r.nextFloat() < 0.015f) addDrop(drops, e, ModItems.AMETHYST.get(), 1);
        }
        // 高阶(tier_3 Sludge): 25% solar_essence
        if (tags.contains("sd_tier_3")) {
            if (r.nextFloat() < 0.25f) addDrop(drops, e, ModItems.SOLAR_ESSENCE.get(), 1);
        }
        // 0.5% dwarf_scroll_i
        if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_I.get(), 1);
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
        // SDV parity: Slime → 0.5% small_glow_ring
        if (r.nextFloat() < 0.005f) addDropByRegistry(drops, e, "small_glow_ring", 1);
    }

    private static void dropBat(Collection<ItemEntity> drops, LivingEntity e, RandomSource r, Set<String> tags) {
        // Iridium Bat (tier_4): 完全不同的掉落表
        if (tags.contains("sd_tier_4")) {
            dropIridiumBat(drops, e, r);
            return;
        }
        // 90% bat_wing
        if (r.nextFloat() < 0.90f) addDrop(drops, e, ModItems.BAT_WING.get(), 1);
        // 额外 bat_wing: tier 1=40%, tier 2=55%, tier 3=70%
        float extraWingChance = tags.contains("sd_tier_3") ? 0.70f :
                                tags.contains("sd_tier_2") ? 0.55f : 0.40f;
        if (r.nextFloat() < extraWingChance) addDrop(drops, e, ModItems.BAT_WING.get(), 1);
        // 2% bomb
        if (r.nextFloat() < 0.02f) addDrop(drops, e, ModItems.BOMB_ITEM.get(), 1);
        // 0.1% rare_disc
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.RARE_DISC.get(), 1);
        // dwarf scrolls by tier: tier1→scroll_i, tier2→scroll_ii, tier3→scroll_iii
        if (!tags.contains("sd_tier_2") && !tags.contains("sd_tier_3")) {
            if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_I.get(), 1);
        } else if (tags.contains("sd_tier_2")) {
            if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_II.get(), 1);
        } else {
            if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_III.get(), 1);
        }
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
    }

    /**
     * Iridium Bat (sd_tier_4) 掉落 — SDV Monsters.json:
     * 386 .9 | 386 .5 | 386 .25 | 386 .1 | 288 .05 | 768 .5 | 773 .05 | 349 .05 | 787 .05 | 337 .008
     */
    private static void dropIridiumBat(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        // Iridium Ore: 4 independent rolls (90% + 50% + 25% + 10%)
        if (r.nextFloat() < 0.90f) addDrop(drops, e, ModItems.IRIDIUM_ORE.get(), 1);
        if (r.nextFloat() < 0.50f) addDrop(drops, e, ModItems.IRIDIUM_ORE.get(), 1);
        if (r.nextFloat() < 0.25f) addDrop(drops, e, ModItems.IRIDIUM_ORE.get(), 1);
        if (r.nextFloat() < 0.10f) addDrop(drops, e, ModItems.IRIDIUM_ORE.get(), 1);
        // 5% mega_bomb
        if (r.nextFloat() < 0.05f) addDrop(drops, e, ModItems.MEGA_BOMB.get(), 1);
        // 50% solar_essence
        if (r.nextFloat() < 0.50f) addDrop(drops, e, ModItems.SOLAR_ESSENCE.get(), 1);
        // 5% life_elixir (未注册，用 energy_tonic 替代)
        if (r.nextFloat() < 0.05f) addDrop(drops, e, ModItems.ENERGY_TONIC.get(), 1);
        // 5% energy_tonic
        if (r.nextFloat() < 0.05f) addDrop(drops, e, ModItems.ENERGY_TONIC.get(), 1);
        // 5% battery_pack
        if (r.nextFloat() < 0.05f) addDrop(drops, e, ModItems.BATTERY_PACK.get(), 1);
        // 0.8% iridium_bar
        if (r.nextFloat() < 0.008f) addDrop(drops, e, ModItems.IRIDIUM_BAR.get(), 1);
    }

    private static void dropFly(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        // SDV Monsters.json Fly: 684 .9 | 157 .02 | 96 .005 | 99 .001
        // 90% bug_meat
        if (r.nextFloat() < 0.90f) addDrop(drops, e, ModItems.BUG_MEAT.get(), 1);
        // 2% white_algae
        if (r.nextFloat() < 0.02f) addDrop(drops, e, ModItems.WHITE_ALGAE.get(), 1);
        // 0.5% dwarf_scroll_i
        if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_I.get(), 1);
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
    }

    private static void dropGrub(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        // SDV Monsters.json Grub: 684 .6 | 273 .05 | 273 .05 | 157 .02 | 96 .005 | 99 .001
        // 60% bug_meat
        if (r.nextFloat() < 0.60f) addDrop(drops, e, ModItems.BUG_MEAT.get(), 1);
        // 2% white_algae
        if (r.nextFloat() < 0.02f) addDrop(drops, e, ModItems.WHITE_ALGAE.get(), 1);
        // 0.5% dwarf_scroll_i
        if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_I.get(), 1);
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
    }

    private static void dropBug(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        // 75% bug_meat x 1-2
        if (r.nextFloat() < 0.75f) addDrop(drops, e, ModItems.BUG_MEAT.get(), 1 + r.nextInt(2));
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
    }

    private static void dropDustSprite(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        // SDV Monsters.json Dust Spirit: 382 .5 | 433 .01 | 336 .001 | 84 .02 | 414 .02 | 97 .005 | 99 .001
        // SDV 382 = Coal → 50%
        if (r.nextFloat() < 0.50f) addDrop(drops, e, ModItems.COAL.get(), 1);
        // 1% coffee_bean (暂用 gold_bar 替代)
        if (r.nextFloat() < 0.01f) addDrop(drops, e, ModItems.GOLD_BAR.get(), 1);
        // 0.5% dwarf_scroll_ii
        if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_II.get(), 1);
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
        // SDV parity: Dust Sprite → 2% small_magnet_ring
        if (r.nextFloat() < 0.02f) addDropByRegistry(drops, e, "small_magnet_ring", 1);
    }

    private static void dropSkeleton(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        // 50% bone_fragment
        if (r.nextFloat() < 0.50f) addDrop(drops, e, ModItems.BONE_FRAGMENT.get(), 1 + r.nextInt(3));
        // 40% bone_fragment (第二次 roll)
        if (r.nextFloat() < 0.40f) addDrop(drops, e, ModItems.BONE_FRAGMENT.get(), 1 + r.nextInt(2));
        // 20% bone_fragment (第三次 roll)
        if (r.nextFloat() < 0.20f) addDrop(drops, e, ModItems.BONE_FRAGMENT.get(), 1);
        // 0.5% prehistoric_tibia
        if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.PREHISTORIC_TIBIA.get(), 1);
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
        // SDV parity: Skeleton → 4% bone_sword
        if (r.nextFloat() < 0.04f) addDropByRegistry(drops, e, "bone_sword", 1);
    }

    private static void dropGhost(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        // 95% solar_essence
        if (r.nextFloat() < 0.95f) addDrop(drops, e, ModItems.SOLAR_ESSENCE.get(), 1);
        // 10% 额外 solar_essence
        if (r.nextFloat() < 0.10f) addDrop(drops, e, ModItems.SOLAR_ESSENCE.get(), 1);
        // 8% ghost fish
        if (r.nextFloat() < 0.08f) addDrop(drops, e, ModItems.GHOSTFISH.get(), 1);
        // 8% refined_quartz
        if (r.nextFloat() < 0.08f) addDrop(drops, e, ModItems.REFINED_QUARTZ.get(), 1);
        // 0.5% dwarf_scroll_ii
        if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_II.get(), 1);
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
        // SDV parity: Ghost → 2% glow_ring
        if (r.nextFloat() < 0.02f) addDropByRegistry(drops, e, "glow_ring", 1);
    }

    private static void dropCrab(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        Set<String> tags = e.getTags();
        if (tags.contains("sd_tier_2")) {
            // Lava Crab: 40% bomb, 25% crab
            if (r.nextFloat() < 0.40f) addDrop(drops, e, ModItems.BOMB_ITEM.get(), 1);
            if (r.nextFloat() < 0.25f) addDrop(drops, e, ModItems.CRAB.get(), 1);
            // 0.5% dwarf_scroll_iii
            if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_III.get(), 1);
        } else {
            // Rock Crab: 40% cherry_bomb, 15% crab
            if (r.nextFloat() < 0.40f) addDrop(drops, e, ModItems.CHERRY_BOMB.get(), 1);
            if (r.nextFloat() < 0.15f) addDrop(drops, e, ModItems.CRAB.get(), 1);
            // 0.5% dwarf_scroll_i
            if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_I.get(), 1);
        }
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
    }

    private static void dropGolem(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        // 30% bone_fragment x 2-4
        if (r.nextFloat() < 0.30f) addDrop(drops, e, ModItems.BONE_FRAGMENT.get(), 2 + r.nextInt(3));
        // 15% solar_essence
        if (r.nextFloat() < 0.15f) addDrop(drops, e, ModItems.SOLAR_ESSENCE.get(), 1);
        // 0.5% dwarf_scroll_i
        if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_I.get(), 1);
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
    }

    private static void dropShadow(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        // 75% void_essence
        if (r.nextFloat() < 0.75f) addDrop(drops, e, ModItems.VOID_ESSENCE.get(), 1);
        // 额外 void_essence: 10% (brute) / 20% (shaman)
        Set<String> tags = e.getTags();
        boolean isShaman = tags.contains("sd_tier_2");
        float extraVoidChance = isShaman ? 0.20f : 0.10f;
        if (r.nextFloat() < extraVoidChance) addDrop(drops, e, ModItems.VOID_ESSENCE.get(), 1);
        // 0.2% iridium_bar
        if (r.nextFloat() < 0.002f) addDrop(drops, e, ModItems.IRIDIUM_BAR.get(), 1);
        // 1% gold_bar
        if (r.nextFloat() < 0.01f) addDrop(drops, e, ModItems.GOLD_BAR.get(), 1);
        // 2% iron_bar
        if (r.nextFloat() < 0.02f) addDrop(drops, e, ModItems.IRON_BAR.get(), 1);
        // 4% copper_bar
        if (r.nextFloat() < 0.04f) addDrop(drops, e, ModItems.COPPER_BAR.get(), 1);
        // 4% pumpkin_soup (Brute only)
        if (!isShaman && r.nextFloat() < 0.04f) {
            addDropByRegistry(drops, e, "pumpkin_soup", 1);
        }
        // 0.3% rare_disc
        if (r.nextFloat() < 0.003f) addDrop(drops, e, ModItems.RARE_DISC.get(), 1);
        // 0.05% prismatic_shard
        if (r.nextFloat() < 0.0005f) addDrop(drops, e, ModItems.PRISMATIC_SHARD.get(), 1);
        // 0.5% dwarf_scroll_iii
        if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_III.get(), 1);
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
        // SDV parity: Shadow → 1% napalm/protection ring
        if (r.nextFloat() < 0.01f) {
            addDropByRegistry(drops, e, r.nextBoolean() ? "napalm_ring" : "protection_ring", 1);
        }
    }

    private static void dropDuggy(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        // 25% cherry_bomb
        if (r.nextFloat() < 0.25f) addDrop(drops, e, ModItems.CHERRY_BOMB.get(), 1);
        // 25% geode
        if (r.nextFloat() < 0.25f) addDrop(drops, e, ModItems.GEODE.get(), 1);
        // 3% crystal_fruit
        if (r.nextFloat() < 0.03f) addDropByRegistry(drops, e, "crystal_fruit", 1);
        // 2% snail
        if (r.nextFloat() < 0.02f) addDrop(drops, e, ModItems.SNAIL.get(), 1);
        // 10% earth_crystal
        if (r.nextFloat() < 0.10f) addDrop(drops, e, ModItems.EARTH_CRYSTAL.get(), 1);
        // 1% diamond
        if (r.nextFloat() < 0.01f) addDrop(drops, e, ModItems.DIAMOND.get(), 1);
        // 0.5% dwarf_scroll_i
        if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_I.get(), 1);
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
    }

    private static void dropMetalHead(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        // SDV Monsters.json: 768 .65 | 378 .1 | 378 .1 | 380 .1 | 380 .1 | 382 .1 | 98 .005 | 99 .001
        // 65% solar_essence
        if (r.nextFloat() < 0.65f) addDrop(drops, e, ModItems.SOLAR_ESSENCE.get(), 1);
        // copper_ore: 两次独立 10% roll（SDV 378 .1 378 .1）
        if (r.nextFloat() < 0.10f) addDrop(drops, e, ModItems.COPPER_ORE.get(), 1);
        if (r.nextFloat() < 0.10f) addDrop(drops, e, ModItems.COPPER_ORE.get(), 1);
        // iron_ore: 两次独立 10% roll（SDV 380 .1 380 .1）
        if (r.nextFloat() < 0.10f) addDrop(drops, e, ModItems.IRON_ORE.get(), 1);
        if (r.nextFloat() < 0.10f) addDrop(drops, e, ModItems.IRON_ORE.get(), 1);
        // 10% gold_ore
        if (r.nextFloat() < 0.10f) addDrop(drops, e, ModItems.GOLD_ORE.get(), 1);
        // 0.5% dwarf_scroll_iii
        if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_III.get(), 1);
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
    }

    private static void dropSquidKid(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        // 75% solar_essence
        if (r.nextFloat() < 0.75f) addDrop(drops, e, ModItems.SOLAR_ESSENCE.get(), 1);
        // 20% squid_ink
        if (r.nextFloat() < 0.20f) addDropByRegistry(drops, e, "squid_ink", 1);
        // 10% bomb
        if (r.nextFloat() < 0.10f) addDrop(drops, e, ModItems.BOMB_ITEM.get(), 1);
        // 5% mega_bomb
        if (r.nextFloat() < 0.05f) addDrop(drops, e, ModItems.MEGA_BOMB.get(), 1);
        // 5% gold_bar
        if (r.nextFloat() < 0.05f) addDrop(drops, e, ModItems.GOLD_BAR.get(), 1);
        // 0.5% dwarf_scroll_iii
        if (r.nextFloat() < 0.005f) addDrop(drops, e, ModItems.DWARF_SCROLL_III.get(), 1);
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
    }

    private static void dropMummy(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        // 60% void_essence x 1-2
        if (r.nextFloat() < 0.60f) addDrop(drops, e, ModItems.VOID_ESSENCE.get(), 1 + r.nextInt(2));
        // 30% bone_fragment x 2-4
        if (r.nextFloat() < 0.30f) addDrop(drops, e, ModItems.BONE_FRAGMENT.get(), 2 + r.nextInt(3));
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
    }

    private static void dropSerpent(Collection<ItemEntity> drops, LivingEntity e, RandomSource r) {
        // 50% void_essence
        if (r.nextFloat() < 0.50f) addDrop(drops, e, ModItems.VOID_ESSENCE.get(), 1);
        // 30% bone_fragment x 1-3
        if (r.nextFloat() < 0.30f) addDrop(drops, e, ModItems.BONE_FRAGMENT.get(), 1 + r.nextInt(3));
        // 0.1% dwarf_scroll_iv
        if (r.nextFloat() < 0.001f) addDrop(drops, e, ModItems.DWARF_SCROLL_IV.get(), 1);
        // SDV parity: Serpent → 0.5% leprechaun_shoes
        if (r.nextFloat() < 0.005f) addDropByRegistry(drops, e, "leprechaun_shoes", 1);
    }

    // ======================== 工具方法 ========================

    private static void addDrop(Collection<ItemEntity> drops, LivingEntity entity, Item item, int count) {
        ItemStack stack = new ItemStack(item, count);
        ItemEntity itemEntity = new ItemEntity(entity.level(),
                entity.getX(), entity.getY(), entity.getZ(), stack);
        itemEntity.setDefaultPickUpDelay();
        drops.add(itemEntity);
    }

    /**
     * 通过注册表名查找物品（用于 VanillaCategoryItemRegistrar / CookingDishRegistrar 注册的物品）
     */
    private static void addDropByRegistry(Collection<ItemEntity> drops, LivingEntity entity, String name, int count) {
        var rl = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, name);
        Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(rl);
        if (item == net.minecraft.world.item.Items.AIR) {
            StardewCraft.LOGGER.warn("[MineMonsterDrop] Item not found: {}", name);
            return;
        }
        addDrop(drops, entity, item, count);
    }
}
