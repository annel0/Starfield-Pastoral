# 应用无限觉醒效果

# 增加暴击率 +80% (使用独立变量,不会被戒指扫描覆盖)
scoreboard players set @s sd_awakening_crit_bonus 80

# 增加闪避率 +25%
scoreboard players set @s sd_dodge_chance 25

# 给予速度III效果（8秒）
effect give @s minecraft:speed 8 2 true

# 给予急迫III效果（8秒，攻击速度+30%）
effect give @s minecraft:haste 8 2 true

# 粒子效果提示
particle minecraft:dragon_breath ~ ~1 ~ 0.5 0.5 0.5 0.2 20 force
particle minecraft:enchant ~ ~1 ~ 0.5 0.5 0.5 1 30 force
particle minecraft:electric_spark ~ ~1 ~ 0.3 0.5 0.3 0.1 15 force
