# 应用靴子的跳跃增强效果
# 从 storage 读取 jump_boost 值

# 清除现有的跳跃提升效果
effect clear @s jump_boost

# 检查是否有跳跃增强
execute if data storage stardew:equipment boots.effects{jump_boost:true} run effect give @s jump_boost infinite 1 true
