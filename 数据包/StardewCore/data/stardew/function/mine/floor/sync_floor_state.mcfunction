# stardew:mine/floor/sync_floor_state.mcfunction
# 同步楼层状态 (检测坑洞和石头数量)
# 宏参数: $(z) - 房间中心 Z 坐标
# 执行者: 玩家 (@s)

# 检测该层是否已有下层坑 (通过检测 sd_mine_ladder_down 标签的实体)
# 使用更大的检测范围以覆盖 room1 和 room2
scoreboard players set @s sd_mine_ladder 0
$execute in stardew:mine positioned 15 65 $(z) store result score @s sd_mine_ladder if entity @e[type=interaction,tag=sd_mine_ladder_down,distance=..50]

# 重新计算该层剩余石头数量
# 使用更大的检测范围以覆盖 room1 和 room2
$execute in stardew:mine positioned 15 65 $(z) store result score @s sd_mine_stones run execute if entity @e[type=interaction,tag=sd_mine_stone,distance=..50]
