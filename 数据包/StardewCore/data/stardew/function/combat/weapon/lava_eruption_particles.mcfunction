# 熔岩爆发 - 持续的火焰粒子效果

# 在玩家周围5格范围内生成持续的火焰粒子
execute as @a[tag=sd_using_lava_eruption] at @s run particle minecraft:flame ~ ~0.1 ~ 4 0.1 4 0.02 30 force
execute as @a[tag=sd_using_lava_eruption] at @s run particle minecraft:lava ~ ~0.1 ~ 3 0 3 0 5 force
execute as @a[tag=sd_using_lava_eruption] at @s run particle minecraft:smoke ~ ~0.5 ~ 3 0.5 3 0.05 20 force
