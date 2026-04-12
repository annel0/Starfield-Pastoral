package com.stardew.craft.entity.bomb;

import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.item.bomb.BombType;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * SDV 炸弹实体 — 放置在地面上，引信倒数后爆炸。
 *
 * <p>完全复刻 SDV 原版逻辑：</p>
 * <ul>
 *   <li>引信时间：48 ticks (2400ms)，引信期间模型加速颤抖</li>
 *   <li>音效：放置 thudStep → 引信 fuse (循环) → 爆炸 explosion</li>
 *   <li>颤抖：shakeIntensity=0.5 + 0.002/tick 加速，完全匹配 SDV</li>
 *   <li>爆炸：2D 圆形填充（XZ 平面）破坏方块，范围 = SDV 原版 +20%</li>
 *   <li>伤害：怪物 r*6~r*8，玩家自伤 r*3（r 为 SDV 原版半径）</li>
 *   <li>粒子：45% 概率每格生成碎片或粉尘</li>
 * </ul>
 */
@SuppressWarnings("null")
public class StardewBombEntity extends Entity {

    private static final EntityDataAccessor<Integer> DATA_BOMB_TYPE =
        SynchedEntityData.defineId(StardewBombEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_FUSE =
        SynchedEntityData.defineId(StardewBombEntity.class, EntityDataSerializers.INT);

    /** 引信循环音效的播放间隔（ticks）。fuse.ogg ≈ 0.16s，每 3 tick 播放一次保持连续 */
    private static final int FUSE_SOUND_INTERVAL = 3;

    @Nullable
    private Player owner;

    public StardewBombEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = false;
        this.setNoGravity(false);
    }

