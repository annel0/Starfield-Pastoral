# 星流连斩 - 第5击（scheduled执行，最后一击需要清理）

execute as @a[tag=sd_using_star_flurry] at @s if entity @e[tag=sd_star_flurry_target] run function stardew:combat/weapon/star_flurry_hit5

# 清理目标标记
tag @e[tag=sd_star_flurry_target] remove sd_star_flurry_target
