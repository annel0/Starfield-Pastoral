# stardew:monsters/spawn/place_monster_tier3.mcfunction
# 在81-120层放置一只随机怪物

# 随机选择怪物类型 (1=史莱姆10%, 2=洞穴蜘蛛15%, 4=流浪者10%, 5=蝙蝠15%, 7=石魔15%, 8=暗影兽20%, 9=乌贼15%)
execute store result score #spawn_type sd_temp run random value 1..20
execute if score #spawn_type sd_temp matches 1..2 run scoreboard players set #spawn_type sd_temp 1
execute if score #spawn_type sd_temp matches 3..5 run scoreboard players set #spawn_type sd_temp 2
execute if score #spawn_type sd_temp matches 6..7 run scoreboard players set #spawn_type sd_temp 4
execute if score #spawn_type sd_temp matches 8..10 run scoreboard players set #spawn_type sd_temp 5
execute if score #spawn_type sd_temp matches 11..13 run scoreboard players set #spawn_type sd_temp 7
execute if score #spawn_type sd_temp matches 14..17 run scoreboard players set #spawn_type sd_temp 8
execute if score #spawn_type sd_temp matches 18..20 run scoreboard players set #spawn_type sd_temp 9

# 寻找生成位置并生成
function stardew:monsters/spawn/find_spawn_pos

