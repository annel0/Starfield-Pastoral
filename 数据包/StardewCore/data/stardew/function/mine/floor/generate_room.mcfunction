# stardew:mine/floor/generate_room.mcfunction
# 生成矿洞房间
# 执行者: 玩家 (@s)
# 
# 新结构规格 (30x5x30):
# - 结构原点放置在 (0, 64, z)
# - 出口梯子: 结构内 (4, 1, 4) -> 世界 (4, 65, z+4)
# - 电梯: 结构内 (5, 1, 4) -> 世界 (5, 65, z+4)
# - 玩家出生: 结构内 (4, 1, 5) -> 世界 (4, 65, z+5)
# - 矿物区域: 结构内 (6,1,6)-(24,1,24) -> 世界 X:6~24, Y:65, Z:z+6~z+24

# 计算基础 Z 坐标 (层数 × 100)
execute store result score #target_z sd_mine_temp run scoreboard players get @s sd_mine_floor
scoreboard players operation #target_z sd_mine_temp *= #100 sd_const

# 预计算关键 Z 偏移坐标
# z4 = 出口梯子/电梯 Z
scoreboard players operation #z4 sd_mine_temp = #target_z sd_mine_temp
scoreboard players add #z4 sd_mine_temp 4

# z5 = 玩家出生点 Z
scoreboard players operation #z5 sd_mine_temp = #target_z sd_mine_temp
scoreboard players add #z5 sd_mine_temp 5

# z6 = 矿物区域起始 Z
scoreboard players operation #z6 sd_mine_temp = #target_z sd_mine_temp
scoreboard players add #z6 sd_mine_temp 6

# z15 = 房间中心 Z (用于清理实体)
scoreboard players operation #z15 sd_mine_temp = #target_z sd_mine_temp
scoreboard players add #z15 sd_mine_temp 15

# z24 = 矿物区域结束 Z
scoreboard players operation #z24 sd_mine_temp = #target_z sd_mine_temp
scoreboard players add #z24 sd_mine_temp 24

# z30 = 结构结束 Z
scoreboard players operation #z30 sd_mine_temp = #target_z sd_mine_temp
scoreboard players add #z30 sd_mine_temp 30

# 保存到 storage
execute store result storage stardew:mine gen.floor int 1 run scoreboard players get @s sd_mine_floor
execute store result storage stardew:mine gen.z int 1 run scoreboard players get #target_z sd_mine_temp
execute store result storage stardew:mine gen.z4 int 1 run scoreboard players get #z4 sd_mine_temp
execute store result storage stardew:mine gen.z5 int 1 run scoreboard players get #z5 sd_mine_temp
execute store result storage stardew:mine gen.z6 int 1 run scoreboard players get #z6 sd_mine_temp
execute store result storage stardew:mine gen.z15 int 1 run scoreboard players get #z15 sd_mine_temp
execute store result storage stardew:mine gen.z24 int 1 run scoreboard players get #z24 sd_mine_temp
execute store result storage stardew:mine gen.z30 int 1 run scoreboard players get #z30 sd_mine_temp

# 保存当前生成的楼层号（用于延迟执行时定位玩家）
scoreboard players operation #generating_floor sd_temp = @s sd_mine_floor

# ===== 强制加载目标区块 =====
function stardew:mine/floor/forceload_area with storage stardew:mine gen

# ===== 2 tick后清理旧实体（确保forceload生效且区块已完全加载），然后再1 tick放置结构 =====
# 修复: forceload需要时间生效,从1t改为2t
schedule function stardew:mine/floor/kill_floor_entities_delayed 2t