    /* ── 初始化 ─────────────────────────────────────────── */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BOMB_TYPE, 0);
        builder.define(DATA_FUSE, 48);
    }

    public void setBombType(BombType type) {
        this.entityData.set(DATA_BOMB_TYPE, type.ordinal());
        this.entityData.set(DATA_FUSE, type.getFuseTicks());
    }

    public BombType getBombType() {
        return BombType.fromOrdinal(this.entityData.get(DATA_BOMB_TYPE));
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE);
    }

    public void setOwner(@Nullable Player player) {
        this.owner = player;
    }

    /* ── tick ────────────────────────────────────────────── */

    @Override
    public void tick() {
        super.tick();

        // 简单重力
        if (!this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04, 0));
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }
        this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());

        int fuse = getFuse();

        if (!level().isClientSide()) {
            // SDV: fuse 音效循环播放（每 FUSE_SOUND_INTERVAL ticks 播一次）
            if (fuse > 0 && fuse % FUSE_SOUND_INTERVAL == 0) {
                level().playSound(null, this.blockPosition(),
                    ModSounds.FUSE.get(), SoundSource.BLOCKS, 0.7f, 1.0f);
            }

            if (fuse <= 0) {
                explode();
                return;
            }
            entityData.set(DATA_FUSE, fuse - 1);
        }

        // 客户端：引信火花粒子
        if (level().isClientSide()) {
            spawnFuseParticles(fuse);
        }
    }

    /* ── 引信火花粒子（SDV: 3 层 spark，黄/橙/白交错） ──── */

    private void spawnFuseParticles(int fuse) {
        if (fuse <= 0) return;

        BombType type = getBombType();
        double sparkX = this.getX();
        double sparkY = this.getY() + getModelHeight(type);
        double sparkZ = this.getZ();

        // SDV: 53ms/frame × 5 frames × 9 loops = ~2385ms
        // MC: 基础火花每 tick 生成
        level().addParticle(ParticleTypes.FLAME,
            sparkX + (random.nextDouble() - 0.5) * 0.15,
            sparkY + random.nextDouble() * 0.1,
            sparkZ + (random.nextDouble() - 0.5) * 0.15,
            0, 0.02, 0);

        // 黄色/橙色层 — 每 2 tick 交替
        if (fuse % 2 == 0) {
            level().addParticle(ParticleTypes.LAVA,
                sparkX + (random.nextDouble() - 0.5) * 0.1,
                sparkY,
                sparkZ + (random.nextDouble() - 0.5) * 0.1,
                0, 0.01, 0);
        }

        // SDV 白色烟雾层 — 概率生成
        if (random.nextInt(3) == 0) {
            level().addParticle(ParticleTypes.SMOKE,
                sparkX, sparkY + 0.1, sparkZ,
                0, 0.01, 0);
        }
    }

    private float getModelHeight(BombType type) {
        return switch (type) {
            case CHERRY_BOMB -> 0.375f;  // 6/16
            case BOMB -> 0.8125f;        // 13/16
            case MEGA_BOMB -> 0.875f;    // 14/16
        };
    }

    /* ── 爆炸 ──────────────────────────────────────────── */

    private void explode() {
        if (level().isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) level();
        BombType type = getBombType();
        BlockPos center = this.blockPosition();
        float scaledRadius = type.getScaledRadius();

        // 1. SDV: 停止 fuse 音效 → 播放 explosion
        serverLevel.playSound(null, center, ModSounds.EXPLOSION.get(),
            SoundSource.BLOCKS, 1.5f, 0.9f + random.nextFloat() * 0.2f);

        // 2. SDV 圆形填充图案破坏方块（2D 圆 + 垂直范围 ±scaledRadius）
        destroyBlocksInCircle(serverLevel, center, scaledRadius);

        // 3. 伤害范围内实体（使用 SDV 原版半径计算伤害）
        damageEntitiesInRadius(serverLevel, center, type, scaledRadius);

        // 4. SDV 爆炸粒子（按炸弹类型区分规模）
        spawnExplosionParticles(serverLevel, center, type, scaledRadius);

        // 5. 移除实体
        this.discard();
    }

    /**
     * SDV 使用 getCircleOutlineGrid(radius) 生成填充圆形图案。
     * 本实现在 XZ 平面上做 2D 圆形检测，Y 方向扩展 ±scaledRadius（球形）。
     */
    private void destroyBlocksInCircle(ServerLevel level, BlockPos center, float radius) {
        int r = (int) Math.ceil(radius);
        float radiusSq = radius * radius;

        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                // SDV 2D 圆形检测
                if (dx * dx + dz * dz > radiusSq) continue;

                for (int dy = -r; dy <= r; dy++) {
                    // 3D 球形检测（自然的 MC 3D 扩展）
                    if (dx * dx + dy * dy + dz * dz > radiusSq) continue;

                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);

                    if (state.isAir()) continue;
                    // 不破坏基岩等不可破坏方块
                    if (state.getDestroySpeed(level, pos) < 0) continue;
                    // 不破坏矿井梯子
                    if (state.is(ModBlocks.MINE_LADDER.get())) continue;

                    // SDV 炸弹掉落逻辑：矿石掉产物，其余掉自身
                    dropBlockForBomb(level, pos, state);
                    level.removeBlock(pos, false);
                }
            }
        }
    }

    /**
     * SDV 炸弹方块掉落：矿石 → 产物物品，其余方块 → 方块自身。
     * 复用 MinePickaxeEvents 中相同的矿石→产物映射。
     */
    private void dropBlockForBomb(ServerLevel level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();

        // 1. 检查是否为模组矿石，掉落对应产物
        net.minecraft.world.item.Item oreProduct = getOreDropItem(state);
        if (oreProduct != null) {
            Block.popResource(level, pos, new net.minecraft.world.item.ItemStack(oreProduct));
            return;
        }

        // 2. 其余方块：掉落方块物品本身
        net.minecraft.world.item.Item blockItem = block.asItem();
        if (blockItem != net.minecraft.world.item.Items.AIR) {
            Block.popResource(level, pos, new net.minecraft.world.item.ItemStack(blockItem));
        }
    }

    /**
     * SDV 矿石 → 产物映射（与 MinePickaxeEvents.getOreDropItem 一致）。
     * 返回 null 表示不是矿石。
     */
    @Nullable
    private static net.minecraft.world.item.Item getOreDropItem(BlockState state) {
        @SuppressWarnings("null")
        var key = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (key == null || !com.stardew.craft.StardewCraft.MODID.equals(key.getNamespace())) {
            return null;
        }
        String path = key.getPath();
        if (path.contains("coal_ore"))     return ModItems.COAL.get();
        if (path.contains("copper_ore"))   return ModItems.COPPER_ORE.get();
        if (path.contains("iron_ore"))     return ModItems.IRON_ORE.get();
        if (path.contains("gold_ore"))     return ModItems.GOLD_ORE.get();
        if (path.contains("iridium_ore"))  return ModItems.IRIDIUM_ORE.get();
        return switch (path) {
            case "amethyst_ore"   -> ModItems.AMETHYST.get();
            case "aquamarine_ore" -> ModItems.AQUAMARINE.get();
            case "diamond_ore"    -> ModItems.DIAMOND.get();
            case "emerald_ore"    -> ModItems.EMERALD.get();
            case "jade_ore"       -> ModItems.JADE.get();
            case "ruby_ore"       -> ModItems.RUBY.get();
            case "topaz_ore"      -> ModItems.TOPAZ.get();
            default -> null;
        };
    }

    /**
     * SDV 伤害公式：怪物 r*6 ~ r*8，玩家自伤 r*3。
     * 使用 SDV 原版半径计算伤害，但检测范围用 scaledRadius。
     */
    private void damageEntitiesInRadius(ServerLevel level, BlockPos center,
                                         BombType type, float scaledRadius) {
        AABB damageBox = new AABB(center).inflate(scaledRadius);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, damageBox);
        double radiusSq = scaledRadius * scaledRadius;
        double cx = center.getX() + 0.5, cy = center.getY() + 0.5, cz = center.getZ() + 0.5;

        for (LivingEntity entity : entities) {
            if (entity.distanceToSqr(cx, cy, cz) > radiusSq) continue;

            DamageSource source = level.damageSources().explosion(this, owner);

            if (entity instanceof Player) {
                entity.hurt(source, type.getPlayerDamage());
            } else if (entity instanceof Mob) {
                int damage = type.getMinDamage()
                    + random.nextInt(type.getMaxDamage() - type.getMinDamage() + 1);
                entity.hurt(source, damage);
            }
        }
    }

    /**
     * SDV: 爆炸粒子按炸弹类型区分。
     * Cherry Bomb: 小型爆炸，少量烟雾
     * Bomb: 中型爆炸 + 火焰+烟雾
     * Mega Bomb: 大型爆炸 + 大量火焰+烟雾+余烬
     */
    private void spawnExplosionParticles(ServerLevel level, BlockPos center,
                                         BombType type, float radius) {
        int r = (int) Math.ceil(radius);
        float radiusSq = radius * radius;

        // 核心爆炸大小取决于炸弹类型
        int emitterCount = switch (type) {
            case CHERRY_BOMB -> 1;
            case BOMB -> 2;
            case MEGA_BOMB -> 3;
        };
        for (int i = 0; i < emitterCount; i++) {
            double ox = (random.nextDouble() - 0.5) * radius * 0.5;
            double oz = (random.nextDouble() - 0.5) * radius * 0.5;
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                center.getX() + 0.5 + ox, center.getY() + 0.5, center.getZ() + 0.5 + oz,
                1, 0, 0, 0, 0);
        }

        // SDV: 45% 概率/格，随机碎片或粉尘
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                if (dx * dx + dz * dz > radiusSq) continue;
                if (random.nextFloat() > 0.45f) continue;

                double px = center.getX() + dx + 0.5 + (random.nextDouble() - 0.5) * 0.5;
                double py = center.getY() + 0.5 + random.nextDouble() * 1.5;
                double pz = center.getZ() + dz + 0.5 + (random.nextDouble() - 0.5) * 0.5;

                if (random.nextBoolean()) {
                    level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        px, py, pz, 1,
                        0.2, 0.3, 0.2, 0.01);
                } else {
                    level.sendParticles(ParticleTypes.POOF,
                        px, py - 0.3, pz, 1,
                        0.1, 0.2, 0.1, 0.02);
                }
            }
        }

        // Bomb / Mega Bomb: 额外火焰粒子（模拟 SDV 大规模爆炸火焰）
        if (type == BombType.BOMB || type == BombType.MEGA_BOMB) {
            int fireCount = type == BombType.MEGA_BOMB ? 20 : 8;
            for (int i = 0; i < fireCount; i++) {
                double fx = center.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
                double fy = center.getY() + 0.5 + random.nextDouble() * 2.0;
                double fz = center.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
                level.sendParticles(ParticleTypes.FLAME,
                    fx, fy, fz, 1,
                    0.05, 0.15, 0.05, 0.03);
            }
        }

        // Mega Bomb: 额外大型烟柱 + 余烬
        if (type == BombType.MEGA_BOMB) {
            for (int i = 0; i < 12; i++) {
                double sx = center.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 1.5;
                double sz = center.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 1.5;
                level.sendParticles(ParticleTypes.LARGE_SMOKE,
                    sx, center.getY() + 1.0, sz, 1,
                    0.1, 0.5, 0.1, 0.02);
            }
            for (int i = 0; i < 8; i++) {
                double lx = center.getX() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
                double lz = center.getZ() + 0.5 + (random.nextDouble() - 0.5) * radius * 2;
                level.sendParticles(ParticleTypes.LAVA,
                    lx, center.getY() + 0.5, lz, 1,
                    0, 0.05, 0, 0);
            }
        }
    }

    /* ── 序列化 ─────────────────────────────────────────── */

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("BombType")) {
            setBombType(BombType.fromOrdinal(tag.getInt("BombType")));
        }
        if (tag.contains("Fuse")) {
            entityData.set(DATA_FUSE, tag.getInt("Fuse"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("BombType", getBombType().ordinal());
        tag.putInt("Fuse", getFuse());
    }

    /* ── 杂项 ──────────────────────────────────────────── */

    @Override
    public boolean isPickable() { return false; }

    @Override
    public boolean isPushable() { return false; }
}
