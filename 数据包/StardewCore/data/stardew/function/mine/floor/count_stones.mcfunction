# stardew:mine/floor/count_stones.mcfunction
# 计算房间内实际的石头数量
# 参数: $(z) - 房间中心 Z 坐标

# 在房间中心位置计数所有带 sd_mine_stone 标签的 interaction 实体
# 房间中心 X=15 (因为房间是 0~30), Y=65 (地板上方一格)
# 使用 distance=..30 覆盖 room1(30x30) 和 room2(30x40)
# 每层间距100，30不会跨层
$execute in stardew:mine positioned 15 65 $(z) store result score @s sd_mine_stones run execute if entity @e[type=interaction,tag=sd_mine_stone,distance=..30]
