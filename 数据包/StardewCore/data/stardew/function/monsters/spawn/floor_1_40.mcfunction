# stardew:monsters/spawn/floor_1_40.mcfunction
# 1-40层怪物生成池
# 执行者: 玩家 (@s)
# 执行环境: in stardew:mine
# 前置条件: #room_x_min, #room_x_max, #room_z_min, #room_z_max 已设置

# 生成数量随层数增加: 1-10层=3-4只, 11-25层=4-5只, 26-40层=5-6只
execute if score @s sd_mine_floor matches 1..10 store result score #spawn_count sd_temp run random value 3..4
execute if score @s sd_mine_floor matches 11..25 store result score #spawn_count sd_temp run random value 4..5
execute if score @s sd_mine_floor matches 26..40 store result score #spawn_count sd_temp run random value 5..6

# 设置怪物层级（用于类型选择）
scoreboard players set #monster_tier sd_temp 1

# 开始生成循环
function stardew:monsters/spawn/spawn_monster_loop
