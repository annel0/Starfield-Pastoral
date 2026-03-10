# 鉴定动画第二阶段a - 从正前方继续逆时针到玩家左前方
# 围绕玩家圆周:从(0,-0.2,1.8)正前方 → (-2.5,-0.1,0.8)左前侧 (逆时针旋转)
# 3tick加速,继续旋转+开始翻滚(X轴)
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.translation set value [-2.5f,-0.1f,0.8f]
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.scale set value [1.2f,1.2f,1.2f]
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.right_rotation set value [0.2588f,0.9659f,0f,0f]

data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] interpolation_duration set value 3
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] start_interpolation set value 0
