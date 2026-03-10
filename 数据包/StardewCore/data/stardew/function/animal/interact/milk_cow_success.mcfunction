# ================================================================
# 星露谷物语 - 挤奶成功
# ================================================================
# 用途：给予玩家牛奶并增加友谊度
# 调用：从 try_milk_cow.mcfunction 调用
# @s = 牛实体

# 根据保存的CMD给予对应牛奶
execute if score @s stardew.animal.produce_cmd matches 8024 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/milk
execute if score @s stardew.animal.produce_cmd matches 8025 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/milk_silver
execute if score @s stardew.animal.produce_cmd matches 8026 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/milk_gold
execute if score @s stardew.animal.produce_cmd matches 8027 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/milk_diamond
execute if score @s stardew.animal.produce_cmd matches 8028 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/large_milk
execute if score @s stardew.animal.produce_cmd matches 8029 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/large_milk_silver
execute if score @s stardew.animal.produce_cmd matches 8030 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/large_milk_gold
execute if score @s stardew.animal.produce_cmd matches 8031 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/large_milk_diamond

# 清除牛奶标记
scoreboard players set @s stardew.animal.has_produce 0
scoreboard players reset @s stardew.animal.produce_cmd

# 增加友谊度(挤奶+5)
scoreboard players add @s stardew.animal.friendship 5

# 增加农耕经验 (挤奶 +5 XP)
execute as @a[distance=..5,limit=1,sort=nearest] run function stardew:farming/xp/animal_care

# 播放音效
playsound minecraft:entity.cow.milk player @a[distance=..10] ~ ~ ~ 1 1

# 显示提示
tellraw @a[distance=..5] [{"text":"[挤奶] ","color":"green","bold":true},{"text":"你收集了牛奶! ","color":"white","bold":false},{"text":"(友谊度 +5)","color":"aqua","bold":false}]

# 添加临时标记防止重复挤奶
tag @s add stardew.just_milked
schedule function stardew:animal/interact/clear_milk_tag 1t
