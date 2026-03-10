# ================================================================
# 重建兔子模型 - 第二步
# ================================================================

tellraw @a [{"text":"[兔子修复] 生成新模型...","color":"yellow"}]

# 找到对应ID的兔子
execute as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.id = #rebuild_id stardew.animal.temp run tag @s add temp.rebuild_target

# 根据年龄生成对应模型
execute as @e[tag=temp.rebuild_target] if score @s stardew.animal.age matches ..4 run function stardew:animal/animated_java/summon_rabbit_baby
execute as @e[tag=temp.rebuild_target] if score @s stardew.animal.age matches 5.. run function stardew:animal/animated_java/summon_rabbit

tellraw @a [{"text":"[兔子修复] ✓ 修复完成！","color":"green"}]

# 清理标记
tag @e[tag=temp.rebuild_rabbit] remove temp.rebuild_rabbit
tag @e[tag=temp.rebuild_target] remove temp.rebuild_target
