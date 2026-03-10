# 第3阶段 - 快速飞出+旋转消失(5tick)
# 从中央快速飞向左侧+向上+旋转+缩小

# 移动display到左侧
execute as @e[tag=sd_identify_display,limit=1] at @s run tp @s ^-2.5 ^0.3 ^-2

# 设置插值目标:飞出+旋转+缩小
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.translation set value [0f,0.5f,-2f]
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.scale set value [0.1f,0.1f,0.1f]
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.right_rotation set value [0f,-1f,0f,0f]

data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] interpolation_duration set value 5
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] start_interpolation set value 0

# 额外粒子
execute at @e[tag=sd_identify_display,limit=1,sort=nearest] run particle minecraft:glow ~ ~ ~ 0.2 0.2 0.2 0.05 15 force @a[distance=..20]
