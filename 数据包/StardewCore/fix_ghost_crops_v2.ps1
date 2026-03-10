# 修复所有作物的plant.mcfunction文件，防止幽灵作物问题
# 按照parsnip、cauliflower、potato的格式统一所有作物的plant函数

$cropsDir = "data\stardew\function\crops\planting"
$backupDir = "backup_crops_$(Get-Date -Format 'yyyyMMdd_HHmmss')"

# 创建备份目录
Write-Host "创建备份目录: $backupDir" -ForegroundColor Yellow
New-Item -ItemType Directory -Path $backupDir -Force | Out-Null

# 所有作物列表（排除已修复的3个）
$crops = @(
    "amaranth", "ancient_fruit", "artichoke", "blueberry", "blue_jazz", 
    "bok_choy", "broccoli", "carrot", "coffee_bean", "corn", 
    "cranberry", "crystal_fruit", "eggplant", "fairy_rose", "garlic", 
    "grape", "green_bean", "hops", "hot_pepper", "kale", 
    "melon", "poppy", "powder_melon", "pumpkin", "radish", 
    "red_cabbage", "rhubarb", "snow_yam", "starfruit", "strawberry", 
    "summer_spangle", "summer_squash", "sunflower", "tomato", "tulip", 
    "wheat", "winter_root", "yam"
)

$fixedCount = 0
$errorCount = 0

foreach ($crop in $crops) {
    $filePath = Join-Path $cropsDir "$crop\plant.mcfunction"
    
    if (-not (Test-Path $filePath)) {
        Write-Host "警告: 文件不存在 $filePath" -ForegroundColor Yellow
        $errorCount++
        continue
    }
    
    Write-Host "处理: $crop" -ForegroundColor Cyan
    
    try {
        # 备份原文件
        $backupPath = Join-Path $backupDir "$crop`_plant.mcfunction"
        Copy-Item $filePath $backupPath -Force
        Write-Host "  → 已备份到: $backupPath" -ForegroundColor Gray
        
        $content = Get-Content $filePath -Raw -Encoding UTF8
        
        # 修复第4步：反馈特效
        # 将 "execute as @e[tag=init_crop,distance=..0.5,limit=1] at @s run playsound"
        # 替换为 "execute align xyz positioned ~0.5 ~1.375 ~0.5 as @e[tag=init_crop,distance=..0.5,limit=1] at @s run playsound"
        $content = $content -replace '(?m)^execute as @e\[tag=init_crop,distance=\.\.0\.5,limit=1\] at @s run playsound', 'execute align xyz positioned ~0.5 ~1.375 ~0.5 as @e[tag=init_crop,distance=..0.5,limit=1] at @s run playsound'
        
        # 修复第4步的particle命令
        $content = $content -replace '(?m)^execute as @e\[tag=init_crop,distance=\.\.0\.5,limit=1\] at @s run particle', 'execute align xyz positioned ~0.5 ~1.375 ~0.5 as @e[tag=init_crop,distance=..0.5,limit=1] at @s run particle'
        
        # 修复第5步：消耗种子与初始化
        # 将所有 "execute if entity @e[tag=init_crop,distance=..0.5]" 替换为 "execute align xyz positioned ~0.5 ~1.375 ~0.5 if entity @e[tag=init_crop,distance=..0.5]"
        $content = $content -replace '(?m)^execute if entity @e\[tag=init_crop,distance=\.\.0\.5\]', 'execute align xyz positioned ~0.5 ~1.375 ~0.5 if entity @e[tag=init_crop,distance=..0.5]'
        
        # 将所有 "execute as @e[tag=init_crop,distance=..0.5]" 替换为 "execute align xyz positioned ~0.5 ~1.375 ~0.5 as @e[tag=init_crop,distance=..0.5]"
        # 但要排除已经有 align xyz positioned 的行
        $lines = $content -split "`n"
        $newLines = @()
        foreach ($line in $lines) {
            if ($line -match '^execute as @e\[tag=init_crop,distance=\.\.0\.5\]' -and $line -notmatch 'align xyz positioned') {
                $line = $line -replace '^execute as @e\[tag=init_crop,distance=\.\.0\.5\]', 'execute align xyz positioned ~0.5 ~1.375 ~0.5 as @e[tag=init_crop,distance=..0.5]'
            }
            $newLines += $line
        }
        $content = $newLines -join "`n"
        
        # 保存文件
        $content | Set-Content $filePath -Encoding UTF8 -NoNewline
        
        Write-Host "  ✓ 修复完成: $crop" -ForegroundColor Green
        $fixedCount++
    }
    catch {
        Write-Host "  ✗ 错误: $crop - $_" -ForegroundColor Red
        $errorCount++
    }
}

Write-Host "`n修复完成！" -ForegroundColor Green
Write-Host "成功修复: $fixedCount 个作物" -ForegroundColor Green
Write-Host "备份位置: $backupDir" -ForegroundColor Cyan
if ($errorCount -gt 0) {
    Write-Host "错误: $errorCount 个作物" -ForegroundColor Red
}
Write-Host "`n如需恢复，可以从备份目录复制文件回去。" -ForegroundColor Yellow
