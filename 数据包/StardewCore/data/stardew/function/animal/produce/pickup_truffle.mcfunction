# ================================================================
# 星露谷物语 - 拾取松露
# ================================================================
# 用途：给予玩家松露物品并删除实体
# 调用：从 detect_truffle_pickup.mcfunction 调用
# @s = interaction实体

# 清除交互数据（必须清除，否则会持续触发）
data remove entity @s interaction
data remove entity @s attack

# 根据存储的 CMD 给予对应的松露
# 松露：8040(base), 8041(silver), 8042(gold), 8043(diamond)
execute if score @s stardew.item.cmd matches 8040 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/truffle
execute if score @s stardew.item.cmd matches 8041 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/truffle_silver
execute if score @s stardew.item.cmd matches 8042 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/truffle_gold
execute if score @s stardew.item.cmd matches 8043 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/truffle_diamond

# 删除相关实体
# 删除附近的松露视觉实体
kill @e[type=item_display,tag=stardew.truffle.visual,distance=..1]
# 删除自己（交互实体）
kill @s

# 播放拾取音效
execute at @a[distance=..10,limit=1,sort=nearest] run playsound minecraft:entity.item.pickup player @s ~ ~ ~ 0.5 1.5