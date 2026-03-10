# ================================================================
# 重建鸭子模型 - 第二步
# ================================================================

# 找到标记的鸭子
execute as @e[type=chicken,tag=stardew.animal,tag=temp.rebuild] run scoreboard players operation #rebuild_id stardew.animal.temp = @s stardew.animal.id
execute as @e[type=chicken,tag=stardew.animal,tag=temp.rebuild] run scoreboard players operation #rebuild_age stardew.animal.temp = @s stardew.animal.age

# 2. 根据年龄生成正确的模型
# 如果年龄 < 5天，生成幼年鸭
execute as @e[type=chicken,tag=stardew.animal,tag=temp.rebuild] if score @s stardew.animal.age matches ..4 at @s run function stardew:animal/animated_java/summon_duck_baby
execute if score #rebuild_age stardew.animal.temp matches ..4 run tellraw @a [{"text":"  - 生成幼年鸭模型 (年龄<5天)","color":"gray"}]

# 如果年龄 >= 5天，生成成年鸭
execute as @e[type=chicken,tag=stardew.animal,tag=temp.rebuild] if score @s stardew.animal.age matches 5.. at @s run function stardew:animal/animated_java/summon_duck
execute if score #rebuild_age stardew.animal.temp matches 5.. run tellraw @a [{"text":"  - 生成成年鸭模型 (年龄>=5天)","color":"gray"}]

tellraw @a [{"text":"[修复] ","color":"green"},{"text":"修复完成！","color":"yellow"}]

# 清理标记
tag @e remove temp.rebuild
