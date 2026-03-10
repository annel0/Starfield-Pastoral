# ============================================================================
# 星露谷物语作物批量生成脚本
# ============================================================================
# 功能：
# 1. 读取作物数据文件
# 2. 从资源包复制贴图文件（crops和seedbag文件夹）
# 3. 生成Java类文件（Item、SeedItem、CropBlock）
# 4. 更新注册文件（ModItems.java、ModBlocks.java）
# ============================================================================

# 配置路径
$projectRoot = "E:\stardewcraft-template-1.21.1"
$resourcePackRoot = "D:\MC\.minecraft\versions\1.21.3-Fabric 0.18.1\resourcepacks\StardewRes"
$cropDataFile = "$projectRoot\scripts\crop_data.txt"

# 目标路径
$itemPath = "$projectRoot\src\main\java\com\stardew\craft\item"
$blockPath = "$projectRoot\src\main\java\com\stardew\craft\block\crop"
$texturePath = "$projectRoot\src\main\resources\assets\stardewcraft\textures\item"

# 确保目录存在
if (!(Test-Path $texturePath)) {
    New-Item -Path $texturePath -ItemType Directory -Force | Out-Null
}
if (!(Test-Path "$texturePath\crops")) {
    New-Item -Path "$texturePath\crops" -ItemType Directory -Force | Out-Null
}
if (!(Test-Path "$texturePath\seedbag")) {
    New-Item -Path "$texturePath\seedbag" -ItemType Directory -Force | Out-Null
}

# 季节映射
$seasonMap = @{
    "0" = "春季"
    "1" = "夏季"
    "2" = "秋季"
    "3" = "冬季"
}

# 季节检查代码生成
function Get-SeasonCheck {
    param($seasonStr, $varName = "season")
    
    $seasons = $seasonStr -split ','
    if ($seasons.Length -eq 1) {
        return "timeManager.getCurrentSeason() == $($seasons[0])"
    }
    else {
        $checks = $seasons | ForEach-Object { "$varName == $_" }
        return "(" + ($checks -join " || ") + ")"
    }
}

# 生成季节注释
function Get-SeasonComment {
    param($seasonStr)
    
    $seasons = $seasonStr -split ','
    $names = $seasons | ForEach-Object { $seasonMap[$_] }
    return $names -join "/"
}

# 首字母大写
function Capitalize {
    param($str)
    return $str.Substring(0,1).ToUpper() + $str.Substring(1)
}

