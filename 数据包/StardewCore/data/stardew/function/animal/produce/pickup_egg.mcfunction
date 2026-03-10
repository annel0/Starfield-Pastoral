# ================================================================
# 星露谷物语 - 拾取鸡蛋
# ================================================================
# 用途：给予玩家鸡蛋物品并删除实体
# 调用：从 detect_egg_pickup.mcfunction 调用
# @s = interaction实体

# 清除交互数据（必须清除，否则会持续触发）
data remove entity @s interaction
data remove entity @s attack

# 根据存储的 CMD 给予对应的鸡蛋
execute if score @s stardew.item.cmd matches 8000 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/egg
execute if score @s stardew.item.cmd matches 8001 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/egg_silver
execute if score @s stardew.item.cmd matches 8002 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/egg_gold
execute if score @s stardew.item.cmd matches 8003 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/egg_diamond
execute if score @s stardew.item.cmd matches 8004 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/large_egg
execute if score @s stardew.item.cmd matches 8005 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/large_egg_silver
execute if score @s stardew.item.cmd matches 8006 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/large_egg_gold
execute if score @s stardew.item.cmd matches 8007 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/large_egg_diamond

# 根据存储的 CMD 给予对应的鸭蛋和鸭毛
execute if score @s stardew.item.cmd matches 8008 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/duck_egg
execute if score @s stardew.item.cmd matches 8009 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/duck_egg_silver
execute if score @s stardew.item.cmd matches 8010 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/duck_egg_gold
execute if score @s stardew.item.cmd matches 8011 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/duck_egg_diamond
execute if score @s stardew.item.cmd matches 8012 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/duck_feather
execute if score @s stardew.item.cmd matches 8013 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/duck_feather_silver
execute if score @s stardew.item.cmd matches 8014 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/duck_feather_gold
execute if score @s stardew.item.cmd matches 8015 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/duck_feather_diamond

# 根据存储的 CMD 给予对应的兔子羊毛和兔子脚
execute if score @s stardew.item.cmd matches 8016 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/wool
execute if score @s stardew.item.cmd matches 8017 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/wool_silver
execute if score @s stardew.item.cmd matches 8018 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/wool_gold
execute if score @s stardew.item.cmd matches 8019 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/wool_diamond
execute if score @s stardew.item.cmd matches 8020 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/rabbit_foot
execute if score @s stardew.item.cmd matches 8021 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/rabbit_foot_silver
execute if score @s stardew.item.cmd matches 8022 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/rabbit_foot_gold
execute if score @s stardew.item.cmd matches 8023 run loot give @a[distance=..10,limit=1,sort=nearest] loot stardew:items/animal_produce/rabbit_foot_diamond

# 播放拾取音效
playsound minecraft:entity.chicken.egg player @a[distance=..10] ~ ~ ~ 1 1

# 增加农耕经验 (拾取产物 +5 XP)
execute as @a[distance=..10,limit=1,sort=nearest] run function stardew:farming/xp/animal_care

# 删除附近的视觉实体
execute as @e[type=item_display,tag=stardew.egg.visual,distance=..1] run kill @s

# 删除交互体自身
kill @s
