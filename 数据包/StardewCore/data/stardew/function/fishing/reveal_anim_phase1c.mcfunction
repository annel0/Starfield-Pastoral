# 钓鱼展示动画第一阶段c - 在玩家正前方停顿展示
# 保持在正前方,放大展示,回到无旋转状态正对玩家
# 调整: Z轴从1.8增加到3.0(更远), 缩放从3.0减小到1.8(更小), 持续时间从5增加到10(停留更久)
data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] transformation.translation set value [0f,-0.2f,3.0f]
data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] transformation.scale set value [1.8f,1.8f,1.8f]
data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] transformation.right_rotation set value [0f,0f,0f,1f]

data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] interpolation_duration set value 10
data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] start_interpolation set value 0
