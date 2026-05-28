package com.stardew.craft.block.decor;

import com.stardew.craft.blockentity.LuauFestivalDecorBlockEntity;
import com.stardew.craft.festival.LuauFestivalService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Consumer;

@SuppressWarnings("null")
public class LuauGeoFestivalDecorBlock extends MapDecorStaticBlock implements EntityBlock {
    public LuauGeoFestivalDecorBlock(Properties properties, String modelId) {
        super(properties, modelId);
    }

    public LuauGeoFestivalDecorBlock(Properties properties, String modelId,
                                    double minX, double minY, double minZ,
                                    double maxX, double maxY, double maxZ) {
        super(properties, modelId, minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected boolean isPathfindable(@Nonnull BlockState state, @Nonnull PathComputationType type) {
        return false;
    }

    @Override
    protected ItemInteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level,
                                             @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand,
                                             @Nonnull BlockHitResult hit) {
        if (level.isClientSide) {
            return ItemInteractionResult.sidedSuccess(true);
        }
        if (player instanceof ServerPlayer serverPlayer && LuauFestivalService.tryOpenSoupContribution(serverPlayer, pos, hand)) {
            return ItemInteractionResult.sidedSuccess(false);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos,
                                               @Nonnull Player player, @Nonnull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer && LuauFestivalService.tryOpenSoupContribution(serverPlayer, pos, InteractionHand.MAIN_HAND)) {
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        if (state.getValue(PART) != Part.MAIN) {
            return null;
        }
        return new LuauFestivalDecorBlockEntity(pos, state);
    }

    @Override
    public void initializeClient(@Nonnull Consumer<net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions> consumer) {
        consumer.accept(new net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions() {
            @Override
            @SuppressWarnings("null")
            public boolean addHitEffects(BlockState state, Level level, net.minecraft.world.phys.HitResult target,
                                         net.minecraft.client.particle.ParticleEngine manager) {
                if (target instanceof net.minecraft.world.phys.BlockHitResult blockHit) {
                    spawnItemCrack(level, blockHit.getLocation().x, blockHit.getLocation().y, blockHit.getLocation().z,
                        blockHit.getDirection(), 4);
                }
                return true;
            }

            @Override
            @SuppressWarnings("null")
            public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos,
                                             net.minecraft.client.particle.ParticleEngine manager) {
                BlockPos mainPos = LuauGeoFestivalDecorBlock.this.findMainPos(level, pos, state);
                BlockPos origin = mainPos != null ? mainPos : pos;
                Direction facing = state.hasProperty(FACING) ? state.getValue(FACING) : Direction.NORTH;
                Random random = new Random();
                for (CellOffset offset : LuauGeoFestivalDecorBlock.this.occupiedOffsets(facing)) {
                    BlockPos particlePos = origin.offset(offset.dx(), offset.dy(), offset.dz());
                    spawnItemCrack(level,
                        particlePos.getX() + 0.1D + random.nextDouble() * 0.8D,
                        particlePos.getY() + 0.1D + random.nextDouble() * 0.8D,
                        particlePos.getZ() + 0.1D + random.nextDouble() * 0.8D,
                        null, 2);
                }
                return true;
            }

            private void spawnItemCrack(Level level, double x, double y, double z, @Nullable Direction face, int count) {
                net.minecraft.core.particles.ItemParticleOption option = new net.minecraft.core.particles.ItemParticleOption(
                    net.minecraft.core.particles.ParticleTypes.ITEM, new ItemStack(LuauGeoFestivalDecorBlock.this));
                Random random = new Random();
                for (int i = 0; i < count; i++) {
                    double dx = x;
                    double dy = y;
                    double dz = z;
                    if (face != null) {
                        dx += face.getStepX() * 0.05D;
                        dy += face.getStepY() * 0.05D;
                        dz += face.getStepZ() * 0.05D;
                    }
                    level.addParticle(option, dx, dy, dz,
                        (random.nextDouble() - 0.5D) * 0.25D,
                        random.nextDouble() * 0.15D,
                        (random.nextDouble() - 0.5D) * 0.25D);
                }
            }
        });
    }
}
