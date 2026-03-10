# ================================================================
# 星露谷物语 - 收集松露
# ================================================================
# 用途：玩家右键点击松露时收集
# @s = 松露实体（item类型）
# ================================================================

# 检查是否有玩家在附近
execute unless entity @a[distance=..5] run return 0

# 根据产出猪的友谊值计算松露品质
scoreboard players set #truffle_quality stardew.animal.temp 0

# 友谊值 >= 200 = 银星
execute if score @s stardew.animal.friendship matches 200..399 run scoreboard players set #truffle_quality stardew.animal.temp 1

# 友谊值 >= 400 = 金星  
execute if score @s stardew.animal.friendship matches 400..599 run scoreboard players set #truffle_quality stardew.animal.temp 2

# 友谊值 >= 600 = 铱星
execute if score @s stardew.animal.friendship matches 600.. run scoreboard players set #truffle_quality stardew.animal.temp 3

# 给予玩家对应品质的松露
execute if score #truffle_quality stardew.animal.temp matches 0 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/foraging/truffle
execute if score #truffle_quality stardew.animal.temp matches 1 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/foraging/truffle_silver
execute if score #truffle_quality stardew.animal.temp matches 2 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/foraging/truffle_gold
execute if score #truffle_quality stardew.animal.temp matches 3 run loot give @a[distance=..5,limit=1,sort=nearest] loot stardew:items/foraging/truffle_iridium

# 播放收集音效
playsound minecraft:entity.item.pickup player @a[distance=..10] ~ ~ ~ 1 1.2

# 显示提示
execute if score #truffle_quality stardew.animal.temp matches 0 run tellraw @a[distance=..5] ["",{"text":"[收集] ","color":"green","bold":true},{"text":"你找到了松露！","color":"brown"}]
execute if score #truffle_quality stardew.animal.temp matches 1 run tellraw @a[distance=..5] ["",{"text":"[收集] ","color":"green","bold":true},{"text":"你找到了银星松露！","color":"gray"}]
execute if score #truffle_quality stardew.animal.temp matches 2 run tellraw @a[distance=..5] ["",{"text":"[收集] ","color":"green","bold":true},{"text":"你找到了金星松露！","color":"gold"}]
execute if score #truffle_quality stardew.animal.temp matches 3 run tellraw @a[distance=..5] ["",{"text":"[收集] ","color":"green","bold":true},{"text":"你找到了铱星松露！","color":"light_purple"}]

# 移除松露实体
kill @s