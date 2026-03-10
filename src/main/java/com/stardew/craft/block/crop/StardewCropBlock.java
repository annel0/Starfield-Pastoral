package com.stardew.craft.block.crop;

import com.stardew.craft.block.shape.ModelVoxelShapeCache;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.item.quality.QualityHelper;
import com.stardew.craft.manager.FertilizerManager;
import com.stardew.craft.player.PlayerStardewDataAPI;
import com.stardew.craft.player.SkillType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;
import com.stardew.craft.manager.CropGrowthManager;
import javax.annotation.Nonnull;

/**
 * 星露谷作物基类
 * 完全照抄原版Crop.cs的机制
 */
public abstract class StardewCropBlock extends Block {
    
    // 作物生长阶段 (0-3, 4个阶段)
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    public static final int SEED_PHASE = 0;
    public static final int MAX_AGE = 3;
        private final boolean solidCollision;
        private final VoxelShape[] outlineShapeByAge;
        private volatile VoxelShape[] modelShapeByAge;
        private volatile boolean modelShapeResolved;
    
    protected StardewCropBlock(Properties properties) {
        this(properties, false);
    }

    /**
     * @param properties       block properties
     * @param solidCollision   true = 具有碰撞体积（用于藤架/作物架类作物）
     */
    @SuppressWarnings("null")
    protected StardewCropBlock(Properties properties, boolean solidCollision) {
        super(configureProperties(properties, solidCollision));
        this.solidCollision = solidCollision;
        this.outlineShapeByAge = buildOutlineShapes(getOutlineHeightsPxByAge(), getOutlineWidthsPxByAge(), solidCollision);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    private static Properties configureProperties(Properties properties, boolean solidCollision) {
        Properties p = properties.instabreak().noOcclusion();
        // 普通作物：不阻挡行走；藤架/作物架：需要碰撞。
        if (!solidCollision) {
            p = p.noCollission();
        }
        return p;
    }

    /**
     * 选框/轮廓高度（单位：像素，0..16），按 AGE=0..3。
     * 默认给一个更明显的高度变化，避免“鼠标边框很穿帮”。
     */
    protected int[] getOutlineHeightsPxByAge() {
        return new int[]{ 2, 6, 12, 16 };
    }

    /**
     * 选框/碰撞宽度（单位：像素，0..16），按 AGE=0..3。
     * 默认返回 null 表示使用满格 16。
     */
    protected int[] getOutlineWidthsPxByAge() {
        return null;
    }

    private static VoxelShape[] buildOutlineShapes(int[] heightsPx, int[] widthsPx, boolean solidCollision) {
        VoxelShape[] shapes = new VoxelShape[MAX_AGE + 1];
        for (int age = 0; age <= MAX_AGE; age++) {
            int h = 16;
            if (!solidCollision) {
                if (heightsPx != null && age < heightsPx.length) {
                    h = heightsPx[age];
                } else {
                    h = 16;
                }
            }
            h = Math.max(0, Math.min(16, h));
            int w = 16;
            if (widthsPx != null && age < widthsPx.length) {
                w = widthsPx[age];
            }
            w = Math.max(0, Math.min(16, w));

            double halfGap = (16.0 - w) / 2.0;
            double min = halfGap;
            double max = 16.0 - halfGap;
            shapes[age] = Block.box(min, 0.0, min, max, h, max);
        }
        return shapes;
    }

    private static int clampPositiveDays(int days) {
        return Math.max(1, days);
    }

    private static int clampRequiredDaysForPhase(int phase, int days) {
        // Stardew Valley: applySpeedIncreases 可能把某些阶段减到 0 天。
        // 但第 0 阶段至少保留 1 天（条件：j>0 || phaseDays[j]>1）。
        if (phase <= 0) {
            return Math.max(1, days);
        }
        return Math.max(0, days);
    }

    private static int getFarmingLevel(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return PlayerStardewDataAPI.getSkillLevel(serverPlayer, SkillType.FARMING);
        }
        return 0;
    }

