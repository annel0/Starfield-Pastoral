# ================================================================
# 星露谷物语 - 剪羊毛成功
# ================================================================
# 用途：给予玩家羊毛并增加友谊度
# 调用：从 try_shear_sheep.mcfunction 调用
# @s = 绵羊实体

# 根据保存的CMD给予对应羊毛
execute if score @s stardew.animal.produce_cmd matches 8032 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/wool
execute if score @s stardew.animal.produce_cmd matches 8033 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/wool_silver
execute if score @s stardew.animal.produce_cmd matches 8034 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/wool_gold
execute if score @s stardew.animal.produce_cmd matches 8035 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/animal_produce/wool_diamond

# 清除羊毛标记
scoreboard players set @s stardew.animal.has_produce 0
scoreboard players reset @s stardew.animal.produce_cmd

# 增加友谊度(剪羊毛+5)
scoreboard players add @s stardew.animal.friendship 5

# 增加农耕经验 (剪羊毛 +5 XP)
execute as @a[distance=..5,limit=1,sort=nearest] run function stardew:farming/xp/animal_care

# 播放音效
playsound minecraft:entity.sheep.shear player @a[distance=..10] ~ ~ ~ 1 1

# 显示提示
tellraw @a[distance=..5] [{"text":"[剪羊毛] ","color":"green","bold":true},{"text":"你收集了羊毛! ","color":"white","bold":false},{"text":"(友谊度 +5)","color":"aqua","bold":false}]

# 添加临时标记防止重复剪羊毛
tag @s add stardew.just_sheared
schedule function stardew:animal/interact/clear_shear_tag 1t
