# 应用靴子的水下呼吸效果
# 从 storage 读取 water_breathing 值

# 清除现有的水下呼吸效果
effect clear @s water_breathing

# 检查是否有水下呼吸
execute if data storage stardew:equipment boots.effects{water_breathing:true} run effect give @s water_breathing infinite 0 true
