# 鉴定动画第一阶段a - 加速飞入(从右后方快速飞到中途)
# 目标:从(3.5,0,2.5)右后方 → (1.2,0,0.8)中途位置
# 快速6tick模拟加速
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.translation set value [1.2f,0f,0.8f]
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.scale set value [0.3f,0.3f,0.3f]

data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] interpolation_duration set value 6
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] start_interpolation set value 0
