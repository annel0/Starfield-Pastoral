# stardew:mine/floor/generate.mcfunction
# 生成普通矿洞层
# 执行者: 玩家 (@s)
# 前置: sd_mine_floor 已设置为目标层数

# 1. 计算目标坐标 (Z = 层数 × 100)
execute store result storage stardew:mine floor_z int 1 run scoreboard players get @s sd_mine_floor
execute store result score #floor_z sd_mine_temp run scoreboard players get @s sd_mine_floor
scoreboard players operation #floor_z sd_mine_temp *= #100 sd_const

# 2. 清空旧区域并生成新房间
function stardew:mine/floor/generate_room

# 3. 重置本层状态
scoreboard players set @s sd_mine_ladder 0
# 石头数量会在 spawn_stones 中设置
