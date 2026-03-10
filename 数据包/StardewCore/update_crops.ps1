# Batch update crop plant.mcfunction files to add fertilizer inheritance logic

$crops = @(
    "amaranth", "ancient_fruit", "artichoke", "blueberry", "blue_jazz", "bok_choy", 
    "broccoli", "carrot", "cauliflower", "coffee_bean", "corn", "cranberry", 
    "crystal_fruit", "eggplant", "fairy_rose", "garlic", "grape", "green_bean", 
    "hops", "hot_pepper", "kale", "melon", "poppy", "potato", 
    "powder_melon", "pumpkin", "radish", "red_cabbage", "rhubarb", "snow_yam", 
    "starfruit", "strawberry", "summer_spangle", "summer_squash", "sunflower", 
    "tomato", "tulip", "wheat", "winter_root", "yam"
)

$basePath = ".\data\stardew\function\crops\planting"
$count = 0
$failed = @()

foreach ($crop in $crops) {
    $filePath = Join-Path $basePath "$crop\plant.mcfunction"
    
    if (-not (Test-Path $filePath)) {
        Write-Host "Skip (not found): $crop" -ForegroundColor Yellow
        $failed += $crop
        continue
    }
    
    $content = Get-Content $filePath -Raw -Encoding UTF8
    
    # Check if already modified
    if ($content -match "sd_new_crop") {
        Write-Host "Skip (already done): $crop" -ForegroundColor Gray
        continue
    }
    
    # Replace content
    $oldText = "tag @e[tag=init_crop] remove init_crop"
    $newText = @"
# 5.5 肥料继承 - 检查并继承已有肥料
execute as @e[tag=init_crop] at @s run tag @s add sd_new_crop
execute as @e[tag=sd_new_crop] at @s run function stardew:farming/fertilizer/inherit_on_plant
execute as @e[tag=sd_new_crop] run tag @s remove sd_new_crop

# 移除初始化标签
tag @e[tag=init_crop] remove init_crop
"@
    
    if ($content -match [regex]::Escape($oldText)) {
        $content = $content -replace [regex]::Escape($oldText), $newText
        Set-Content $filePath $content -NoNewline -Encoding UTF8
        $count++
        Write-Host "Updated: $crop" -ForegroundColor Green
    } else {
        Write-Host "Failed (text not found): $crop" -ForegroundColor Red
        $failed += $crop
    }
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Total updated: $count files" -ForegroundColor Green
if ($failed.Count -gt 0) {
    Write-Host "Failed crops: $($failed -join ', ')" -ForegroundColor Red
}
