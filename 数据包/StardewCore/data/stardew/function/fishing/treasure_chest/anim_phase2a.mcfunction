# 宝箱动画第二阶段a - 从正前方继续逆时针到玩家左前方
data modify entity @e[tag=sd_treasure_item_display,limit=1,sort=nearest] transformation.translation set value [-2.5f,-0.1f,0.8f]
data modify entity @e[tag=sd_treasure_item_display,limit=1,sort=nearest] transformation.scale set value [1.2f,1.2f,1.2f]
data modify entity @e[tag=sd_treasure_item_display,limit=1,sort=nearest] transformation.right_rotation set value [0.2588f,0.9659f,0f,0f]

data modify entity @e[tag=sd_treasure_item_display,limit=1,sort=nearest] interpolation_duration set value 3
data modify entity @e[tag=sd_treasure_item_display,limit=1,sort=nearest] start_interpolation set value 0
