# 刀锋之舞 - 第5击（scheduled执行，最后一击需要清理）

execute as @a[tag=sd_using_blade_dance] at @s if entity @e[tag=sd_blade_dance_target] run function stardew:combat/weapon/blade_dance_hit5

# 清理目标标记
tag @e[tag=sd_blade_dance_target] remove sd_blade_dance_target
