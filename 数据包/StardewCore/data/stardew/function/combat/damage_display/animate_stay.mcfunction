# 停留阶段动画（9-28 ticks）
# 保持在最高点，维持最大尺寸 2.0

# 保持缩放为 2.0（如果之前的动画没有正确设置）
execute if score @s sd_dmg_anim matches 9 run data modify entity @s transformation.scale set value [2.0f,2.0f,2.0f]

# 此阶段不需要其他操作，实体保持静止
