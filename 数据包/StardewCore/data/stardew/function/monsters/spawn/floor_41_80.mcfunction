# stardew:monsters/spawn/floor_41_80.mcfunction
# 41-80层怪物生成池
# 执行者: 玩家 (@s)
# 执行环境: in stardew:mine
# 前置条件: #room_x_min, #room_x_max, #room_z_min, #room_z_max 已设置

# 生成数量随层数增加: 41-55层=5-6只, 56-70层=6-7只, 71-80层=7-8只
execute if score @s sd_mine_floor matches 41..55 store result score #spawn_count sd_temp run random value 5..6
execute if score @s sd_mine_floor matches 56..70 store result score #spawn_count sd_temp run random value 6..7
execute if score @s sd_mine_floor matches 71..80 store result score #spawn_count sd_temp run random value 7..8

# 设置怪物层级（用于类型选择）
scoreboard players set #monster_tier sd_temp 2

# 开始生成循环
function stardew:monsters/spawn/spawn_monster_loop
