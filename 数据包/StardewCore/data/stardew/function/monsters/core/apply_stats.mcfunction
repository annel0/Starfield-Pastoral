# stardew:monsters/core/apply_stats.mcfunction
# 为单个怪物应用属性

# 初始化怪物血量记分板
execute store result score @s sd_monster_hp run data get entity @s Health
execute store result score @s sd_monster_max_hp run data get entity @s Attributes[{Name:"minecraft:generic.max_health"}].Base

# 给予发光效果用于显示血量
effect give @s minecraft:glowing infinite 0 true

# 标记属性已设置
tag @s add sd_stats_set

