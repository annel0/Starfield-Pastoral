package com.stardew.craft.block.nature;

import com.mojang.serialization.MapCodec;
import com.stardew.craft.item.quality.QualityHelper;
import com.stardew.craft.player.PlayerStardewDataAPI;
import com.stardew.craft.player.ProfessionType;
import com.stardew.craft.player.SkillType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

/**
 * Generic forage block using cross model. Drops a specified item when broken.
 * Used for world-generated forage items (wild_horseradish, daffodil, etc.).
 *
 * <p>SDV parity:
 * <ul>
 *   <li>Quality determined by Foraging level (level/30 gold, level/15 silver)</li>
 *   <li>Botanist profession → always iridium quality</li>
 *   <li>Gatherer profession → 20% chance double harvest</li>
 *   <li>7 Foraging XP per pickup</li>
 * </ul>
 */
public class ForageBlock extends BushBlock {
    public static final MapCodec<ForageBlock> CODEC = simpleCodec(ForageBlock::new);

    /** Foraging XP granted per forage pickup (SDV: 7) */
    private static final int FORAGE_XP = 7;

    private Supplier<ItemStack> dropSupplier;

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    public ForageBlock(Properties properties) {
        super(properties);
    }

    public ForageBlock setDrop(Supplier<ItemStack> drop) {
        this.dropSupplier = drop;
        return this;
    }

    @SuppressWarnings("null")
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getBlock() instanceof FarmBlock
                || state.is(BlockTags.DIRT) || state.is(BlockTags.SAND)
                || state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.isFaceSturdy(level, pos, Direction.UP);
    }

    /**
     * Right-click to pick up forage (SDV: click to collect).
     * Same quality / gatherer / XP logic as breaking.
     */
    @SuppressWarnings("null")
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel
                && player instanceof ServerPlayer serverPlayer) {
            harvestForage(serverLevel, pos, serverPlayer);
            level.removeBlock(pos, false);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @SuppressWarnings("null")
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel
                && player instanceof ServerPlayer serverPlayer && !player.isCreative()) {
            harvestForage(serverLevel, pos, serverPlayer);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    /**
     * Shared harvest logic for both right-click pickup and block breaking.
     */
    @SuppressWarnings("null")
    private void harvestForage(ServerLevel level, BlockPos pos, ServerPlayer player) {
        if (dropSupplier == null) return;

        ItemStack drop = dropSupplier.get();

        // ---- Quality (SDV: GetHarvestSpawnedObjectQuality) ----
        int quality = determineQuality(player, level.getRandom());
        QualityHelper.setQuality(drop, quality);

        // ---- Drop the item ----
        popResource(level, pos, drop);

        // ---- Gatherer profession: 20% chance double harvest ----
        if (PlayerStardewDataAPI.hasProfession(player, ProfessionType.GATHERER)) {
            if (level.getRandom().nextFloat() < 0.2f) {
                ItemStack bonus = dropSupplier.get();
                QualityHelper.setQuality(bonus, quality);
                popResource(level, pos, bonus);
            }
        }

        // ---- Foraging XP ----
        PlayerStardewDataAPI.addExperience(player, SkillType.FORAGING, FORAGE_XP);
    }

    /**
     * SDV parity: GameLocation.GetHarvestSpawnedObjectQuality(isForage=true)
     * <ul>
     *   <li>Botanist (profession 16): always iridium</li>
     *   <li>level/30 chance → gold</li>
     *   <li>level/15 chance → silver</li>
     *   <li>else → normal</li>
     * </ul>
     */
    private static int determineQuality(ServerPlayer player, net.minecraft.util.RandomSource random) {
        // Botanist: always iridium
        if (PlayerStardewDataAPI.hasProfession(player, ProfessionType.BOTANIST)) {
            return QualityHelper.IRIDIUM;
        }

        int foragingLevel = PlayerStardewDataAPI.getSkillLevel(player, SkillType.FORAGING);

        // Gold: foragingLevel / 30 chance
        if (random.nextFloat() < foragingLevel / 30.0f) {
            return QualityHelper.GOLD;
        }
        // Silver: foragingLevel / 15 chance
        if (random.nextFloat() < foragingLevel / 15.0f) {
            return QualityHelper.SILVER;
        }
        return QualityHelper.NORMAL;
    }
}
