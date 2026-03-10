# 宝箱动画第二阶段b - 从左前侧继续逆时针回到左后方消失
data modify entity @e[tag=sd_treasure_item_display,limit=1,sort=nearest] transformation.translation set value [-2.2f,0.2f,-2.2f]
data modify entity @e[tag=sd_treasure_item_display,limit=1,sort=nearest] transformation.scale set value [0f,0f,0f]
data modify entity @e[tag=sd_treasure_item_display,limit=1,sort=nearest] transformation.right_rotation set value [0.5f,0.5f,0.5f,0.5f]

data modify entity @e[tag=sd_treasure_item_display,limit=1,sort=nearest] interpolation_duration set value 4
data modify entity @e[tag=sd_treasure_item_display,limit=1,sort=nearest] start_interpolation set value 0

# 额外粒子效果
execute at @e[tag=sd_treasure_item_display,limit=1,sort=nearest] run particle minecraft:glow ~ ~ ~ 0.2 0.2 0.2 0.05 20 force @a[distance=..20]
