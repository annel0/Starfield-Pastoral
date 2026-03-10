# 骨裂打击命中效果
execute as @e[tag=sd_bone_break_target,limit=1] at @s run playsound minecraft:entity.skeleton.death player @a ~ ~ ~ 1.2 0.7
execute as @e[tag=sd_bone_break_target,limit=1] at @s run playsound minecraft:entity.player.attack.knockback player @a ~ ~ ~ 1 0.6
execute as @e[tag=sd_bone_break_target,limit=1] at @s run particle minecraft:block{block_state:"minecraft:bone_block"} ~ ~1 ~ 0.4 0.6 0.4 0.2 30 force
execute as @e[tag=sd_bone_break_target,limit=1] at @s run particle minecraft:damage_indicator ~ ~1.5 ~ 0.3 0.4 0.3 0.15 5 force
execute as @e[tag=sd_bone_break_target,limit=1] at @s run function stardew:combat/weapon/bone_break_damage
