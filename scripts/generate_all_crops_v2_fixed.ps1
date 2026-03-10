﻿# ========================================================
# 鏄熼湶璋风墿璇綔鐗╂壒閲忕敓鎴愯剼鏈?v2
# 瀹屽叏鐓ф妱闃查鑽夌殑閫昏緫锛屽寘鎷敓鍛藉拰鑳介噺鎭㈠
# ========================================================

$projectRoot = "E:\stardewcraft-template-1.21.1"
$resourcePackRoot = "D:\MC\.minecraft\versions\1.21.3-Fabric 0.18.1\resourcepacks\StardewRes"
$cropDataFile = "$projectRoot\scripts\crop_data.txt"

$itemPath = "$projectRoot\src\main\java\com\stardew\craft\item"
$blockPath = "$projectRoot\src\main\java\com\stardew\craft\block\crop"

# 棣栧瓧姣嶅ぇ鍐?
function Capitalize {
    param($str)
    return $str.Substring(0,1).ToUpper() + $str.Substring(1)
}

# 瀛ｈ妭鏄犲皠
$seasonMap = @{
    "0" = "鏄ュ"
    "1" = "澶忓"
    "2" = "绉嬪"
    "3" = "鍐"
}

# 鐢熸垚瀛ｈ妭娉ㄩ噴
function Get-SeasonComment {
    param($seasonStr)
    $seasons = $seasonStr -split ','
    $names = $seasons | ForEach-Object { $seasonMap[$_] }
    return $names -join "/"
}

# 鐢熸垚瀛ｈ妭妫€鏌ヤ唬鐮?
function Get-SeasonCheck {
    param($seasonStr, $varName = "season")
    $seasons = $seasonStr -split ','
    if ($seasons.Length -eq 1) {
        return "$varName == $($seasons[0])"
    }
    else {
        $checks = $seasons | ForEach-Object { "$varName == $_" }
        return "(" + ($checks -join " || ") + ")"
    }
}

