# 出售动物
# 根据星露谷物语公式计算价格

# 找到被选中的动物
execute as @e[type=#stardew:animals,tag=stardew.animal] if score @s stardew.animal.id = @p stardew.animal.selected_id run tag @s add stardew.animal.to_sell

# 检查是否找到动物
execute unless entity @e[tag=stardew.animal.to_sell] run tellraw @s [{"text":"[错误] ","color":"red"},{"text":"未找到要出售的动物！","color":"gray"}]
execute unless entity @e[tag=stardew.animal.to_sell] run return 0

# 重新计算售价
scoreboard players operation #sell_price stardew.animal.temp = @e[tag=stardew.animal.to_sell,limit=1] stardew.animal.type
execute if score #sell_price stardew.animal.temp matches 101 run scoreboard players set #base_price stardew.animal.temp 800
execute if score #sell_price stardew.animal.temp matches 102 run scoreboard players set #base_price stardew.animal.temp 1200
execute if score #sell_price stardew.animal.temp matches 103 run scoreboard players set #base_price stardew.animal.temp 8000
execute if score #sell_price stardew.animal.temp matches 104 run scoreboard players set #base_price stardew.animal.temp 1300
execute if score #sell_price stardew.animal.temp matches 105 run scoreboard players set #base_price stardew.animal.temp 1040
execute if score #sell_price stardew.animal.temp matches 106 run scoreboard players set #base_price stardew.animal.temp 100000
execute if score #sell_price stardew.animal.temp matches 201 run scoreboard players set #base_price stardew.animal.temp 1500
execute if score #sell_price stardew.animal.temp matches 202 run scoreboard players set #base_price stardew.animal.temp 4000
execute if score #sell_price stardew.animal.temp matches 203 run scoreboard players set #base_price stardew.animal.temp 8000
execute if score #sell_price stardew.animal.temp matches 204 run scoreboard players set #base_price stardew.animal.temp 16000
execute if score #sell_price stardew.animal.temp matches 205 run scoreboard players set #base_price stardew.animal.temp 20000

scoreboard players set #1000 stardew.animal.const 1000
scoreboard players set #300 stardew.animal.const 300
execute as @e[tag=stardew.animal.to_sell,limit=1] run scoreboard players operation #friendship_bonus stardew.animal.temp = @s stardew.animal.friendship
scoreboard players operation #friendship_bonus stardew.animal.temp += #300 stardew.animal.const
scoreboard players operation #sell_price stardew.animal.temp = #base_price stardew.animal.temp
scoreboard players operation #sell_price stardew.animal.temp *= #friendship_bonus stardew.animal.temp
scoreboard players operation #sell_price stardew.animal.temp /= #1000 stardew.animal.const

# 增加玩家金币
scoreboard players operation @s sd_gold += #sell_price stardew.animal.temp

# 显示消息
tellraw @s [{"text":"━━━━━━━━━━━━━━━━━━━━","color":"gray"}]
tellraw @s [{"text":"[出售成功] ","color":"green","bold":true}]
tellraw @s [{"text":"你出售了这只动物，获得 ","color":"white"},{"score":{"name":"#sell_price","objective":"stardew.animal.temp"},"color":"gold"},{"text":"G","color":"gold"}]
tellraw @s [{"text":"━━━━━━━━━━━━━━━━━━━━","color":"gray"}]

# 播放音效
playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 1 1.5
playsound minecraft:entity.villager.yes player @s ~ ~ ~ 1 1

# 删除 Animated Java 模型
execute store result score #remove_id stardew.animal.temp run scoreboard players get @e[tag=stardew.animal.to_sell,limit=1] stardew.animal.id

# 根据类型移除对应的 AJ 模型
execute if entity @e[tag=stardew.animal.to_sell,scores={stardew.animal.type=101},limit=1] run function stardew:animal/animated_java/remove_chicken
execute if entity @e[tag=stardew.animal.to_sell,scores={stardew.animal.type=101},limit=1] run function stardew:animal/animated_java/remove_chicken_baby

execute if entity @e[tag=stardew.animal.to_sell,scores={stardew.animal.type=102},limit=1] run function stardew:animal/animated_java/remove_duck
execute if entity @e[tag=stardew.animal.to_sell,scores={stardew.animal.type=102},limit=1] run function stardew:animal/animated_java/remove_duck_baby

execute if entity @e[tag=stardew.animal.to_sell,scores={stardew.animal.type=103},limit=1] run function stardew:animal/animated_java/remove_rabbit
execute if entity @e[tag=stardew.animal.to_sell,scores={stardew.animal.type=103},limit=1] run function stardew:animal/animated_java/remove_rabbit_baby

execute if entity @e[tag=stardew.animal.to_sell,scores={stardew.animal.type=201},limit=1] run function stardew:animal/animated_java/remove_cow
execute if entity @e[tag=stardew.animal.to_sell,scores={stardew.animal.type=201},limit=1] run function stardew:animal/animated_java/remove_cow_baby

execute if entity @e[tag=stardew.animal.to_sell,scores={stardew.animal.type=202},limit=1] run function stardew:animal/animated_java/remove_goat
execute if entity @e[tag=stardew.animal.to_sell,scores={stardew.animal.type=202},limit=1] run function stardew:animal/animated_java/remove_goat_baby

execute if entity @e[tag=stardew.animal.to_sell,scores={stardew.animal.type=203},limit=1] run function stardew:animal/animated_java/remove_sheep
execute if entity @e[tag=stardew.animal.to_sell,scores={stardew.animal.type=203},limit=1] run function stardew:animal/animated_java/remove_sheep_baby

execute if entity @e[tag=stardew.animal.to_sell,scores={stardew.animal.type=204},limit=1] run function stardew:animal/animated_java/remove_pig
execute if entity @e[tag=stardew.animal.to_sell,scores={stardew.animal.type=204},limit=1] run function stardew:animal/animated_java/remove_pig_baby

# 删除动物及其interaction实体
execute as @e[type=interaction,tag=stardew.animal.interaction] if score @s stardew.animal.id = @e[tag=stardew.animal.to_sell,limit=1] stardew.animal.id run kill @s
kill @e[tag=stardew.animal.to_sell]

# 清除选中标记和ID
scoreboard players reset @s stardew.animal.selected_id
