# 刀锋之舞 - 第5击（终结）
# 最强一击，爆发性的伤害

# 音效（爆发）
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run playsound minecraft:entity.player.attack.sweep player @a ~ ~ ~ 1.5 1.6
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 1.2 1.2
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run playsound minecraft:entity.player.attack.knockback player @a ~ ~ ~ 0.8 0.8

# 粒子效果（爆发）
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run particle minecraft:sweep_attack ~ ~1 ~ 0.4 0.6 0.4 0.1 5 force
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run particle minecraft:damage_indicator ~ ~1 ~ 0.6 0.6 0.6 0.3 5 force
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run particle minecraft:crit ~ ~1 ~ 0.5 0.7 0.5 0.2 20 force
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run particle minecraft:enchanted_hit ~ ~1 ~ 0.4 0.6 0.4 0.3 10 force

# 执行伤害
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run function stardew:combat/weapon/blade_dance_damage
