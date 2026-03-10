# stardew:mine/ladder/highlight_from_center.mcfunction
# 从房间中心位置搜索并高亮最后一块石头
# 参数: $(z) - 房间中心 Z 坐标
# 使用40格半径覆盖最大房间 (40x40)

$execute in stardew:mine positioned 20 66 $(z) as @e[type=interaction,tag=sd_mine_stone,distance=..40,limit=1] at @s run function stardew:mine/ladder/apply_glow
