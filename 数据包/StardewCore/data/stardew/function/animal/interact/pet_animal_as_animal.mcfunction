# ================================================================
# 星露谷物语 - 对动物执行抚摸
# ================================================================
# 用途：以动物为执行者，更新好感度和心情
# @s = 被抚摸的动物实体
# ================================================================

# 标记这个动物已经被处理（避免在同一次射线检测中重复抚摸）
tag @s add stardew.animal.just_petted

# 检查今天是否已经抚摸过
execute if score @s stardew.animal.friendship_today matches 1 run tellraw @a[distance=..5] [{"text":"[提示] ","color":"yellow","bold":true},{"text":"今天已经抚摸过这只动物了！","color":"white","bold":false}]
execute if score @s stardew.animal.friendship_today matches 1 run return fail

# 更新好感度
function stardew:animal/data/friendship

# 更新心情
function stardew:animal/data/mood

# 显示动物名字（基于stardew.animal.type而非原版实体类型）
execute if score @s stardew.animal.type matches 101 run tellraw @a[distance=..5] [{"text":"[抚摸] ","color":"green","bold":true},{"text":"你抚摸了一只鸡 🐔","color":"white","bold":false}]
execute if score @s stardew.animal.type matches 102 run tellraw @a[distance=..5] [{"text":"[抚摸] ","color":"green","bold":true},{"text":"你抚摸了一只鸭 🦆","color":"white","bold":false}]
execute if score @s stardew.animal.type matches 103 run tellraw @a[distance=..5] [{"text":"[抚摸] ","color":"green","bold":true},{"text":"你抚摸了一只兔子 🐰","color":"white","bold":false}]
execute if score @s stardew.animal.type matches 201 run tellraw @a[distance=..5] [{"text":"[抚摸] ","color":"green","bold":true},{"text":"你抚摸了一头牛 🐂","color":"white","bold":false}]
execute if score @s stardew.animal.type matches 202 run tellraw @a[distance=..5] [{"text":"[抚摸] ","color":"green","bold":true},{"text":"你抚摸了一只山羊 🐐","color":"white","bold":false}]
execute if score @s stardew.animal.type matches 203 run tellraw @a[distance=..5] [{"text":"[抚摸] ","color":"green","bold":true},{"text":"你抚摸了一只羊 🐑","color":"white","bold":false}]
execute if score @s stardew.animal.type matches 204 run tellraw @a[distance=..5] [{"text":"[抚摸] ","color":"green","bold":true},{"text":"你抚摸了一头猪 🐷","color":"white","bold":false}]

# 显示爱心粒子效果
particle minecraft:heart ~ ~1 ~ 0.5 0.5 0.5 0 5 force @a[distance=..20]

# 增加农耕经验 (抚摸动物 +5 XP)
execute as @a[distance=..5,limit=1,sort=nearest] run function stardew:farming/xp/animal_care

# 播放音效（基于stardew.animal.type）
execute if score @s stardew.animal.type matches 101 run playsound minecraft:entity.chicken.ambient master @a[distance=..10] ~ ~ ~ 1 1.2
execute if score @s stardew.animal.type matches 102 run playsound minecraft:entity.chicken.ambient master @a[distance=..10] ~ ~ ~ 1 0.8
execute if score @s stardew.animal.type matches 103 run playsound minecraft:entity.rabbit.ambient master @a[distance=..10] ~ ~ ~ 1 1.2
execute if score @s stardew.animal.type matches 201 run playsound minecraft:entity.cow.ambient master @a[distance=..10] ~ ~ ~ 1 1
execute if score @s stardew.animal.type matches 202 run playsound minecraft:entity.goat.ambient master @a[distance=..10] ~ ~ ~ 1 1
execute if score @s stardew.animal.type matches 203 run playsound minecraft:entity.sheep.ambient master @a[distance=..10] ~ ~ ~ 1 1
execute if score @s stardew.animal.type matches 204 run playsound minecraft:entity.pig.ambient master @a[distance=..10] ~ ~ ~ 1 1