    /**
     * 获取肥料等级（预留接口）
     */
    @SuppressWarnings("null")
    private static int getFertilizerLevel(ServerLevel level, BlockPos pos) {
        BlockPos farmPos = pos.below();
        if (!(level.getBlockState(farmPos).getBlock() instanceof net.minecraft.world.level.block.FarmBlock)) {
            return 0;
        }
        FertilizerManager manager = FertilizerManager.get(level);
        return manager.getQualityLevel(level, farmPos);
    }

    @SuppressWarnings("null")
    private static float getSpeedBoost(ServerLevel level, BlockPos pos) {
        BlockPos farmPos = pos.below();
        if (!(level.getBlockState(farmPos).getBlock() instanceof net.minecraft.world.level.block.FarmBlock)) {
            return 0f;
        }
        FertilizerManager manager = FertilizerManager.get(level);
        return manager.getSpeedBoost(level, farmPos);
    }

    private static int[] applySpeedGroToPhaseDays(int[] phaseDays, float speedBoost) {
        if (phaseDays == null || phaseDays.length == 0 || speedBoost <= 0f) {
            return phaseDays;
        }

    // 1:1 参考 Stardew Valley: HoeDirt.applySpeedIncreases
    // daysToRemove = ceil(totalDaysOfCropGrowth * speedIncrease)
    // 然后最多 3 次 pass，逐段 -1（第 0 段至少保留 1 天；其余段允许减到 0；99999 不处理）。
        int totalDaysOfCropGrowth = 0;
        for (int d : phaseDays) {
            totalDaysOfCropGrowth += Math.max(0, d);
        }

        int daysToRemove = (int) Math.ceil(totalDaysOfCropGrowth * (double) speedBoost);
        if (daysToRemove <= 0) {
            return phaseDays;
        }

        int[] result = phaseDays.clone();
        int tries = 0;
        while (daysToRemove > 0 && tries < 3) {
            for (int j = 0; j < result.length; j++) {
                int d = result[j];
                if (d <= 0) {
                    continue;
                }
                if (d == 99999) {
                    continue;
                }
                // 第 0 阶段至少保留1天
                if (j == 0 && d <= 1) {
                    continue;
                }

                result[j] = d - 1;
                daysToRemove--;
                if (daysToRemove <= 0) {
                    break;
                }
            }
            tries++;
        }

        return result;
    }

    private static int computeTotalDays(int[] phaseDays) {
        if (phaseDays == null || phaseDays.length == 0) {
            return 1;
        }
        int total = 0;
        for (int d : phaseDays) {
            total += Math.max(0, d);
        }
        return Math.max(1, total);
    }

    private static int computeGrownDays(int[] phaseDays, int phase, int dayInPhase) {
        if (phaseDays == null || phaseDays.length == 0) {
            return 0;
        }
        int p = Math.max(0, Math.min(phase, phaseDays.length));
        int grown = 0;
        for (int i = 0; i < p && i < phaseDays.length; i++) {
            grown += Math.max(0, phaseDays[i]);
        }
        grown += Math.max(0, dayInPhase);
        return Math.max(0, grown);
    }

    /**
     * 把“生长进度(天)”均分到 3 个贴图阶段 (0/1/2)，成熟时才显示 3。
     */
    private static int computeVisualAgeEvenly(int grownDays, int totalDays) {
        int total = Math.max(1, totalDays);
        int grown = Math.max(0, Math.min(grownDays, total));

        if (grown >= total) {
            return MAX_AGE;
        }

        // 0..(total-1) 映射到 0..2
        int age = (int) ((grown * 3L) / total);
        return Math.max(0, Math.min(MAX_AGE - 1, age));
    }

    /**
     * 该作物“完全成熟可收割”的判定。
     * 注意：我们的 AGE 只有 0-3，但 Wiki 的 phaseDays 是 4 段；
     * 因此 AGE==3 只是进入最后阶段，仍需等待 phaseDays[3] 天才算成熟。
     */
    @SuppressWarnings("null")
    protected boolean isMature(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.getValue(AGE) < MAX_AGE) {
            return false;
        }

