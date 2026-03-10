# 显示怪物血量（使用 CustomName 动态更新）

# 【稻草人特殊处理】显示固定的满血状态
execute if entity @s[tag=sd_dummy] run data modify storage stardew:temp name set value '{"text":"🎯 稻草人","color":"yellow","bold":true}'
execute if entity @s[tag=sd_dummy] run data modify storage stardew:temp hp set value "999999"
execute if entity @s[tag=sd_dummy] run data modify storage stardew:temp max_hp set value "999999"
execute if entity @s[tag=sd_dummy] run scoreboard players set #hp_percent sd_temp 100
execute if entity @s[tag=sd_dummy] run function stardew:combat/update_hp_display with storage stardew:temp
execute if entity @s[tag=sd_dummy] run return 0

# 给怪物发光效果
execute if score @s sd_monster_hp matches 1.. run effect give @s minecraft:glowing 1 0 true

# 将血量数据存入 storage（如果是负数则显示为 0）
execute store result storage stardew:temp hp int 1 run scoreboard players get @s sd_monster_hp
execute if score @s sd_monster_hp matches ..0 run data modify storage stardew:temp hp set value 0
execute store result storage stardew:temp max_hp int 1 run scoreboard players get @s sd_monster_max_hp

# 计算血量百分比用于颜色（避免除零错误）
scoreboard players operation #hp_percent sd_temp = @s sd_monster_hp
scoreboard players operation #hp_percent sd_temp *= #100 sd_const
execute if score @s sd_monster_max_hp matches 1.. run scoreboard players operation #hp_percent sd_temp /= @s sd_monster_max_hp
execute unless score @s sd_monster_max_hp matches 1.. run scoreboard players set #hp_percent sd_temp 0

# 根据怪物类型设置名字（使用原版生物名称）
execute if entity @s[tag=sd_mob_slime] run data modify storage stardew:temp name set value '{"text":"史莱姆","color":"green"}'
execute if entity @s[tag=sd_mob_spider] run data modify storage stardew:temp name set value '{"text":"蜘蛛","color":"dark_gray"}'
execute if entity @s[tag=sd_mob_cave_spider] run data modify storage stardew:temp name set value '{"text":"洞穴蜘蛛","color":"dark_red"}'
execute if entity @s[tag=sd_mob_silverfish] run data modify storage stardew:temp name set value '{"text":"蠹虫","color":"gray"}'
execute if entity @s[tag=sd_mob_skeleton] run data modify storage stardew:temp name set value '{"text":"骷髅","color":"white"}'
execute if entity @s[tag=sd_mob_stray] run data modify storage stardew:temp name set value '{"text":"流浪者","color":"aqua"}'
execute if entity @s[tag=sd_mob_bat] run data modify storage stardew:temp name set value '{"text":"幻翼","color":"dark_purple"}'
execute if entity @s[tag=sd_mob_ghost] run data modify storage stardew:temp name set value '{"text":"溺尸","color":"aqua"}'
execute if entity @s[tag=sd_mob_golem] run data modify storage stardew:temp name set value '{"text":"僵尸","color":"dark_green"}'
execute if entity @s[tag=sd_mob_shadow] run data modify storage stardew:temp name set value '{"text":"凋灵骷髅","color":"dark_gray"}'
execute if entity @s[tag=sd_mob_squid] run data modify storage stardew:temp name set value '{"text":"恼鬼","color":"white"}'

# 使用宏函数更新名字
function stardew:combat/update_hp_display with storage stardew:temp

