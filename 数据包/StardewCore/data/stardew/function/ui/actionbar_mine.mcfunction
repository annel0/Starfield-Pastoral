# stardew:ui/actionbar_mine.mcfunction
# 矿洞专用的 actionbar 渲染
# 第三栏显示当前层数，格式: ⛏ 第 X 层
# 参数: $(HP), $(MaxHP), $(Energy), $(MaxEnergy), $(MineFloor) 等

# 由于矿洞UI比较简单（第三栏只显示层数），直接用宏渲染
# 复用原有的 bar 文件，但把 sd_special_type 临时设为 0 让它显示空白第三栏
# 然后我们用额外的 tellraw 覆盖... 不行，actionbar 会被覆盖

# 简化方案：直接调用原来的渲染（显示空白第三栏），矿洞层数通过其他方式显示
# 但这样第三栏就是空的了...

# 最简单方案：直接构建一个简化的 actionbar（不处理所有组合，用默认颜色）
$title @s actionbar [{"text":"❤ ","color":"red"},{"score":{"name":"@s","objective":"sd_health"},"color":"white"},{"text":"/","color":"gray"},{"score":{"name":"@s","objective":"sd_max_health"},"color":"white"},{"text":"  │  ","color":"dark_gray"},{"text":"⚡ ","color":"yellow"},{"score":{"name":"@s","objective":"sd_energy"},"color":"white"},{"text":"/","color":"gray"},{"score":{"name":"@s","objective":"sd_max_energy"},"color":"white"},{"text":"  │  ","color":"dark_gray"},{"text":"⛏ 矿洞 ","color":"gold"},{"text":"第 ","color":"gray"},{"text":"$(MineFloor)","color":"aqua","bold":true},{"text":" 层","color":"gray"}]
