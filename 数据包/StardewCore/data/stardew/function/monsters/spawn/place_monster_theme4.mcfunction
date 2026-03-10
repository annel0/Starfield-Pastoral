# stardew:monsters/spawn/place_monster_theme4.mcfunction
# 主题4（76-100层）：熔岩风格 - 精英怪物
# 史莱姆8%, 流浪者18%, 僵尸14%, 凋灵骷髅28%, 恼鬼18%, 寒霜幻翼14%

# 随机选择怪物类型
execute store result score #spawn_type sd_temp run random value 1..50

# 1-4: 史莱姆 (8%)
execute if score #spawn_type sd_temp matches 1..4 run scoreboard players set #spawn_type sd_temp 1

# 5-13: 流浪者 (18%)
execute if score #spawn_type sd_temp matches 5..13 run scoreboard players set #spawn_type sd_temp 4

# 14-20: 僵尸 (14%)
execute if score #spawn_type sd_temp matches 14..20 run scoreboard players set #spawn_type sd_temp 7

# 21-34: 凋灵骷髅 (28%)
execute if score #spawn_type sd_temp matches 21..34 run scoreboard players set #spawn_type sd_temp 8

# 35-43: 恼鬼 (18%)
execute if score #spawn_type sd_temp matches 35..43 run scoreboard players set #spawn_type sd_temp 9

# 44-50: 寒霜幻翼 (14%)
execute if score #spawn_type sd_temp matches 44..50 run scoreboard players set #spawn_type sd_temp 5

# 注意: 不在这里生成，而是由 try_spawn_monster 统一调用 spawn_random_monster
