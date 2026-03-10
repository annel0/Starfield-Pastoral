# 修复所有作物的plant.mcfunction文件 - 使用正确的UTF-8编码（无BOM）
$cropsDir = "data\stardew\function\crops\planting"

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

# 创建UTF-8无BOM编码对象
$utf8NoBom = New-Object System.Text.UTF8Encoding $false

foreach ($crop in $crops) {
    $filePath = Join-Path $cropsDir "$crop\plant.mcfunction"
    
    if (-not (Test-Path $filePath)) {
        Write-Host "警告: 文件不存在 $filePath" -ForegroundColor Yellow
        $errorCount++
        continue
    }
    
    Write-Host "处理: $crop" -ForegroundColor Cyan
    
    try {
        # 读取文件（保持原编码）
        $content = [System.IO.File]::ReadAllText($filePath, [System.Text.Encoding]::UTF8)
        
        # 修复第4步：反馈特效
        $content = $content -replace '(?m)^execute as @e\[tag=init_crop,distance=\.\.0\.5,limit=1\] at @s run playsound', 'execute align xyz positioned ~0.5 ~1.375 ~0.5 as @e[tag=init_crop,distance=..0.5,limit=1] at @s run playsound'
        $content = $content -replace '(?m)^execute as @e\[tag=init_crop,distance=\.\.0\.5,limit=1\] at @s run particle', 'execute align xyz positioned ~0.5 ~1.375 ~0.5 as @e[tag=init_crop,distance=..0.5,limit=1] at @s run particle'
        
        # 修复第5步：消耗种子与初始化
        $content = $content -replace '(?m)^execute if entity @e\[tag=init_crop,distance=\.\.0\.5\]', 'execute align xyz positioned ~0.5 ~1.375 ~0.5 if entity @e[tag=init_crop,distance=..0.5]'
        
        # 逐行处理，修复as @e但排除已有align xyz positioned的
        $lines = $content -split "`n"
        $newLines = @()
        foreach ($line in $lines) {
            if ($line -match '^execute as @e\[tag=init_crop,distance=\.\.0\.5\]' -and $line -notmatch 'align xyz positioned') {
                $line = $line -replace '^execute as @e\[tag=init_crop,distance=\.\.0\.5\]', 'execute align xyz positioned ~0.5 ~1.375 ~0.5 as @e[tag=init_crop,distance=..0.5]'
            }
            $newLines += $line
        }
        $content = $newLines -join "`n"
        
        # 使用UTF-8无BOM保存
        [System.IO.File]::WriteAllText($filePath, $content, $utf8NoBom)
        
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
if ($errorCount -gt 0) {
    Write-Host "错误: $errorCount 个作物" -ForegroundColor Red
}
