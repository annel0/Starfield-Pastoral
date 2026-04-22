package com.stardew.craft.block.mine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.serialization.MapCodec;

/**
 * 流沙方块 — 骷髅矿洞特色陷阱（重力方块）。
 * <p>
 * 继承 FallingBlock，像沙子一样受重力影响。
 * 类似细雪（PowderSnowBlock）的沉没机制：
 * - 实体进入后缓慢下沉 + 大幅移速降低
 * - 持续站立时受到窒息伤害
 * - 跳跃可以挣脱（但速度很慢）
 */
@SuppressWarnings("null")
public class QuicksandBlock extends FallingBlock {
    public static final MapCodec<QuicksandBlock> CODEC = simpleCodec(QuicksandBlock::new);

    @Override
    protected MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }
    /** 碰撞箱：比完整方块略矮，让实体"陷入" */
    private static final VoxelShape FALLING_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);

    /** 移动减速因子 */
    private static final Vec3 SLOW_FACTOR = new Vec3(0.25, 0.05, 0.25);

    /** 窒息伤害间隔（tick） */
    private static final int SUFFOCATION_INTERVAL = 40;

    /** 每次窒息伤害量 */
    private static final float SUFFOCATION_DAMAGE = 1.0F;

    /** 下落沙粒颜色（沙黄色） */
    private static final int DUST_COLOR = 0xDBC88B;

    public QuicksandBlock(Properties props) {
        super(props);
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return DUST_COLOR;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (ctx instanceof EntityCollisionContext eCtx) {
            Entity entity = eCtx.getEntity();
            if (entity != null) {
                if (entity.fallDistance > 2.5F) {
                    return FALLING_SHAPE;
                }
                if (isEntityInsideQuicksand(entity, pos)) {
                    return Shapes.empty();
                }
            }
        }
        return FALLING_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        // 下沉 + 减速
        entity.makeStuckInBlock(state, SLOW_FACTOR);

        if (!level.isClientSide) {
            if (entity instanceof LivingEntity living) {
                if (isEntityDeepInQuicksand(living, pos)) {
                    if (living.tickCount % SUFFOCATION_INTERVAL == 0) {
                        living.hurt(level.damageSources().inWall(), SUFFOCATION_DAMAGE);
                    }
                }
            }

            // 阻止着火（流沙是湿的）
            if (entity.isOnFire()) {
                entity.clearFire();
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // 偶尔冒出沙粒粒子
        if (random.nextInt(16) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + random.nextDouble();
            level.addParticle(
                    new BlockParticleOption(ParticleTypes.FALLING_DUST, state),
                    x, y, z, 0.0, 0.0, 0.0);
        }
    }

    private static boolean isEntityInsideQuicksand(Entity entity, BlockPos pos) {
        return entity.getY() < (double) pos.getY() + 0.9
                && entity.getBoundingBox().maxY > (double) pos.getY();
    }

    private static boolean isEntityDeepInQuicksand(LivingEntity entity, BlockPos pos) {
        return entity.getEyeY() < (double) pos.getY() + 1.0;
    }
}
