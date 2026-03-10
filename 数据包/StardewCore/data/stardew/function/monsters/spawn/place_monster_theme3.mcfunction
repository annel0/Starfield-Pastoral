# stardew:monsters/spawn/place_monster_theme3.mcfunction
# 主题3（51-75层）：冰霜风格 - 坦克怪物
# 史莱姆10%, 洞穴蜘蛛18%, 溺尸18%, 僵尸22%, 恼鬼14%, 凋灵骷髅18%

# 随机选择怪物类型
execute store result score #spawn_type sd_temp run random value 1..50

# 1-5: 史莱姆 (10%)
execute if score #spawn_type sd_temp matches 1..5 run scoreboard players set #spawn_type sd_temp 1

# 6-14: 洞穴蜘蛛 (18%)
execute if score #spawn_type sd_temp matches 6..14 run scoreboard players set #spawn_type sd_temp 2

# 15-23: 溺尸 (18%)
execute if score #spawn_type sd_temp matches 15..23 run scoreboard players set #spawn_type sd_temp 6

# 24-34: 僵尸 (22%)
execute if score #spawn_type sd_temp matches 24..34 run scoreboard players set #spawn_type sd_temp 7

# 35-41: 恼鬼 (14%)
execute if score #spawn_type sd_temp matches 35..41 run scoreboard players set #spawn_type sd_temp 9

# 42-50: 凋灵骷髅 (18%)
execute if score #spawn_type sd_temp matches 42..50 run scoreboard players set #spawn_type sd_temp 8

# 注意: 不在这里生成，而是由 try_spawn_monster 统一调用 spawn_random_monster
