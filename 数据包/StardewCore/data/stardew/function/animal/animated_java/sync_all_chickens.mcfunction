# ================================================================
# 星露谷物语 - 同步 AJ 鸡、鸭和兔位置
# ================================================================
# 用途：每 tick 让 AJ root entity 跟随隐形鸡实体
# 利用 Auto Update Rig Orientation，只需 tp root entity

# 同步成年鸡
execute as @e[tag=aj.chicken.root,tag=stardew.animal.aj_bound] at @s run function stardew:animal/animated_java/sync_chicken_single

# 同步幼年鸡（type=101的幼年鸡）
execute as @e[tag=aj.chicken_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.type matches 101 at @s run function stardew:animal/animated_java/sync_chicken_baby_single

# 同步成年鸭
execute as @e[tag=aj.duck.root,tag=stardew.animal.aj_bound] at @s run function stardew:animal/animated_java/sync_duck_single

# 同步幼年鸭（使用chicken_baby模型，但type=102）
execute as @e[tag=aj.chicken_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.type matches 102 at @s run function stardew:animal/animated_java/sync_duck_baby_single

# 同步成年兔
execute as @e[tag=aj.rabbit.root,tag=stardew.animal.aj_bound] at @s run function stardew:animal/animated_java/sync_rabbit_single

# 同步幼年兔（rabbit_baby模型，type=103）
execute as @e[tag=aj.rabbit_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.type matches 103 at @s run function stardew:animal/animated_java/sync_rabbit_baby_single

# 同步成年牛
execute as @e[tag=aj.cow.root,tag=stardew.animal.aj_bound] at @s run function stardew:animal/animated_java/sync_cow_single

# 同步幼年牛（cow_baby模型，type=201）
execute as @e[tag=aj.cow_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.type matches 201 at @s run function stardew:animal/animated_java/sync_cow_baby_single

# 同步成年绵羊
execute as @e[tag=aj.sheep.root,tag=stardew.animal.aj_bound] at @s run function stardew:animal/animated_java/sync_sheep_single

# 同步幼年绵羊（sheep_baby模型，type=203）
execute as @e[tag=aj.sheep_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.type matches 203 at @s run function stardew:animal/animated_java/sync_sheep_baby_single

# 同步成年山羊
execute as @e[tag=aj.goat.root,tag=stardew.animal.aj_bound] at @s run function stardew:animal/visual/sync_goat_single

# 同步幼年山羊（goat_baby模型，type=202）
execute as @e[tag=aj.goat_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.type matches 202 at @s run function stardew:animal/visual/sync_goat_baby_single

# 同步成年猪
execute as @e[tag=aj.pig.root,tag=stardew.animal.aj_bound] at @s run function stardew:animal/animated_java/sync_pig_single

# 同步幼年猪（pig_baby模型，type=204）
execute as @e[tag=aj.pig_baby.root,tag=stardew.animal.aj_bound] if score @s stardew.animal.type matches 204 at @s run function stardew:animal/animated_java/sync_pig_baby_single
