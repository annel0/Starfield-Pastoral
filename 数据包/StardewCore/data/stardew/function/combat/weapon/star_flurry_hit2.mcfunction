# 星流连斩 - �?�?

# 音效
execute as @e[tag=sd_star_flurry_target,limit=1] at @s run playsound minecraft:entity.player.attack.sweep player @a ~ ~ ~ 1.0 2.0
execute as @e[tag=sd_star_flurry_target,limit=1] at @s run playsound minecraft:block.amethyst_block.chime player @a ~ ~ ~ 0.6 1.6

# 粒子效果
execute as @e[tag=sd_star_flurry_target,limit=1] at @s run particle minecraft:sweep_attack ~ ~1 ~ 0.2 0.4 0.2 0.1 2 force
execute as @e[tag=sd_star_flurry_target,limit=1] at @s run particle minecraft:enchanted_hit ~ ~1 ~ 0.4 0.4 0.4 0.3 8 force
execute as @e[tag=sd_star_flurry_target,limit=1] at @s run particle minecraft:electric_spark ~ ~1 ~ 0.3 0.5 0.3 0.1 6 force

# 执行伤害
execute as @e[tag=sd_star_flurry_target,limit=1] at @s run function stardew:combat/weapon/star_flurry_damage
