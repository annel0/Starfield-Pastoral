# 钓鱼展示动画第一阶段b - 从右后侧快速到玩家正前方(继续逆时针)
# 围绕玩家圆周:从(2.5,-0.2,-0.8)右后侧 → (1.0,-0.2,2.5)正前方 (逆时针旋转,Z正=前方)
# 调整: Z轴从1.5增加到2.5(更远), 缩放从2.0减小到1.5(更小), 持续时间从2增加到4(更慢)
data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] transformation.translation set value [1.0f,-0.2f,2.5f]
data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] transformation.scale set value [1.5f,1.5f,1.5f]
data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] transformation.right_rotation set value [0f,0.7071f,0f,0.7071f]

data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] interpolation_duration set value 4
data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] start_interpolation set value 0
