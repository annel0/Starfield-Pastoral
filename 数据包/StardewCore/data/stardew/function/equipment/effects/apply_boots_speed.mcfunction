# 应用靴子的速度加成效果
# 从 storage 读取 speed 值

# 清除现有的速度效果(避免叠加)
effect clear @s speed

# 检查是否有速度加成
execute store result score #boots_speed sd_temp run data get storage stardew:equipment boots.effects.speed 10

# 应用速度效果 (speed值*10作为MC效果等级)
# 例如: speed=0.5 → level 5, speed=1 → level 10
execute if score #boots_speed sd_temp matches 5 run effect give @s speed infinite 0 true
execute if score #boots_speed sd_temp matches 10 run effect give @s speed infinite 1 true
execute if score #boots_speed sd_temp matches 15.. run effect give @s speed infinite 2 true