# 生成作物Item类
function Generate-CropItem {
    param($cropName, $englishName, $price, $growthDays)
    
    $className = (Capitalize $englishName) + "Item"
    $filepath = "$itemPath\$className.java"
    
    # 根据价格计算能量和生命恢复
    $baseHealth = [math]::Max(5, [math]::Floor($price / 5))
    $baseEnergy = [math]::Max(10, [math]::Floor($price / 2))
    
    $content = @"
package com.stardew.craft.item;

import com.stardew.craft.item.quality.QualityHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * $cropName - 作物
 * 生长周期：${growthDays}天
 * 售价：${price}g (普通)
 */
public class $className extends Item implements IStardewItem {
    
    // 基础属性
    private static final int BASE_PRICE = $price;
    private static final int BASE_HEALTH = $baseHealth;
    private static final int BASE_ENERGY = $baseEnergy;
    
    public ${className}(Item.Properties properties) {
        super(properties
                .food(new FoodProperties.Builder()
                        .nutrition(2)
                        .saturationModifier(0.3f)
                        .alwaysEdible()
                        .build())
        );
    }
    
    @Override
    public Component getName(ItemStack stack) {
        int quality = QualityHelper.getQuality(stack);
        Component prefix = QualityHelper.getQualityPrefix(quality);
        Component baseName = Component.translatable(this.getDescriptionId(stack))
                .withStyle(ChatFormatting.WHITE);
        
        // 设置CustomModelData用于显示星星
        var customData = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA,
                net.minecraft.world.item.component.CustomModelData.DEFAULT);
        if (quality != QualityHelper.NORMAL && customData.equals(net.minecraft.world.item.component.CustomModelData.DEFAULT)) {
            stack.set(net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA, 
                    new net.minecraft.world.item.component.CustomModelData(quality));
        }
        
        if (quality == QualityHelper.NORMAL) {
            return baseName;
        }
        
        return Component.empty().append(prefix).append(baseName);
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, net.minecraft.world.level.Level level, net.minecraft.world.entity.LivingEntity livingEntity) {
        int quality = QualityHelper.getQuality(stack);
        int health = getHealthRestoration(quality);
        int energy = getEnergyRestoration(quality);
        
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        
        if (!level.isClientSide && livingEntity instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            if (health > 0) {
                int currentSDHealth = com.stardew.craft.player.PlayerStardewDataAPI.getHealth(serverPlayer);
                int maxSDHealth = com.stardew.craft.player.PlayerStardewDataAPI.getMaxHealth(serverPlayer);
                com.stardew.craft.player.PlayerStardewDataAPI.setHealth(serverPlayer, Math.min(maxSDHealth, currentSDHealth + health));
            }
            
            if (energy > 0) {
                com.stardew.craft.player.PlayerStardewDataAPI.restoreEnergy(serverPlayer, energy);
            }
        }
        
        return result;
    }

    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.crop";
    }

    @Override
    public int getSellPrice(ItemStack stack) {
        return getSellPrice(QualityHelper.getQuality(stack));
    }

    @Override
    public boolean isFood() {
        return true;
    }

    @Override
    public int getHealthRestoration() {
        return BASE_HEALTH;
    }

    @Override
    public int getEnergyRestoration() {
        return BASE_ENERGY;
    }

    private int getSellPrice(int quality) {
        return (int) (BASE_PRICE * QualityHelper.getPriceMultiplier(quality));
    }

    private int getHealthRestoration(int quality) {
        return (int) (BASE_HEALTH * QualityHelper.getHealthEnergyMultiplier(quality));
    }

    private int getEnergyRestoration(int quality) {
        return (int) (BASE_ENERGY * QualityHelper.getHealthEnergyMultiplier(quality));
    }
}
"@
    
    Set-Content -Path $filepath -Value $content -Encoding UTF8
    Write-Host "✓ 生成: $filepath" -ForegroundColor Green
}

# 生成种子Item类
function Generate-SeedItem {
    param($cropName, $englishName, $season, $seedPrice)
    
    $className = (Capitalize $englishName) + "SeedItem"
    $cropBlockName = (Capitalize $englishName).ToUpper() + "_CROP"
    $filepath = "$itemPath\$className.java"
    
    $seasonComment = Get-SeasonComment $season
    $seasonCheck = Get-SeasonCheck $season "StardewTimeManager.get().getCurrentSeason()"
    
    $content = @"
package com.stardew.craft.item;

import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * ${cropName}种子
 * ${seasonComment}作物
 */
public class $className extends Item implements IStardewItem {
    
    public ${className}(Item.Properties properties) {
        super(properties);
    }

    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.seed";
    }

    @Override
    public int getSellPrice(ItemStack stack) {
        return $seedPrice;
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(pos);
        
        if (!isFarmland(clickedState)) {
            return InteractionResult.PASS;
        }
        
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        if (!aboveState.isAir()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            // 季节检查
            if (!($seasonCheck)) {
                if (context.getPlayer() != null) {
                    context.getPlayer().displayClientMessage(
                            net.minecraft.network.chat.Component.translatable("stardewcraft.message.seed.wrong_season"),
                            true);
                }
                return InteractionResult.FAIL;
            }
        }
        
        if (!level.isClientSide) {
            level.setBlock(abovePos, ModBlocks.$cropBlockName.get().defaultBlockState(), 3);
            
            level.playSound(null, abovePos, 
                net.minecraft.sounds.SoundEvents.HOE_TILL, 
                net.minecraft.sounds.SoundSource.BLOCKS, 
                1.0F, 1.0F);
            
            context.getItemInHand().shrink(1);
        }
        
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    
    private boolean isFarmland(BlockState state) {
        Block block = state.getBlock();
        
        if (block instanceof FarmBlock) {
            return true;
        }
        
        String blockId = block.builtInRegistryHolder().key().location().toString().toLowerCase();
        return blockId.contains("farmland");
    }
}
"@
    
    Set-Content -Path $filepath -Value $content -Encoding UTF8
    Write-Host "✓ 生成: $filepath" -ForegroundColor Green
}

