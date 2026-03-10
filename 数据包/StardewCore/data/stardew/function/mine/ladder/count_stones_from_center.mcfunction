# stardew:mine/ladder/count_stones_from_center.mcfunction
# 从房间中心计数剩余石头
# 参数: $(z) - 房间中心 Z 坐标
# 输出: @p sd_mine_stones

$execute in stardew:mine positioned 20 66 $(z) store result score @p sd_mine_stones run execute if entity @e[type=interaction,tag=sd_mine_stone,distance=..40]
