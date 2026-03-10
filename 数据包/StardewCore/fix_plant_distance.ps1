# 修复所有plant.mcfunction文件中的distance选择器
# 将 distance=..2 改为 distance=..0.5
# 将全局 tag @e[tag=init_crop] 改为 distance=..0.5

$plantFiles = Get-ChildItem -Path "data\stardew\function\crops\planting" -Recurse -Filter "plant.mcfunction"

$totalFiles = $plantFiles.Count
$successCount = 0
$errorCount = 0

Write-Host "找到 $totalFiles 个 plant.mcfunction 文件" -ForegroundColor Cyan
Write-Host ""

foreach ($file in $plantFiles) {
    try {
        $content = Get-Content $file.FullName -Raw -Encoding UTF8
        $originalContent = $content
        
        # 1. 修复 distance=..2 为 distance=..0.5
        $content = $content -replace 'tag=init_crop,distance=\.\.2', 'tag=init_crop,distance=..0.5'
        
        # 2. 修复没有distance限制的init_crop操作
        # 匹配: execute as @e[tag=init_crop] (后面没有distance)
        # 改为: execute as @e[tag=init_crop,distance=..0.5]
        $content = $content -replace 'execute as @e\[tag=init_crop\](?!.*distance)', 'execute as @e[tag=init_crop,distance=..0.5]'
        
        # 3. 修复最后一行的全局tag移除
        # 匹配: tag @e[tag=init_crop] remove init_crop (没有execute as前缀)
        # 改为: execute as @e[tag=init_crop,distance=..0.5] run tag @s remove init_crop
        $content = $content -replace '^tag @e\[tag=init_crop\] remove init_crop', 'execute as @e[tag=init_crop,distance=..0.5] run tag @s remove init_crop' -replace '\r?\ntag @e\[tag=init_crop\] remove init_crop', "`nexecute as @e[tag=init_crop,distance=..0.5] run tag @s remove init_crop"
        
        # 4. 修复可能存在的 execute if entity @e[tag=init_crop,distance=..2]
        $content = $content -replace 'execute if entity @e\[tag=init_crop,distance=\.\.2\]', 'execute if entity @e[tag=init_crop,distance=..0.5]'
        
        if ($content -ne $originalContent) {
            Set-Content -Path $file.FullName -Value $content -Encoding UTF8 -NoNewline
            Write-Host "✓ 已修复: $($file.FullName)" -ForegroundColor Green
            $successCount++
        } else {
            Write-Host "○ 无需修改: $($file.FullName)" -ForegroundColor Gray
        }
    }
    catch {
        Write-Host "✗ 错误: $($file.FullName)" -ForegroundColor Red
        Write-Host "  详情: $_" -ForegroundColor Red
        $errorCount++
    }
}

Write-Host ""
Write-Host "================ 修复完成 ================" -ForegroundColor Cyan
Write-Host "总文件数: $totalFiles" -ForegroundColor White
Write-Host "成功修复: $successCount" -ForegroundColor Green
Write-Host "发生错误: $errorCount" -ForegroundColor Red
Write-Host "=========================================" -ForegroundColor Cyan