# 生成作物Block类
function Generate-CropBlock {
    param($cropName, $englishName, $season, $growthDays, $canRegrow, $regrowDays)
    
    $className = (Capitalize $englishName) + "CropBlock"
    $filepath = "$blockPath\$className.java"
    
    $seasonComment = Get-SeasonComment $season
    $seasonCheck = Get-SeasonCheck $season "timeManager.getCurrentSeason()"
    
    # 计算每个阶段的天数（4个阶段）
    $totalDays = $growthDays
    $avgDays = [math]::Floor($totalDays / 4.0)
    $remainder = $totalDays % 4
    
    $phaseDays = @()
    for ($i = 0; $i -lt 4; $i++) {
        if ($i -lt $remainder) {
            $phaseDays += ($avgDays + 1)
        } else {
            $phaseDays += $avgDays
        }
    }
    $phaseDaysStr = "{" + ($phaseDays -join ", ") + "}"
    
    $regrowComment = if ($canRegrow -eq "true") { 
        "Regrows in ${regrowDays} days" 
    } else { 
        "Single harvest" 
    }
    
    $itemName = (Capitalize $englishName).ToUpper()
    
    $content = @"
package com.stardew.craft.block.crop;

import com.stardew.craft.item.ModItems;
import com.stardew.craft.item.quality.QualityHelper;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Supplier;

/**
 * $cropName
 * ${seasonComment}作物，${growthDays}天生长周期
 * $regrowComment
 */
public class $className extends StardewCropBlock {
    
    private static final int[] PHASE_DAYS = $phaseDaysStr;
    
    public ${className}() {
        super(Properties.of()
                .mapColor(MapColor.PLANT)
                .pushReaction(PushReaction.DESTROY)
                .sound(SoundType.CROP));
    }
    
    @Override
    protected Supplier<Item> getSeedsItem() {
        return ModItems.${itemName}_SEEDS;
    }
    
    @Override
    protected Supplier<Item> getCropItem() {
        return ModItems.$itemName;
    }
    
    @Override
    protected boolean isInSeason(Level level) {
        if (level.isClientSide()) {
            return true;
        }
        
        StardewTimeManager timeManager = StardewTimeManager.get();
        int season = timeManager.getCurrentSeason();
        return $seasonCheck;
    }
    
    @Override
    protected int[] getPhaseDays() {
        return PHASE_DAYS;
    }
    
    @Override
    protected ItemStack getHarvestItem(int quality) {
        ItemStack stack = new ItemStack(ModItems.$itemName.get());
        QualityHelper.setQuality(stack, quality);
        return stack;
    }
    
    @Override
    protected boolean canRegrow() {
        return $canRegrow;
    }
    
    @Override
    protected int getRegrowAge() {
        return $regrowDays;
    }
    
    @Override
    public String getCropDisplayName() {
        return "$cropName";
    }
}
"@
    
    Set-Content -Path $filepath -Value $content -Encoding UTF8
    Write-Host "✓ 生成: $filepath" -ForegroundColor Green
}

# 复制贴图文件
function Copy-CropTextures {
    param($englishName)
    
    $sourceCropDir = "$resourcePackRoot\assets\minecraft\textures\item\crops\$englishName"
    $targetCropDir = "$texturePath\crops\$englishName"
    
    if (Test-Path $sourceCropDir) {
        if (!(Test-Path $targetCropDir)) {
            New-Item -Path $targetCropDir -ItemType Directory -Force | Out-Null
        }
        Copy-Item "$sourceCropDir\*" -Destination $targetCropDir -Force
        Write-Host "  → 复制贴图: $englishName (crops)" -ForegroundColor Cyan
    } else {
        Write-Host "  ⚠ 未找到贴图: $sourceCropDir" -ForegroundColor Yellow
    }
    
    $sourceSeedDir = "$resourcePackRoot\assets\minecraft\textures\item\seedbag\$englishName"
    $targetSeedDir = "$texturePath\seedbag\$englishName"
    
    if (Test-Path $sourceSeedDir) {
        if (!(Test-Path $targetSeedDir)) {
            New-Item -Path $targetSeedDir -ItemType Directory -Force | Out-Null
        }
        Copy-Item "$sourceSeedDir\*" -Destination $targetSeedDir -Force
        Write-Host "  → 复制贴图: $englishName (seedbag)" -ForegroundColor Cyan
    }
}

