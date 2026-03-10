# 应用靴子的火焰免疫效果
# 从 storage 读取 fire_immunity 值

# 清除现有的抗火效果
effect clear @s fire_resistance

# 检查是否有火焰免疫
execute if data storage stardew:equipment boots.effects{fire_immunity:true} run effect give @s fire_resistance infinite 0 true
