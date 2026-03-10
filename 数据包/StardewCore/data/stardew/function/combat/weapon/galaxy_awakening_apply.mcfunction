# 应用银河觉醒效果

# 增加暴击率 +60% (使用独立变量,不会被戒指扫描覆盖)
scoreboard players set @s sd_awakening_crit_bonus 60

# 增加闪避率 +15%
scoreboard players set @s sd_dodge_chance 15

# 给予速度II效果（6秒）
effect give @s minecraft:speed 6 1 true

# 粒子效果提示
particle minecraft:dragon_breath ~ ~1 ~ 0.3 0.5 0.3 0.1 10 force
particle minecraft:enchant ~ ~1 ~ 0.3 0.5 0.3 0.5 20 force
