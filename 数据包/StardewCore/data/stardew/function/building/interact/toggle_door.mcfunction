# ================================================================
# 星露谷物语 - 切换建筑门状态
# ================================================================
# 用途：开关门
# 调用：从on_clicked调用，作为marker建筑执行

# 检查当前状态并切换
execute if score @s stardew.building.door_open matches 1 run tag @s add door_was_open
execute if score @s stardew.building.door_open matches 0 run tag @s add door_was_closed

# 切换状态
execute if entity @s[tag=door_was_open] run scoreboard players set @s stardew.building.door_open 0
execute if entity @s[tag=door_was_closed] run scoreboard players set @s stardew.building.door_open 1

# 显示消息（根据新状态）
execute if entity @s[tag=door_was_open] run tellraw @a[distance=..10] [{"text":"[建筑] ","color":"gold"},{"text":"门已关闭","color":"red"}]
execute if entity @s[tag=door_was_closed] run tellraw @a[distance=..10] [{"text":"[建筑] ","color":"gold"},{"text":"门已打开","color":"green"}]

# 更新视觉外壳的名称显示
execute if entity @s[tag=door_was_closed] if entity @s[tag=stardew.building.coop] at @s run data modify entity @e[type=armor_stand,tag=stardew.building.visual,distance=..1,limit=1] CustomName set value '{"text":"🐔 鸡舍 [开]","color":"green"}'
execute if entity @s[tag=door_was_open] if entity @s[tag=stardew.building.coop] at @s run data modify entity @e[type=armor_stand,tag=stardew.building.visual,distance=..1,limit=1] CustomName set value '{"text":"🐔 鸡舍 [关]","color":"red"}'

execute if entity @s[tag=door_was_closed] if entity @s[tag=stardew.building.barn] at @s run data modify entity @e[type=armor_stand,tag=stardew.building.visual,distance=..1,limit=1] CustomName set value '{"text":"🐄 畜棚 [开]","color":"green"}'
execute if entity @s[tag=door_was_open] if entity @s[tag=stardew.building.barn] at @s run data modify entity @e[type=armor_stand,tag=stardew.building.visual,distance=..1,limit=1] CustomName set value '{"text":"🐄 畜棚 [关]","color":"red"}'

# 清除临时标签
tag @s remove door_was_open
tag @s remove door_was_closed

# 播放音效
execute at @s run playsound minecraft:block.wooden_door.open block @a[distance=..10] ~ ~ ~ 1.0 1.0
