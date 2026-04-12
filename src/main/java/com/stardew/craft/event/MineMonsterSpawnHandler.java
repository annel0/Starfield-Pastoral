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
        } else if (type == EntityType.VEX) {
            assignVex(mob, floor);
        } else if (type == EntityType.SKELETON) {
            assignSkeleton(mob, floor);
        } else if (type == EntityType.ZOMBIE) {
            assignZombie(mob, floor);
        } else if (type == EntityType.WITHER_SKELETON) {
            assignWitherSkeleton(mob, floor);
        } else if (type == EntityType.EVOKER) {
            assignEvoker(mob, floor);
        } else if (type == EntityType.BLAZE) {
            assignBlaze(mob, floor);
        } else {
            // 不属于矿井怪物映射表：取消生成
            event.setCanceled(true);
        }
    }

    // ======================== 怪物分配 ========================
    // 所有数值严格匹配 SDV 原版 (MINE_MONSTER_SYSTEM_DESIGN.md §2.2)

    private static void assignSlime(Mob mob, int floor) {
        mob.addTag("sd_mob_slime");
        if (floor >= 80) {
            mob.addTag("sd_tier_3");
            setStats(mob, 205, 16, 0, 0.25);   // Sludge (SDV 原版)
            setSDVName(mob, "Sludge");
        } else if (floor >= 40) {
            mob.addTag("sd_tier_2");
            setStats(mob, 106, 7, 0, 0.25);    // Frost Jelly
            setSDVName(mob, "Frost Jelly");
        } else {
            setStats(mob, 24, 5, 0, 0.25);     // Green Slime
            setSDVName(mob, "Green Slime");
        }
    }

    private static void assignBat(Mob mob, int floor) {
        mob.addTag("sd_mob_bat");
        if (floor >= 120) {
            mob.addTag("sd_tier_4");
            setStats(mob, 300, 25, 0, 0.40);    // Iridium Bat (SDV 原版)
            setSDVName(mob, "Iridium Bat");
        } else if (floor >= 80) {
            mob.addTag("sd_tier_3");
            setStats(mob, 80, 15, 0, 0.35);    // Lava Bat
            setSDVName(mob, "Lava Bat");
        } else if (floor >= 40) {
            mob.addTag("sd_tier_2");
            setStats(mob, 36, 7, 0, 0.30);     // Frost Bat
            setSDVName(mob, "Frost Bat");
        } else {
            setStats(mob, 24, 6, 0, 0.30);     // Bat
            setSDVName(mob, "Bat");
        }
    }

    private static void assignSilverfish(Mob mob, int floor) {
        if (floor >= 80) {
            // Lava Crab (80-119)
            mob.addTag("sd_mob_crab");
            mob.addTag("sd_tier_2");
            setStats(mob, 120, 15, 12, 0.25);
            setSDVName(mob, "Lava Crab");
        } else if (floor >= 40) {
            // 40-79 层无 Silverfish 类怪物，按 Duggy 处理
            mob.addTag("sd_mob_duggy");
            setStats(mob, 40, 6, 0, 0.20);
            setSDVName(mob, "Duggy");
        } else {
            // Rock Crab (1-39) 或 Duggy (1-39) — 随机分配
            if (mob.getRandom().nextBoolean()) {
                mob.addTag("sd_mob_crab");
                setStats(mob, 30, 5, 8, 0.20);     // Rock Crab
                setSDVName(mob, "Rock Crab");
            } else {
                mob.addTag("sd_mob_duggy");
                setStats(mob, 40, 6, 0, 0.20);     // Duggy
                setSDVName(mob, "Duggy");
            }
        }
    }

    private static void assignEndermite(Mob mob, int floor) {
        if (floor >= 40) {
            mob.addTag("sd_mob_dust_sprite");
            setStats(mob, 40, 6, 2, 0.35);     // Dust Spirit (SDV 原版)
            setSDVName(mob, "Dust Spirit");
        } else {
            mob.addTag("sd_mob_grub");
            setStats(mob, 20, 4, 0, 0.15);     // Grub
            setSDVName(mob, "Grub");
        }
    }

    private static void assignVex(Mob mob, int floor) {
        if (floor >= 40) {
            mob.addTag("sd_mob_ghost");
            setStats(mob, 96, 10, 2, 0.30);    // Ghost (SDV 原版)
            setSDVName(mob, "Ghost");
        } else {
            mob.addTag("sd_mob_fly");
            setStats(mob, 22, 6, 0, 0.30);     // Fly
            setSDVName(mob, "Fly");
        }
    }

    private static void assignSkeleton(Mob mob, int floor) {
        mob.addTag("sd_mob_skeleton");
        setStats(mob, 140, 10, 2, 0.25);       // Skeleton (SDV 原版，仅40-79层出现)
        setSDVName(mob, "Skeleton");
    }

    private static void assignZombie(Mob mob, int floor) {
        if (floor >= 80) {
            mob.addTag("sd_mob_metal_head");
            setStats(mob, 80, 15, 16, 0.20);   // Metal Head (SDV 原版：高护甲)
            setSDVName(mob, "Metal Head");
        } else {
            mob.addTag("sd_mob_golem");
            setStats(mob, 45, 5, 10, 0.18);    // Rock Golem (SDV 原版：高防)
            setSDVName(mob, "Rock Golem");
        }
    }

    private static void assignWitherSkeleton(Mob mob, int floor) {
        mob.addTag("sd_mob_shadow");
        setStats(mob, 160, 18, 4, 0.30);       // Shadow Brute (SDV 原版)
        setSDVName(mob, "Shadow Brute");
    }

    private static void assignEvoker(Mob mob, int floor) {
        mob.addTag("sd_mob_shadow");
        mob.addTag("sd_tier_2");
        setStats(mob, 80, 17, 2, 0.25);        // Shadow Shaman (SDV 原版)
        setSDVName(mob, "Shadow Shaman");
    }

    private static void assignBlaze(Mob mob, int floor) {
        mob.addTag("sd_mob_squid");
        setStats(mob, 50, 18, 2, 0.25);        // Squid Kid (SDV 原版)
        setSDVName(mob, "Squid Kid");
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
}
