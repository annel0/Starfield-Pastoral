# 第1阶段 - 快速飞入+旋转放大(5tick)
# 从右后方实际位置快速飞到玩家面前中央

# 计算玩家正前方位置并移动display到那里
execute as @e[tag=sd_identify_display,limit=1] at @s facing entity @p eyes run tp @s ^ ^ ^3

# 设置插值目标:放大到2.5倍+旋转到正面+向玩家移动
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.translation set value [0f,0f,-3f]
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.scale set value [2.5f,2.5f,2.5f]
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] transformation.right_rotation set value [0f,0f,0f,1f]

data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] interpolation_duration set value 5
data modify entity @e[tag=sd_identify_display,limit=1,sort=nearest] start_interpolation set value 0
