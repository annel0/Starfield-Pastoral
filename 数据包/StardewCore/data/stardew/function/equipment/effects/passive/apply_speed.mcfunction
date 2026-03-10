# 应用速度效果
# speed值可以是0.5或1，对应MC速度效果的等级

# 读取速度数值
execute store result score #speed_level sd_temp run data get storage stardew:temp boots_effects.speed 10

# speed=0.5 → #speed_level=5 → 速度I (level 0)
# speed=1 → #speed_level=10 → 速度II (level 1)

execute if score #speed_level sd_temp matches 5 run effect give @s speed 2 0 true
execute if score #speed_level sd_temp matches 10 run effect give @s speed 2 1 true
