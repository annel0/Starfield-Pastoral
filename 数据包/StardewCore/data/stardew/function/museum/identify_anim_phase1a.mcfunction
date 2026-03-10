# 鉴定动画第一阶段a - 从玩家右后方飞到右前方(逆时针开始)
# 围绕玩家圆周:从(2.2,-0.2,-2.2)右后方 → (2.5,-0.2,-0.8)右后侧 (逆时针旋转,Z负=后,正=前)
# 3tick快速加速,从无到有,加上Y轴旋转
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.translation set value [2.5f,-0.2f,-0.8f]
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.scale set value [1.0f,1.0f,1.0f]
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.right_rotation set value [0f,0.3827f,0f,0.9239f]

data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] interpolation_duration set value 3
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] start_interpolation set value 0
# 围绕玩家圆周:从(1.8,0,-1.8)右后方 → (1.8,0,-1.0)右后侧 (逆时针旋转,Z负=后,正=前)
# 3tick快速加速,从无到有
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.translation set value [2.0f,0f,-0.5f]
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.scale set value [1.2f,1.2f,1.2f]

data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] interpolation_duration set value 3
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] start_interpolation set value 0
