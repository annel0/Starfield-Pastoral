# 修复所有plant.mcfunction中的consume_seed调用
# 将 consume_seed 改为直接 clear 命令

$plantFiles = Get-ChildItem -Path "data\stardew\function\crops\planting" -Recurse -Filter "plant.mcfunction"

$successCount = 0

foreach ($file in $plantFiles) {
    $content = Get-Content $file.FullName -Raw -Encoding UTF8
    
    # 查找CMD值 (从文件名或路径推断)
    $cropName = $file.Directory.Name
    
    # 跳过已经使用clear的文件
    if ($content -match 'run clear @s carrot_on_a_stick') {
        Write-Host "○ 已是clear格式,跳过: $cropName" -ForegroundColor Gray
        continue
    }
    
    # 替换 consume_seed 为 clear 命令
    # 但我们需要知道每个作物的CMD值...
    # 先输出需要手动修改的文件
    Write-Host "✗ 需要修改: $($file.FullName)" -ForegroundColor Yellow
    $successCount++
}

Write-Host ""
Write-Host "发现 $successCount 个文件使用 consume_seed,需要改为 clear" -ForegroundColor Cyan