# 鐢熸垚浣滅墿Item绫伙紙瀹屽叏鐓ф妱ParsnipItem锛?
function Generate-CropItem {
    param($crop)
    
    $className = (Capitalize $crop.EnglishName) + "Item"
    $filepath = "$itemPath\$className.java"
    
    $seasonComment = Get-SeasonComment $crop.Season
    
    $content = @"
package com.stardew.craft.item;

import com.stardew.craft.item.quality.QualityHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * $($crop.ChineseName) - ${seasonComment}浣滅墿
 * 鐢熼暱鍛ㄦ湡锛?($crop.GrowthDays)澶?
 * 鍞环锛?($crop.BasePrice)g (鏅€?
 */
public class $className extends Item implements IStardewItem {
    
    // 鍩虹灞炴€?
    private static final int BASE_PRICE = $($crop.BasePrice);
    private static final int BASE_HEALTH = $($crop.BaseHealth);
    private static final int BASE_ENERGY = $($crop.BaseEnergy);
    
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
        
        // 璁剧疆CustomModelData鐢ㄤ簬鏄剧ず鏄熸槦
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
    public int getHealth(ItemStack stack) {
        return getHealthRestoration(QualityHelper.getQuality(stack));
    }

    @Override
    public int getEnergy(ItemStack stack) {
        return getEnergyRestoration(QualityHelper.getQuality(stack));
    }
    
    /**
     * 鑾峰彇鎭㈠鐨勭敓鍛藉€硷紙鏍规嵁鍝佽川锛?
     */
    public static int getHealthRestoration(int quality) {
        return (int)(BASE_HEALTH * QualityHelper.getPriceMultiplier(quality));
    }
    
    /**
     * 鑾峰彇鎭㈠鐨勮兘閲忓€硷紙鏍规嵁鍝佽川锛?
     */
    public static int getEnergyRestoration(int quality) {
        return (int)(BASE_ENERGY * QualityHelper.getPriceMultiplier(quality));
    }
    
    /**
     * 鑾峰彇鍞环锛堟牴鎹搧璐級
     */
    public static int getSellPrice(int quality) {
        return (int)(BASE_PRICE * QualityHelper.getPriceMultiplier(quality));
    }
}
"@
    
    Set-Content -Path $filepath -Value $content -Encoding UTF8
    Write-Host "  鐢熸垚Item: $className" -ForegroundColor Green
}

# 鐢熸垚绉嶅瓙Item绫伙紙瀹屽叏鐓ф妱ParsnipSeedItem锛?
function Generate-SeedItem {
    param($crop)
    
    $className = (Capitalize $crop.EnglishName) + "SeedItem"
    $cropBlockName = (Capitalize $crop.EnglishName).ToUpper() + "_CROP"
    $filepath = "$itemPath\$className.java"
    
    $seasonComment = Get-SeasonComment $crop.Season
    $seasonCheck = Get-SeasonCheck $crop.Season "StardewTimeManager.get().getCurrentSeason()"
    
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
 * $($crop.ChineseName)绉嶅瓙
 * ${seasonComment}浣滅墿
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
        return $($crop.SeedPrice);
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
    Write-Host "  鐢熸垚SeedItem: $className" -ForegroundColor Green
}

# 鐢熸垚浣滅墿Block绫伙紙瀹屽叏鐓ф妱ParsnipCropBlock锛?
function Generate-CropBlock {
    param($crop)
    
    $className = (Capitalize $crop.EnglishName) + "CropBlock"
    $filepath = "$blockPath\$className.java"
    
    $seasonComment = Get-SeasonComment $crop.Season
    $seasonCheck = Get-SeasonCheck $crop.Season "season"
    
    # 璁＄畻姣忎釜闃舵鐨勫ぉ鏁?
    $totalDays = $crop.GrowthDays
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
    
    $itemName = (Capitalize $crop.EnglishName).ToUpper()
    
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
 * $($crop.ChineseName)
 * ${seasonComment}浣滅墿锛?($crop.GrowthDays)澶╃敓闀垮懆鏈?
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
        return $($crop.CanRegrow.ToString().ToLower());
    }
    
    @Override
    protected int getRegrowAge() {
        return $($crop.RegrowDays);
    }
    
    @Override
    public String getCropDisplayName() {
        return "$($crop.ChineseName)";
    }
}
"@
    
    Set-Content -Path $filepath -Value $content -Encoding UTF8
    Write-Host "  鐢熸垚CropBlock: $className" -ForegroundColor Green
}

# 涓绘祦绋?
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  鏄熼湶璋蜂綔鐗╂壒閲忕敓鎴?v2" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 璇诲彇浣滅墿鏁版嵁
$crops = @()
$lines = Get-Content $cropDataFile -Encoding UTF8
foreach ($line in $lines) {
    if ($line.StartsWith("#") -or $line.Trim() -eq "") {
        continue
    }
    
    $parts = $line -split '\|'
    if ($parts.Length -ge 12) {
        $crops += [PSCustomObject]@{
            ChineseName = $parts[0]
            EnglishName = $parts[1]
            EnglishDesc = $parts[2]
            ChineseDesc = $parts[3]
            Season = $parts[4]
            GrowthDays = [int]$parts[5]
            BasePrice = [int]$parts[6]
            BaseEnergy = [int]$parts[7]
            BaseHealth = [int]$parts[8]
            CanRegrow = $parts[9]
            RegrowDays = [int]$parts[10]
            SeedPrice = [int]$parts[11]
        }
    }
}

Write-Host "鍏辨壘鍒?$($crops.Count) 涓綔鐗ーn" -ForegroundColor Yellow

# 鐢熸垚鎵€鏈変綔鐗?
foreach ($crop in $crops) {
    
    
    Write-Host "`n澶勭悊: $($crop.ChineseName) ($($crop.EnglishName))" -ForegroundColor Cyan
    
    Generate-CropItem $crop
    Generate-SeedItem $crop
    Generate-CropBlock $crop
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  瀹屾垚锛? -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan


