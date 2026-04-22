package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.mining.MiningCoordinates;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

/**
 * 矿井怪物刷怪事件处理器
 *
 * 当生物加入矿井维度时：
 * 1. 仅允许指定MC原版生物类型
 * 2. 根据楼层分配 sd_mob_* / sd_tier_* 标签
 * 3. 覆写 HP / 攻击力 / 护甲 / 移速 以匹配SDV怪物属性
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
@SuppressWarnings("null")
public class MineMonsterSpawnHandler {

    private static final ResourceLocation MOD_HP = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "mine_mob_hp");
    private static final ResourceLocation MOD_ATK = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "mine_mob_atk");
    private static final ResourceLocation MOD_ARMOR = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "mine_mob_armor");
    private static final ResourceLocation MOD_SPEED = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "mine_mob_speed");

    /** 每层楼最大怪物数量（房间 80-120 格，面积比 SDV 大 5-10 倍） */
    private static final int MAX_MONSTERS_PER_FLOOR = 30;

    /** Per-floor mob count cache to avoid expensive AABB scans on every spawn. */
    private static final java.util.Map<Integer, Integer> floorMobCounts = new java.util.concurrent.ConcurrentHashMap<>();
    private static long lastCountRefreshTick = 0;
    private static final long COUNT_REFRESH_INTERVAL = 60; // refresh every 3 seconds

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (!serverLevel.dimension().equals(ModMiningDimensions.STARDEW_MINING)) return;
        if (!(event.getEntity() instanceof Mob mob)) return;

        // StardewNpcEntity (e.g. Dwarf) is a Mob subclass — never filter it
        if (mob instanceof com.stardew.craft.entity.npc.StardewNpcEntity) return;

        // 已经标记过的不再处理
        if (mob.getTags().stream().anyMatch(t -> t.startsWith("sd_mob_"))) return;

        // 怪物数量上限检查：使用缓存计数而非每次 AABB 扫描
        int floor = getFloorFromPos(mob);

        // Periodically refresh counts (cheap: just reset, actual AABB scan only if needed)
        long currentTick = serverLevel.getGameTime();
        if (currentTick - lastCountRefreshTick > COUNT_REFRESH_INTERVAL) {
            floorMobCounts.clear();
            lastCountRefreshTick = currentTick;
        }

        int cachedCount = floorMobCounts.getOrDefault(floor, -1);
        if (cachedCount == -1) {
            // First check for this floor in this refresh window — do actual count
            int floorZ = floor * MiningCoordinates.FLOOR_SPACING;
            AABB floorBounds = new AABB(
                    mob.getX() - 150, 0, floorZ - 10,
                    mob.getX() + 150, 256, floorZ + 150);
            cachedCount = serverLevel.getEntitiesOfClass(Mob.class, floorBounds,
                    m -> m.getTags().stream().anyMatch(t -> t.startsWith("sd_mob_"))).size();
            floorMobCounts.put(floor, cachedCount);
        }

        if (cachedCount >= MAX_MONSTERS_PER_FLOOR) {
            event.setCanceled(true);
            return;
        }

        // Increment cached count for this floor
        floorMobCounts.merge(floor, 1, Integer::sum);
        EntityType<?> type = mob.getType();

        // 根据MC实体类型 + 楼层分配SDV角色
        if (type == EntityType.SLIME) {
            assignSlime(mob, floor);
        } else if (type == EntityType.PHANTOM) {
            assignBat(mob, floor);
        } else if (type == EntityType.SILVERFISH) {
            assignSilverfish(mob, floor);
        } else if (type == EntityType.ENDERMITE) {
            assignEndermite(mob, floor);
        } else if (type == EntityType.SPIDER) {
            assignBug(mob, floor);
        } else if (type == EntityType.CAVE_SPIDER) {
            assignFly(mob, floor);
        } else if (type == EntityType.HUSK) {
            assignGhost(mob, floor);
        } else if (type == EntityType.SKELETON) {
            assignSkeleton(mob, floor);
        } else if (type == EntityType.ZOMBIE) {
            assignZombie(mob, floor);
        } else if (type == EntityType.WITHER_SKELETON) {
            assignWitherSkeleton(mob, floor);
        } else if (type == EntityType.STRAY) {
            assignStray(mob, floor);
        } else if (type == EntityType.BLAZE) {
            assignBlaze(mob, floor);
        } else if (type == EntityType.DROWNED) {
            assignMummy(mob, floor);
        } else if (type == EntityType.VEX) {
            assignSerpent(mob, floor);
        } else if (type == EntityType.HOGLIN) {
            assignDino(mob, floor);
        } else if (type == EntityType.MAGMA_CUBE) {
            assignBigSlime(mob, floor);
        } else {
            // 不属于矿井怪物映射表：取消生成
            event.setCanceled(true);
        }
    }

    // ======================== 怪物分配 ========================
    // 数值基于 SDV Monsters.json，加入楼层缩放（zone 起始 0.8x → zone 末尾 1.0x）

    /**
     * 楼层难度缩放：每个 zone 内部从 0.8 逐渐升到 1.0
     * 让同一 zone 前几层更轻松，后几层接近 SDV 原值
     */
    private static float getFloorScaling(int floor) {
        float progress;
        if (floor <= 40) {
            progress = Math.max(0f, floor / 40f);
        } else if (floor <= 79) {
            progress = (floor - 40) / 40f;
        } else if (floor <= 119) {
            progress = (floor - 80) / 40f;
        } else if (floor > 120) {
            // 骷髅矿：1.0 + (floor-120) * 0.008，上限 2.5
            float skullScaling = 1.0f + (floor - 120) * 0.008f;
            return Math.min(2.5f, skullScaling);
        } else {
            progress = 1.0f;
        }
        return 0.8f + 0.2f * progress;
    }

    private static void assignSlime(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        mob.addTag("sd_mob_slime");
        if (floor >= 80) {
            mob.addTag("sd_tier_3");
            setStats(mob, 205 * s, 16 * s, 0, 0.25);
            setSDVName(mob, "Sludge");
        } else if (floor >= 40) {
            mob.addTag("sd_tier_2");
            setStats(mob, 106 * s, 7 * s, 0, 0.25);
            setSDVName(mob, "Frost Jelly");
        } else {
            setStats(mob, 24 * s, 5 * s, 0, 0.25);
            setSDVName(mob, "Green Slime");
        }
    }

    private static void assignBat(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        mob.addTag("sd_mob_bat");
        if (floor >= 120) {
            mob.addTag("sd_tier_4");
            setStats(mob, 300 * s, 25 * s, 0, 0.40); // Iridium Bat — 按楼层缩放
            setSDVName(mob, "Iridium Bat");
        } else if (floor >= 80) {
            mob.addTag("sd_tier_3");
            setStats(mob, 80 * s, 15 * s, 0, 0.35);
            setSDVName(mob, "Lava Bat");
        } else if (floor >= 40) {
            mob.addTag("sd_tier_2");
            setStats(mob, 36 * s, 7 * s, 0, 0.30);
            setSDVName(mob, "Frost Bat");
        } else {
            setStats(mob, 24 * s, 6 * s, 0, 0.30);
            setSDVName(mob, "Bat");
        }
    }

    private static void assignSilverfish(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        if (floor > 120) {
            // 骷髅矿：Iridium Crab
            mob.addTag("sd_mob_crab");
            mob.addTag("sd_tier_4");
            mob.addTag("sd_tier_skull");
            setStats(mob, 300 * s, 28 * s, 16, 0.20);
            setSDVName(mob, "Iridium Crab");
        } else if (floor >= 80) {
            mob.addTag("sd_mob_crab");
            mob.addTag("sd_tier_2");
            setStats(mob, 120 * s, 15 * s, 12, 0.25);
            setSDVName(mob, "Lava Crab");
        } else if (floor >= 40) {
            mob.addTag("sd_mob_duggy");
            setStats(mob, 40 * s, 6 * s, 0, 0.20);
            setSDVName(mob, "Duggy");
        } else {
            // 1-39: Rock Crab 或 Duggy（随机）
            if (mob.getRandom().nextBoolean()) {
                mob.addTag("sd_mob_crab");
                setStats(mob, 30 * s, 5 * s, 8, 0.20);
                setSDVName(mob, "Rock Crab");
            } else {
                mob.addTag("sd_mob_duggy");
                setStats(mob, 40 * s, 6 * s, 0, 0.20);
                setSDVName(mob, "Duggy");
            }
        }
    }

    private static void assignEndermite(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        if (floor >= 40) {
            mob.addTag("sd_mob_dust_sprite");
            setStats(mob, 40 * s, 6 * s, 2, 0.35);
            setSDVName(mob, "Dust Spirit");
        } else {
            mob.addTag("sd_mob_grub");
            setStats(mob, 20 * s, 4 * s, 0, 0.15);
            setSDVName(mob, "Grub");
        }
    }

    /** Bug（SDV 装甲飞虫）— MC Spider 映射 */
    private static void assignBug(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        mob.addTag("sd_mob_bug");
        setStats(mob, 1, 8 * s, 0, 0.30);  // SDV Bug: 1HP 但 8ATK，一碰就死
        setSDVName(mob, "Bug");
    }

    /** Fly（SDV 苍蝇）— MC Cave Spider 映射 */
    private static void assignFly(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        mob.addTag("sd_mob_fly");
        setStats(mob, 22 * s, 6 * s, 0, 0.30);
        setSDVName(mob, "Fly");
    }

    /** Ghost — MC Husk 映射（缓慢肉搏型，每层限 1 只） */
    private static void assignGhost(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        if (floor > 120) {
            // 骷髅矿：Carbon Ghost
            mob.addTag("sd_mob_ghost");
            mob.addTag("sd_tier_skull");
            setStats(mob, 190 * s, 25 * s, 4, 0.30);
            setSDVName(mob, "Carbon Ghost");
        } else {
            mob.addTag("sd_mob_ghost");
            setStats(mob, 96 * s, 10 * s, 3, 0.25);
            setSDVName(mob, "Ghost");
        }
    }

    private static void assignSkeleton(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        mob.addTag("sd_mob_skeleton");
        setStats(mob, 140 * s, 10 * s, 2, 0.25);
        setSDVName(mob, "Skeleton");
    }

    private static void assignZombie(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        if (floor >= 80) {
            mob.addTag("sd_mob_metal_head");
            setStats(mob, 40 * s, 15 * s, 16, 0.20);  // SDV Metal Head: 40HP 高甲
            setSDVName(mob, "Metal Head");
        } else {
            mob.addTag("sd_mob_golem");
            setStats(mob, 45 * s, 5 * s, 10, 0.18);
            setSDVName(mob, "Rock Golem");
        }
    }

    private static void assignWitherSkeleton(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        mob.addTag("sd_mob_shadow");
        setStats(mob, 160 * s, 18 * s, 4, 0.30);
        setSDVName(mob, "Shadow Brute");
    }

    /** Shadow Shaman — MC Stray 映射（远程骷髅，替代 Evoker 避免召唤恼鬼） */
    private static void assignStray(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        mob.addTag("sd_mob_shadow");
        mob.addTag("sd_tier_2");
        setStats(mob, 80 * s, 17 * s, 2, 0.25);
        setSDVName(mob, "Shadow Shaman");
    }

    private static void assignBlaze(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        mob.addTag("sd_mob_squid");
        setStats(mob, 50 * s, 18 * s, 2, 0.25);
        setSDVName(mob, "Squid Kid");
    }

    // ──────── 骷髅矿洞新增怪物映射 ────────

    /** Mummy — MC Drowned 映射（骷髅矿核心怪物，被击倒后可复活） */
    private static void assignMummy(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        mob.addTag("sd_mob_mummy");
        mob.addTag("sd_tier_skull");
        setStats(mob, 260 * s, 30 * s, 4, 0.20);
        setSDVName(mob, "Mummy");
    }

    /** Serpent — MC Vex 映射（飞行型，速度快） */
    private static void assignSerpent(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        // 深层变为 Royal Serpent
        if (floor >= 200 && s > 1.5f) {
            mob.addTag("sd_mob_royal_serpent");
            mob.addTag("sd_tier_skull");
            setStats(mob, 300 * s, 30 * s, 3, 0.40);
            setSDVName(mob, "Royal Serpent");
        } else {
            mob.addTag("sd_mob_serpent");
            mob.addTag("sd_tier_skull");
            setStats(mob, 150 * s, 23 * s, 2, 0.35);
            setSDVName(mob, "Serpent");
        }
    }

    /** DinoMonster — MC Hoglin 映射（稀有地面大型怪） */
    private static void assignDino(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        mob.addTag("sd_mob_dino");
        mob.addTag("sd_tier_skull");
        setStats(mob, 300 * s, 15 * s, 6, 0.25);
        setSDVName(mob, "Pepper Rex");
    }

    /** BigSlime (Skull) — MC MagmaCube 映射 */
    private static void assignBigSlime(Mob mob, int floor) {
        float s = getFloorScaling(floor);
        mob.addTag("sd_mob_bigslime_skull");
        mob.addTag("sd_tier_skull");
        setStats(mob, 200 * s, 20 * s, 2, 0.20);
        setSDVName(mob, "Big Slime");
    }

    // ======================== 属性设置 ========================

    @SuppressWarnings("null")
    private static void setStats(Mob mob, double hp, double atk, double armor, double speed) {
        // HP
        AttributeInstance maxHpAttr = mob.getAttribute(Attributes.MAX_HEALTH);
        if (maxHpAttr != null) {
            double base = maxHpAttr.getBaseValue();
            double diff = hp - base;
            maxHpAttr.removeModifier(MOD_HP);
            maxHpAttr.addPermanentModifier(new AttributeModifier(
                    MOD_HP, diff, AttributeModifier.Operation.ADD_VALUE));
            mob.setHealth((float) hp);
        }

        // Attack
        AttributeInstance atkAttr = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (atkAttr != null) {
            double base = atkAttr.getBaseValue();
            double diff = atk - base;
            atkAttr.removeModifier(MOD_ATK);
            atkAttr.addPermanentModifier(new AttributeModifier(
                    MOD_ATK, diff, AttributeModifier.Operation.ADD_VALUE));
        }

        // Armor
        AttributeInstance armorAttr = mob.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            double base = armorAttr.getBaseValue();
            double diff = armor - base;
            armorAttr.removeModifier(MOD_ARMOR);
            armorAttr.addPermanentModifier(new AttributeModifier(
                    MOD_ARMOR, diff, AttributeModifier.Operation.ADD_VALUE));
        }

        // Speed
        AttributeInstance speedAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            double base = speedAttr.getBaseValue();
            double diff = speed - base;
            speedAttr.removeModifier(MOD_SPEED);
            speedAttr.addPermanentModifier(new AttributeModifier(
                    MOD_SPEED, diff, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    /**
     * 设置 SDV 显示名（同步到客户端），隐藏原版 nametag。
     */
    private static void setSDVName(Mob mob, String name) {
        mob.setCustomName(Component.literal(name));
        mob.setCustomNameVisible(false);
    }

    private static int getFloorFromPos(Mob mob) {
        return Math.round(mob.blockPosition().getZ() / (float) MiningCoordinates.FLOOR_SPACING);
    }

    // ═══════════ Mummy 复活机制 ═══════════
    // SDV: 非爆炸击杀 → 倒地 10 秒后原地复活满血；只有炸弹能永久击杀

    /** Mummy 倒地标记 tag */
    private static final String MUMMY_COLLAPSED_TAG = "sd_mummy_collapsed";

    @SubscribeEvent
    public static void onMummyDeath(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (!mob.getTags().contains("sd_mob_mummy")) return;
        // 已经在倒地状态再次被杀（如炸弹补刀）→ 允许正常死亡
        if (mob.getTags().contains(MUMMY_COLLAPSED_TAG)) return;

        // 检查伤害源是否是爆炸
        if (event.getSource() != null && event.getSource().is(net.minecraft.world.damagesource.DamageTypes.EXPLOSION)
                || (event.getSource() != null && event.getSource().is(net.minecraft.world.damagesource.DamageTypes.PLAYER_EXPLOSION))) {
            return; // 爆炸 → 正常死亡
        }

        // 取消死亡 → 进入"倒地"状态
        event.setCanceled(true);
        mob.setHealth(1.0F); // 保留 1 HP 避免被 MC 判定为已死
        mob.addTag(MUMMY_COLLAPSED_TAG);
        mob.setNoAi(true);
        mob.setInvulnerable(true);
        mob.setSilent(true);

        // 10 秒后复活
        if (mob.level() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().tell(new net.minecraft.server.TickTask(
                    serverLevel.getServer().getTickCount() + 200, () -> {
                if (mob.isAlive() && mob.getTags().contains(MUMMY_COLLAPSED_TAG)) {
                    mob.removeTag(MUMMY_COLLAPSED_TAG);
                    mob.setNoAi(false);
                    mob.setInvulnerable(false);
                    mob.setSilent(false);
                    // 复活满血
                    AttributeInstance maxHp = mob.getAttribute(Attributes.MAX_HEALTH);
                    if (maxHp != null) {
                        mob.setHealth((float) maxHp.getValue());
                    }
                }
            }));
        }
    }
}
