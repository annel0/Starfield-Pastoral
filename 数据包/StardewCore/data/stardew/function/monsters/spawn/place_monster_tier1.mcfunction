# stardew:monsters/spawn/place_monster_tier1.mcfunction
# 在1-40层放置一只随机怪物

# 随机选择怪物类型 (1=史莱姆25%, 2=蜘蛛15%, 3=蠹虫15%, 4=蝙蝠25%, 5=幽灵20%)
execute store result score #spawn_type sd_temp run random value 1..20
execute if score #spawn_type sd_temp matches 1..5 run scoreboard players set #spawn_type sd_temp 1
execute if score #spawn_type sd_temp matches 6..8 run scoreboard players set #spawn_type sd_temp 2
execute if score #spawn_type sd_temp matches 9..11 run scoreboard players set #spawn_type sd_temp 3
execute if score #spawn_type sd_temp matches 12..16 run scoreboard players set #spawn_type sd_temp 5
execute if score #spawn_type sd_temp matches 17..20 run scoreboard players set #spawn_type sd_temp 6

# 寻找生成位置并生成
function stardew:monsters/spawn/find_spawn_pos

