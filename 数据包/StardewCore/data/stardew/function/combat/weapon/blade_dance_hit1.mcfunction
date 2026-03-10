# 刀锋之舞 - 第1击
# 快速、凌厉的刀刃效果

# 音效（凌厉、低沉）
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run playsound minecraft:entity.player.attack.sweep player @a ~ ~ ~ 1.2 1.2
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run playsound minecraft:entity.player.attack.strong player @a ~ ~ ~ 0.8 1.0

# 粒子效果（血红刀光）
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run particle minecraft:sweep_attack ~ ~1 ~ 0.3 0.5 0.3 0.1 3 force
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run particle minecraft:damage_indicator ~ ~1 ~ 0.4 0.4 0.4 0.2 3 force
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run particle minecraft:crit ~ ~1 ~ 0.3 0.5 0.3 0.15 10 force

# 执行伤害
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run function stardew:combat/weapon/blade_dance_damage