# 主流程
Write-Host "`n========================================" -ForegroundColor Magenta
Write-Host "  星露谷物语作物批量生成" -ForegroundColor Magenta
Write-Host "========================================`n" -ForegroundColor Magenta

# 读取作物数据
$crops = @()
$lines = Get-Content $cropDataFile -Encoding UTF8
foreach ($line in $lines) {
    if ($line.StartsWith("#") -or $line.Trim() -eq "") {
        continue
    }
    
    $parts = $line -split '\|'
    if ($parts.Length -ge 6) {
        $crops += [PSCustomObject]@{
            Name = $parts[0]
            EnglishName = $parts[1]
            Season = $parts[2]
            GrowthDays = [int]$parts[3]
            Price = [int]$parts[4]
            CanRegrow = $parts[5]
            RegrowDays = [int]$parts[6]
        }
    }
}

Write-Host "共找到 $($crops.Count) 个作物`n" -ForegroundColor Yellow

# 生成所有作物
$itemRegistrations = @()
$blockRegistrations = @()

foreach ($crop in $crops) {
    Write-Host "`n处理: $($crop.Name) ($($crop.EnglishName))" -ForegroundColor Cyan
    
    # 跳过已存在的防风草（示例）
    if ($crop.EnglishName -eq "parsnip") {
        Write-Host "  → 跳过 (已存在)" -ForegroundColor Gray
        continue
    }
    
    # 生成类文件
    Generate-CropItem $crop.Name $crop.EnglishName $crop.Price $crop.GrowthDays
    $seedPrice = [math]::Floor($crop.Price / 2)
    Generate-SeedItem $crop.Name $crop.EnglishName $crop.Season $seedPrice
    Generate-CropBlock $crop.Name $crop.EnglishName $crop.Season $crop.GrowthDays $crop.CanRegrow $crop.RegrowDays
    
    # 复制贴图
    Copy-CropTextures $crop.EnglishName
    
    # 记录注册信息
    $upperName = $crop.EnglishName.ToUpper()
    $itemClass = (Capitalize $crop.EnglishName) + "Item"
    $seedClass = (Capitalize $crop.EnglishName) + "SeedItem"
    $blockClass = (Capitalize $crop.EnglishName) + "CropBlock"
    
    $itemRegistrations += "    public static final DeferredItem<Item> $upperName = ITEMS.register(`"$($crop.EnglishName)`", () -> new ${itemClass}(new Item.Properties()));"
    $itemRegistrations += "    public static final DeferredItem<Item> ${upperName}_SEEDS = ITEMS.register(`"$($crop.EnglishName)_seeds`", () -> new ${seedClass}(new Item.Properties()));"
    $itemRegistrations += ""
    
    $blockRegistrations += "    public static final DeferredBlock<Block> ${upperName}_CROP = BLOCKS.register(`"$($crop.EnglishName)_crop`", () -> new ${blockClass}());"
    $blockRegistrations += ""
}

# 输出注册代码
Write-Host "`n========================================" -ForegroundColor Magenta
Write-Host "  注册代码（添加到ModItems.java）" -ForegroundColor Magenta
Write-Host "========================================`n" -ForegroundColor Magenta
$itemRegistrations | ForEach-Object { Write-Host $_ -ForegroundColor White }

Write-Host "`n========================================" -ForegroundColor Magenta
Write-Host "  注册代码（添加到ModBlocks.java）" -ForegroundColor Magenta
Write-Host "========================================`n" -ForegroundColor Magenta
$blockRegistrations | ForEach-Object { Write-Host $_ -ForegroundColor White }

# 保存注册代码到文件
$itemRegFile = "$projectRoot\scripts\item_registrations.txt"
$blockRegFile = "$projectRoot\scripts\block_registrations.txt"

$itemRegistrations | Out-File -FilePath $itemRegFile -Encoding UTF8
$blockRegistrations | Out-File -FilePath $blockRegFile -Encoding UTF8

Write-Host "`n✓ 注册代码已保存到：" -ForegroundColor Green
Write-Host "  - $itemRegFile" -ForegroundColor Gray
Write-Host "  - $blockRegFile" -ForegroundColor Gray

Write-Host "`n========================================" -ForegroundColor Magenta
Write-Host "  完成！" -ForegroundColor Magenta
Write-Host "========================================`n" -ForegroundColor Magenta
