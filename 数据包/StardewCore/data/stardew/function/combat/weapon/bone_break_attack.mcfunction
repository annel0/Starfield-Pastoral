# 骨裂打击攻击调度
execute as @a[tag=sd_using_bone_break] at @s if entity @e[tag=sd_bone_break_target] run function stardew:combat/weapon/bone_break_hit
tag @e[tag=sd_bone_break_target] remove sd_bone_break_target
