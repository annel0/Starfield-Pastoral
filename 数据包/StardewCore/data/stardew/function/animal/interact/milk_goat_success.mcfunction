# ================================================================
# 星露谷物语 - 挤山羊奶成功
# ================================================================
# 用途：给玩家山羊羊奶并移除山羊的产出标记
# @s = 山羊实体
# ================================================================

# 根据保存的 CMD 给予对应羊奶
execute if score @s stardew.animal.produce_cmd matches 8036 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/goat_milk
execute if score @s stardew.animal.produce_cmd matches 8037 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/goat_milk_silver
execute if score @s stardew.animal.produce_cmd matches 8038 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/goat_milk_gold
execute if score @s stardew.animal.produce_cmd matches 8039 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/goat_milk_diamond
execute if score @s stardew.animal.produce_cmd matches 8040 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/large_goat_milk
execute if score @s stardew.animal.produce_cmd matches 8041 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/large_goat_milk_silver
execute if score @s stardew.animal.produce_cmd matches 8042 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/large_goat_milk_gold
execute if score @s stardew.animal.produce_cmd matches 8043 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/large_goat_milk_diamond

# 移除产出标记（使用分数，与牛系统一致）
scoreboard players set @s stardew.animal.has_produce 0
scoreboard players reset @s stardew.animal.produce_cmd

# 增加友谊度
scoreboard players add @s stardew.animal.friendship 5

# 播放音效
execute at @s run playsound minecraft:entity.goat.milk neutral @a[distance=..10] ~ ~ ~ 1 1

# 提示玩家
tellraw @a[distance=..5] ["",{"text":"[成功] ","color":"green","bold":true},{"text":"你挤了山羊奶！（友谊度 +5）","color":"yellow"}]

# 调试信息
tellraw @a[tag=stardew.debug] ["",{"text":"[挤山羊奶] ","color":"aqua"},{"text":"山羊 ","color":"white"},{"score":{"name":"@s","objective":"stardew.animal.id"}},{"text":" 被挤奶了","color":"white"}]
