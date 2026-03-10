# 鉴定动画第二阶段 - 从中间(translation.x=0)飞向左侧(translation.x=-4)围绕玩家飞出视野+向上+旋转+缩小消失
# 先设置目标状态:飞到左侧更远处(x=-4)真正飞出视野+向上(y=0.8)+反向旋转一圈+缩小
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.translation set value [-4.0f,0.8f,0f]
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.scale set value [0.05f,0.05f,0.05f]
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.right_rotation set value [0f,-1f,0f,0f]

# 然后设置插值参数:14tick内完成动画(更长时间让它真正飞出视野)
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] interpolation_duration set value 14
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] start_interpolation set value 0

# 额外粒子效果
execute at @e[tag=sd_identify_display,limit=1,sort=nearest] run particle minecraft:glow ~ ~ ~ 0.15 0.15 0.15 0.05 15 force @a[distance=..20]
