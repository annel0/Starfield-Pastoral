# ================================================================
# 一键修复卡住的鸭子
# ================================================================
# 用途：自动找到所有年龄>=5天但还是幼年模型的鸭子，并强制它们长大

# 计数器
scoreboard players set #fixed_count stardew.animal.temp 0

# 找到所有需要长大的鸭子
execute as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.type matches 102 if score @s stardew.animal.age matches 5.. run tag @s add temp.need_grow

# 对每只需要长大的鸭子
execute as @e[tag=temp.need_grow] run function stardew:animal/debug/fix_single_duck

# 显示结果
execute if score #fixed_count stardew.animal.temp matches 1.. run tellraw @a [{"text":"[动物系统] ","color":"green"},{"text":"已修复 ","color":"yellow"},{"score":{"name":"#fixed_count","objective":"stardew.animal.temp"},"color":"white"},{"text":" 只卡住的鸭子","color":"yellow"}]
execute unless score #fixed_count stardew.animal.temp matches 1.. run tellraw @a [{"text":"[动物系统] ","color":"green"},{"text":"没有发现需要修复的鸭子","color":"yellow"}]

# 清理
tag @e remove temp.need_grow
