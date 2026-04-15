package com.stardew.craft.block.crop;

import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.manager.CropGrowthManager;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Supplier;

/**
 * 野生种子作物方块 — 种下后经历2个生长阶段，成熟后自动变成对应季节的采集物方块(ForageBlock)。
 * SDV: Crops.json phases [3,4], SpriteIndex 23, 成熟后 newDay() 替换为地面采集物。
 * 复用 broccoli 的 stage0/stage1 cross 贴图作为生长阶段外观。
 */
@SuppressWarnings("null")
public class WildSeedCropBlock extends StardewCropBlock {

    /** SDV wild seed crop: 2 growth phases, 3 days + 4 days = 7 days total */
    private static final int[] PHASE_DAYS = new int[]{3, 4};

    private final int season; // 0=spring, 1=summer, 2=fall, 3=winter
    private final Supplier<Item> seedsItem;

    public WildSeedCropBlock(int season, Supplier<Item> seedsItem) {
        super(Properties.of()
                .mapColor(MapColor.PLANT)
                .pushReaction(PushReaction.DESTROY)
                .sound(SoundType.CROP));
        this.season = season;
        this.seedsItem = seedsItem;
    }

    @Override
    protected int[] getPhaseDays() {
        return PHASE_DAYS;
    }

    @Override
    protected Supplier<Item> getSeedsItem() {
        return seedsItem;
    }

    @Override
    protected Supplier<Item> getCropItem() {
        return seedsItem;
    }

    @Override
    protected boolean isInSeason(Level level) {
        if (level.isClientSide()) return true;
        return StardewTimeManager.get().getCurrentSeason() == season;
    }

    @Override
    protected ItemStack getHarvestItem(int quality) {
        return new ItemStack(seedsItem.get());
    }

    @Override
    protected boolean canRegrow() {
        return false;
    }

    @Override
    protected int getRegrowAge() {
        return 0;
    }

    @Override
    public String getCropDisplayName() {
        return switch (season) {
            case 0 -> "春季野生种子";
            case 1 -> "夏季野生种子";
            case 2 -> "秋季野生种子";
            case 3 -> "冬季野生种子";
            default -> "野生种子";
        };
    }

    /**
     * Override daily growth: when the crop reaches maturity,
     * transform it into a random seasonal ForageBlock.
     */
    @SuppressWarnings("null")
    @Override
    public void growCropOneDay(ServerLevel level, BlockPos pos, BlockState state,
                               boolean watered, CropGrowthManager.CropGrowthState growthState) {
        super.growCropOneDay(level, pos, state, watered, growthState);

        // Check if we just reached maturity
        BlockState currentState = level.getBlockState(pos);
        if (currentState.getBlock() == this && currentState.getValue(AGE) == MAX_AGE) {
            transformToForage(level, pos);
        }
    }

    /**
     * Replace this crop block with a random ForageBlock for the corresponding season.
     * ForageBlock.mayPlaceOn now supports FarmBlock, so forage survives on farmland.
     */
    @SuppressWarnings("null")
    private void transformToForage(ServerLevel level, BlockPos pos) {
        Block forageBlock = pickRandomForage(level.getRandom());
        if (forageBlock != null) {
            level.setBlock(pos, forageBlock.defaultBlockState(), 3);
        }
    }

    /**
     * SDV Crop.getRandomWildCropForSeason — picks a random forage block.
     */
    private Block pickRandomForage(net.minecraft.util.RandomSource random) {
        return switch (season) {
            case 0 -> switch (random.nextInt(4)) {
                case 0 -> ModBlocks.FORAGE_WILD_HORSERADISH.get();
                case 1 -> ModBlocks.FORAGE_DAFFODIL.get();
                case 2 -> ModBlocks.FORAGE_LEEK.get();
                default -> ModBlocks.FORAGE_DANDELION.get();
            };
            case 1 -> switch (random.nextInt(3)) {
                case 0 -> ModBlocks.FORAGE_SPICE_BERRY.get();
                case 1 -> ModBlocks.FORAGE_SWEET_PEA.get();
                default -> lookupForageBlock("grape");
            };
            case 2 -> switch (random.nextInt(4)) {
                case 0 -> ModBlocks.FORAGE_COMMON_MUSHROOM.get();
                case 1 -> ModBlocks.FORAGE_WILD_PLUM.get();
                case 2 -> ModBlocks.FORAGE_HAZELNUT.get();
                default -> ModBlocks.FORAGE_BLACKBERRY.get();
            };
            case 3 -> switch (random.nextInt(4)) {
                case 0 -> ModBlocks.FORAGE_WINTER_ROOT.get();
                case 1 -> ModBlocks.FORAGE_CRYSTAL_FRUIT.get();
                case 2 -> lookupForageBlock("snow_yam");
                default -> ModBlocks.FORAGE_CROCUS.get();
            };
            default -> null;
        };
    }

    @SuppressWarnings("null")
    private static Block lookupForageBlock(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("stardewcraft", "forage_" + name);
        if (BuiltInRegistries.BLOCK.containsKey(id)) {
            return BuiltInRegistries.BLOCK.get(id);
        }
        return null;
    }
}
