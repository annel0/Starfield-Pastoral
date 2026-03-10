# stardew:monsters/spawn/floor_81_100.mcfunction
# 81-100层怪物生成池
# 执行者: 玩家 (@s)
# 执行环境: in stardew:mine
# 前置条件: #room_x_min, #room_x_max, #room_z_min, #room_z_max 已设置

# 生成数量随层数增加: 81-90层=7-9只, 91-100层=8-10只
execute if score @s sd_mine_floor matches 81..90 store result score #spawn_count sd_temp run random value 7..9
execute if score @s sd_mine_floor matches 91..100 store result score #spawn_count sd_temp run random value 8..10

# 设置怪物层级（用于类型选择）
scoreboard players set #monster_tier sd_temp 3

# 开始生成循环
function stardew:monsters/spawn/spawn_monster_loop
