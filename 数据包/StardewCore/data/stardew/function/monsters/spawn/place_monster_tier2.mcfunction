# stardew:monsters/spawn/place_monster_tier2.mcfunction
# 在41-80层放置一只随机怪物

# 随机选择怪物类型 (1=史莱姆15%, 2=蜘蛛15%, 4=骷髅15%, 3=蠹虫10%, 5=蝙蝠15%, 6=幽灵15%, 7=石魔15%)
execute store result score #spawn_type sd_temp run random value 1..20
execute if score #spawn_type sd_temp matches 1..3 run scoreboard players set #spawn_type sd_temp 1
execute if score #spawn_type sd_temp matches 4..6 run scoreboard players set #spawn_type sd_temp 2
execute if score #spawn_type sd_temp matches 7..8 run scoreboard players set #spawn_type sd_temp 3
execute if score #spawn_type sd_temp matches 9..11 run scoreboard players set #spawn_type sd_temp 4
execute if score #spawn_type sd_temp matches 12..14 run scoreboard players set #spawn_type sd_temp 5
execute if score #spawn_type sd_temp matches 15..17 run scoreboard players set #spawn_type sd_temp 6
execute if score #spawn_type sd_temp matches 18..20 run scoreboard players set #spawn_type sd_temp 7

# 寻找生成位置并生成
function stardew:monsters/spawn/find_spawn_pos

