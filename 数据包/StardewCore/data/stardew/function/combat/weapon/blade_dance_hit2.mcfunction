# 刀锋之舞 - 第2击

# 音效
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run playsound minecraft:entity.player.attack.sweep player @a ~ ~ ~ 1.2 1.3
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run playsound minecraft:item.trident.throw player @a ~ ~ ~ 0.6 1.5

# 粒子效果
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run particle minecraft:sweep_attack ~ ~1 ~ 0.3 0.5 0.3 0.1 3 force
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run particle minecraft:damage_indicator ~ ~1 ~ 0.4 0.4 0.4 0.2 3 force
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run particle minecraft:enchanted_hit ~ ~1 ~ 0.3 0.5 0.3 0.2 6 force

# 执行伤害
execute as @e[tag=sd_blade_dance_target,limit=1] at @s run function stardew:combat/weapon/blade_dance_damage
