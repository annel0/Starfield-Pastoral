# 上升阶段动画（1-6 ticks）
# 使用 interpolation_duration 实现平滑动画

# 第一帧：设置目标状态（缩放+平移）
execute if score @s sd_dmg_anim matches 1 run data modify entity @s transformation.scale set value [2.0f,2.0f,2.0f]
execute if score @s sd_dmg_anim matches 1 run data modify entity @s transformation.translation set value [0f,1.0f,0f]
execute if score @s sd_dmg_anim matches 1 run data modify entity @s interpolation_duration set value 6
execute if score @s sd_dmg_anim matches 1 run data modify entity @s start_interpolation set value 0
