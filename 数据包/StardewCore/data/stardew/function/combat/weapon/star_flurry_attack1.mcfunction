# 星流连斩 - 第1击（scheduled执行）

# 检查玩家和目标是否还存在
execute as @a[tag=sd_using_star_flurry] at @s if entity @e[tag=sd_star_flurry_target] run function stardew:combat/weapon/star_flurry_hit1