        CropGrowthManager.CropGrowthState growthState = CropGrowthManager.get(level).getState(level, pos);
        if (growthState != null && growthState.regrowing) {
            return false;
        }

        // 使用真实 phase，避免把 AGE 当作 phase 造成“都只有 3 天/3 阶段”的错觉。
        if (growthState != null && growthState.phase < MAX_AGE) {
            return false;
        }

        int[] phaseDays = getPhaseDays();
        int last = (phaseDays != null && phaseDays.length > MAX_AGE) ? phaseDays[MAX_AGE] : 1;
        int required = Math.max(0, last);
        int dayInPhase = growthState != null ? growthState.dayInPhase : 0;
        return dayInPhase >= required;
    }
    
    @Override
    protected void createBlockStateDefinition(@SuppressWarnings("null") StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
        addExtraProperties(builder);
    }

    /**
     * 允许子类添加额外的方块状态属性（例如颜色变体）。
     */
    protected void addExtraProperties(StateDefinition.Builder<Block, BlockState> builder) {
        // no-op by default
    }
    
    /**
     * 检查是否可以存活（只能种在耕地上）
     */
    @SuppressWarnings("null")
    @Override
    protected boolean canSurvive(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") net.minecraft.world.level.LevelReader level, @SuppressWarnings("null") BlockPos pos) {
        BlockPos belowPos = pos.below();
        @SuppressWarnings("null")
        BlockState belowState = level.getBlockState(belowPos);
        @Nonnull Block block = belowState.getBlock();
        
        // 检查是否是耕地
        if (block instanceof net.minecraft.world.level.block.FarmBlock) {
            return true;
        }
        
        // 兼容其他模组的耕地
        String blockId = BuiltInRegistries.BLOCK.getKey(block).toString().toLowerCase();
        return blockId.contains("farmland");
    }
    
    @SuppressWarnings("null")
    @Override
    protected VoxelShape getShape(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") BlockGetter level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") CollisionContext context) {
        VoxelShape[] shapes = getResolvedShapeByAge(state);
        return shapes[state.getValue(AGE)];
    }

    @SuppressWarnings("null")
    @Override
    protected VoxelShape getCollisionShape(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") BlockGetter level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") CollisionContext context) {
        // 普通作物：无碰撞；藤架/作物架：给满格碰撞，避免穿帮与可穿过。
        if (!solidCollision) {
            return net.minecraft.world.phys.shapes.Shapes.empty();
        }
        VoxelShape[] shapes = getResolvedShapeByAge(state);
        return shapes[state.getValue(AGE)];
    }

    @SuppressWarnings("null")
    private VoxelShape[] getResolvedShapeByAge(BlockState state) {
        if (modelShapeResolved) {
            return modelShapeByAge != null ? modelShapeByAge : outlineShapeByAge;
        }

        synchronized (this) {
            if (modelShapeResolved) {
                return modelShapeByAge != null ? modelShapeByAge : outlineShapeByAge;
            }

            @SuppressWarnings("null")
            Block block = state.getBlock();
            String blockId = BuiltInRegistries.BLOCK.getKey(block).toString();
            VoxelShape[] resolved = new VoxelShape[MAX_AGE + 1];
            for (int age = 0; age <= MAX_AGE; age++) {
                String modelId = ModelVoxelShapeCache.variantModel(blockId, "age=" + age);
                if (modelId == null || modelId.isBlank()) {
                    resolved = null;
                    break;
                }
                resolved[age] = ModelVoxelShapeCache.shape(modelId);
            }

            modelShapeByAge = resolved;
            modelShapeResolved = true;
            return modelShapeByAge != null ? modelShapeByAge : outlineShapeByAge;
        }
    }

    /**
     * 更新方块形态（检查由于邻居更新导致的存活状态）
     * 解决下方方块被破坏后作物悬空不消失的问题
     */
    @SuppressWarnings("null")
    @Override
    protected BlockState updateShape(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") net.minecraft.core.Direction direction, @SuppressWarnings("null") BlockState neighborState, @SuppressWarnings("null") net.minecraft.world.level.LevelAccessor level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") BlockPos neighborPos) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
    
    @SuppressWarnings("null")
    @Override
    public ItemStack getCloneItemStack(@SuppressWarnings("null") net.minecraft.world.level.LevelReader level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") BlockState state) {
        // 返回作物物品作为图标（防风草）
        return new ItemStack(this.getCropItem().get());
    }

    /**
     * 右键收割成熟作物
     */
    @Override
    protected InteractionResult useWithoutItem(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") Player player, @SuppressWarnings("null") BlockHitResult hitResult) {
        // 只有空手才能尝试交互收割
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            return InteractionResult.PASS;
        }

        @SuppressWarnings("null")
        int currentAge = state.getValue(AGE);
        
        // 只有成熟时才能收割
        if (currentAge < MAX_AGE) {
            // 不成熟的作物，不做任何处理，直接消费事件防止其他交互
            return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        // AGE==3 但仍在最后阶段生长中（还没到 phaseDays[3]），也视为未成熟
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            if (!isMature(serverLevel, pos, state)) {
                return InteractionResult.CONSUME;
            }
        }
        
        // 成熟的作物，进行收割
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            harvest(serverLevel, pos, state, player);
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /**
     * 玩家左键破坏方块前触发
     * 如果是成熟作物，触发掉落逻辑
     */
    @SuppressWarnings("null")
    @Override
    public BlockState playerWillDestroy(@SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Player player) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            @SuppressWarnings("null")
            boolean mature = state.getValue(AGE) == MAX_AGE && isMature(serverLevel, pos, state);
            // 如果不是创造模式，生成掉落物
            if (mature && !player.isCreative()) {
                int farmingLevel = getFarmingLevel(player);
                int fertilizerLevel = getFertilizerLevel(serverLevel, pos);
                spawnHarvestDrops(serverLevel, pos, state, level.getRandom(), fertilizerLevel, farmingLevel);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    /**
     * 收割作物（照抄原版Crop.harvest逻辑）
     */
    @SuppressWarnings("null")
    protected void harvest(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        // 重新获取当前最新的state（防止使用旧state）
        @SuppressWarnings("null")
        BlockState currentState = level.getBlockState(pos);
        if (!(currentState.getBlock() instanceof StardewCropBlock)) {
            return; // 方块已经改变，停止收割
        }
        
        int farmingLevel = getFarmingLevel(player);
        int fertilizerLevel = getFertilizerLevel(level, pos);
        
        // 生成并掉落物品
        spawnHarvestDrops(level, pos, state, level.getRandom(), fertilizerLevel, farmingLevel);
        
        // 播放收割音效（原版harvest）
        level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        
        // 检查是否可以再生长
        if (canRegrow()) {
            // 重新生长：重置为regrow阶段
            int regrowAge = getRegrowAge();
            level.setBlock(pos, currentState.setValue(AGE, regrowAge), 3);

            // 标记为“再生长倒计时”状态，让每日生长按 regrowDays 计算
            CropGrowthManager.get(level).setRegrowing(level, pos, true);
        } else {
            // 一次性作物：移除方块
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    /**
     * 供工具（例如镰刀）触发的收割入口。
     * 返回是否实际发生了收割。
     */
    @SuppressWarnings("null")
    public boolean tryHarvestByTool(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        if (state.getValue(AGE) < MAX_AGE) {
            return false;
        }
        if (!isMature(level, pos, state)) {
            return false;
        }
        harvest(level, pos, state, player);
        return true;
    }

    /**
     * 生成并掉落收获物品（供右键收割和左键破坏共用）
     */
    @SuppressWarnings("null")
    protected void spawnHarvestDrops(ServerLevel level, BlockPos pos, BlockState state, RandomSource random, int fertilizerLevel, int farmingLevel) {
        int quality = getHarvestQuality(random, fertilizerLevel, farmingLevel);
        
        // 获取收获数量（基础1个）
        int numToHarvest = getHarvestCount(random, farmingLevel);
        
        // 创建作物物品
        ItemStack harvestItem = getHarvestItem(quality);
        harvestItem.setCount(numToHarvest);
        harvestItem = applyHarvestItemCustomization(harvestItem, state);
        
        // 掉落物品
        popResource(level, pos, harvestItem);
    }
    
    /**
     * 获取收获数量（基于farming等级和随机）
     */
    protected int getHarvestCount(RandomSource random, int farmingLevel) {
        // 原版逻辑：基础1个 + ExtraHarvestChance概率额外收获
        return 1;
    }
    
    /**
     * 是否可以再生长（如番茄、蓝莓等）
     */
    protected abstract boolean canRegrow();
    
    /**
     * 再生长时的年龄阶段
     */
    protected abstract int getRegrowAge();

    /**
     * 再生长天数（如番茄、蓝莓等）。一次性作物返回 0。
     */
    protected int getRegrowDays() {
        return 0;
    }

    /**
     * 给集成/显示层（如 Jade）用的公开访问器。
     * 这些方法只是转调 protected 抽象方法，避免破坏现有子类 override 签名。
     */
    public final int[] getPhaseDaysForDisplay() {
        return getPhaseDays();
    }

    public final boolean canRegrowForDisplay() {
        return canRegrow();
    }

    public final int getRegrowDaysForDisplay() {
        return getRegrowDays();
    }
    
    /**
     * 获取收获的物品（子类实现）
     */
    protected abstract ItemStack getHarvestItem(int quality);

    /**
     * 子类可在此根据方块状态调整收获物（例如写入颜色变体）。
     */
    protected ItemStack applyHarvestItemCustomization(ItemStack stack, BlockState state) {
        return stack;
    }

    /**
     * 花卉颜色变体 + 品质组合的模型编码。
     * custom_model_data = 100 + quality * 10 + color
     */
    @SuppressWarnings("null")
    protected static void setFlowerVariantModelData(ItemStack stack, int color) {
        int quality = QualityHelper.getQuality(stack);
        int cmd = 100 + (quality * 10) + Math.max(0, color);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA,
                new net.minecraft.world.item.component.CustomModelData(cmd));
    }
    
    /**
     * 获取作物显示名称（用于Jade等信息显示模组）
     */
    public abstract String getCropDisplayName();
    
    /**
     * 获取作物种子物品
     */
    protected abstract Supplier<Item> getSeedsItem();
    
    /**
     * 获取作物产物物品
     */
    protected abstract Supplier<Item> getCropItem();
    
    /**
     * 判断当前是否在合适的季节
     */
    protected abstract boolean isInSeason(Level level);
    
    /**
     * 获取phaseDays数组（每个阶段的天数）
     * 原版: phaseDays = {1, 1, 1, 1} 表示4个阶段各需1天
     */
    protected abstract int[] getPhaseDays();

    /**
     * 颜色变体属性（仅用于有多色成熟外观的作物）。
     */
    protected IntegerProperty getColorVariantProperty() {
        return null;
    }

    /**
     * 颜色变体数量（用于随机）。默认 1 表示无变体。
     */
    protected int getColorVariantCount() {
        return 1;
    }

    protected BlockState applyMatureVariant(ServerLevel level, BlockPos pos, BlockState state) {
        IntegerProperty color = getColorVariantProperty();
        int count = getColorVariantCount();
        if (color != null && count > 1) {
            int variant = level.getRandom().nextInt(count);
            return state.setValue(color, variant);
        }
        return state;
    }
    
    /**
     * 获取作物品质（基于随机+农业等级+肥料）
     * 完全照抄原版的harvest()方法的品质计算
     */
    protected int getHarvestQuality(RandomSource random, int fertilizerLevel, int farmingLevel) {
        // 原版代码:
        // double chanceForGoldQuality = 0.2 * (Game1.player.FarmingLevel / 10.0) + 0.2 * fertilizerQualityLevel * ((Game1.player.FarmingLevel + 2.0) / 12.0) + 0.01;
        // double chanceForSilverQuality = Math.Min(0.75, chanceForGoldQuality * 2.0);
        
        double chanceForGoldQuality = 0.2 * (farmingLevel / 10.0) + 0.2 * fertilizerLevel * ((farmingLevel + 2.0) / 12.0) + 0.01;
        double chanceForSilverQuality = Math.min(0.75, chanceForGoldQuality * 2.0);
        
        int quality = QualityHelper.NORMAL;
        
        // 铱星肥料 (fertilizerLevel >= 3) 有机会铱星
        if (fertilizerLevel >= 3 && random.nextDouble() < chanceForGoldQuality / 2.0) {
            quality = QualityHelper.IRIDIUM;
        }
        // 金星
        else if (random.nextDouble() < chanceForGoldQuality) {
            quality = QualityHelper.GOLD;
        }
        // 银星
        else if (random.nextDouble() < chanceForSilverQuality || fertilizerLevel >= 3) {
            quality = QualityHelper.SILVER;
        }
        
        return quality;
    }
    
    /**
     * 每日生长（照抄原版newDay()方法）
     * @param level 世界
     * @param pos 位置
     * @param state 当前方块状态
     * @param watered 是否被浇水了
     */
    @SuppressWarnings("null")
    public void growCropOneDay(ServerLevel level, BlockPos pos, BlockState state, boolean watered, CropGrowthManager.CropGrowthState growthState) {
        // 检查是否在星露谷维度
        if (level.dimension() != ModDimensions.STARDEW_VALLEY) {
            return;
        }
        
        // 检查季节
        if (!isInSeason(level)) {
            // 原版: Kill() -> replace with Dead Crop
            level.setBlock(pos, com.stardew.craft.block.ModBlocks.DEAD_CROP.get().defaultBlockState()
                    .setValue(com.stardew.craft.block.crop.DeadCropBlock.VARIANT, level.random.nextInt(4)), 3);
            return;
        }
        
        if (growthState == null) {
            // 理论上 manager 会传进来，这里兜底。
            return;
        }

        @SuppressWarnings("null")
        int currentAge = state.getValue(AGE);
        float speedBoost = getSpeedBoost(level, pos);
        int[] phaseDays = applySpeedGroToPhaseDays(getPhaseDays(), speedBoost);
        if (phaseDays == null || phaseDays.length <= 0) {
            return;
        }

        // 检查是否被浇水
        if (!watered) {
            return; // 没浇水就不生长
        }

        // 旧存档/未初始化：尽量从 AGE 推断一个 phase，避免显示/生长错乱。
        // 注意：AGE=3 只允许在“成熟可收割”时出现，因此 AGE=3 应被视为 phase=3 并视为成熟。
        if (growthState.phase == 0 && growthState.dayInPhase == 0 && !growthState.regrowing && currentAge > 0) {
            if (currentAge >= MAX_AGE) {
                growthState.phase = MAX_AGE;
                int last = (phaseDays.length > MAX_AGE) ? phaseDays[MAX_AGE] : 1;
                growthState.dayInPhase = Math.max(0, last);
            } else {
                growthState.phase = Math.min(currentAge, MAX_AGE - 1);
            }
        }

        // 再生作物：严格按 regrowDays 倒计时，不按 AGE/phaseDays 推进。
        if (growthState.regrowing && canRegrow()) {
            // Stardew Valley 的 Speed-Gro 不会通过 applySpeedIncreases 影响再生倒计时。
            int regrowDays = clampPositiveDays(getRegrowDays());
            growthState.dayInPhase++;

            if (growthState.dayInPhase >= regrowDays) {
                growthState.regrowing = false;
                growthState.dayInPhase = 0;

                // 再生结束：回到最后 phase，并切到成熟贴图(AGE=3)
                growthState.phase = MAX_AGE;

                // 回到可收割状态：AGE=3 并把 dayInPhase 置满最后阶段（用于成熟判定 & Jade 显示）
                int last = (phaseDays != null && phaseDays.length > MAX_AGE) ? phaseDays[MAX_AGE] : 1;
                int lastRequired = Math.max(0, last);
                growthState.dayInPhase = lastRequired;
                if (currentAge != MAX_AGE) {
                    @SuppressWarnings("null")
                    BlockState matureState = applyMatureVariant(level, pos, state).setValue(AGE, MAX_AGE);
                    level.setBlock(pos, matureState, 2);
                }
            } else {
                // 再生期间保持 regrowAge（通常是成熟植株外观），避免回到幼苗贴图
                int regrowAge = getRegrowAge();
                if (regrowAge < 0) regrowAge = 0;
                if (regrowAge > MAX_AGE - 1) regrowAge = MAX_AGE - 1;
                if (currentAge != regrowAge) {
                    level.setBlock(pos, state.setValue(AGE, regrowAge), 2);
                }
            }
            return;
        }

        // 一次性/非再生：按“真实 phase(0-3)”推进。
        // 要求：stage3(AGE=3) 只在成熟可收割时出现；成熟前的最后阶段保持 AGE=2。
        int phase = growthState.phase;
        if (phase < 0) phase = 0;
        if (phase > MAX_AGE) phase = MAX_AGE;

        if (phase < MAX_AGE) {
            int required = clampRequiredDaysForPhase(phase, phaseDays.length > phase ? phaseDays[phase] : 1);
            growthState.dayInPhase++;

            if (growthState.dayInPhase >= required) {
                growthState.phase = phase + 1;
                growthState.dayInPhase = 0;
            }

            // 贴图阶段均分：0/1/2 在整个生长期内尽量平均占用（成熟才显示 3）
            int totalDays = computeTotalDays(phaseDays);
            int grownDays = computeGrownDays(phaseDays, growthState.phase, growthState.dayInPhase);
            int targetAge = computeVisualAgeEvenly(grownDays, totalDays);

            if (state.getValue(AGE) != targetAge) {
                level.setBlock(pos, state.setValue(AGE, targetAge), 2);
            }
            return;
        }

        // phase==3：成熟前最后阶段。成熟前保持 AGE=2，成熟当日切到 AGE=3。
        int lastRequired = Math.max(0, phaseDays.length > MAX_AGE ? phaseDays[MAX_AGE] : 1);
        growthState.dayInPhase++;
        if (growthState.dayInPhase >= lastRequired) {
            growthState.dayInPhase = lastRequired;
            if (state.getValue(AGE) != MAX_AGE) {
                @SuppressWarnings("null")
                BlockState matureState = applyMatureVariant(level, pos, state).setValue(AGE, MAX_AGE);
                level.setBlock(pos, matureState, 2);
            }
        } else {
            int totalDays = computeTotalDays(phaseDays);
            int grownDays = computeGrownDays(phaseDays, MAX_AGE, growthState.dayInPhase);
            int targetAge = computeVisualAgeEvenly(grownDays, totalDays);
            // computeVisualAgeEvenly 在未成熟时不会返回 3，所以 stage3 仍只会在成熟出现
            if (state.getValue(AGE) != targetAge) {
                level.setBlock(pos, state.setValue(AGE, targetAge), 2);
            }
        }
    }
    
    /**
     * 检查能否存活
     */
    // @Override
    // protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
    //    // Removed unsafe onPlace check. Rely on canSurvive and updateShape.
    // }
    
    @SuppressWarnings("null")
    @Override
    protected void onPlace(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") BlockState oldState, boolean isMoving) {
        if (!state.is(oldState.getBlock())) {
            // 新放置 (或者方块类型改变)，注册到管理器
            if (level instanceof ServerLevel serverLevel) {
                com.stardew.craft.manager.CropGrowthManager.get(serverLevel).addCrop(serverLevel, pos);
            }
        }
        super.onPlace(state, level, pos, oldState, isMoving);
    }
    
    @SuppressWarnings("null")
    @Override
    protected void onRemove(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            // 被移除 (或者变成别的方块了)，从管理器移除
            if (level instanceof ServerLevel serverLevel) {
                com.stardew.craft.manager.CropGrowthManager.get(serverLevel).removeCrop(serverLevel, pos);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
