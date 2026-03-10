# 钓鱼展示动画第二阶段b - 从左前侧继续逆时针回到左后方消失
# 围绕玩家圆周:从(-2.5,-0.1,0.8)左前侧 → (-2.2,0.2,-2.2)左后方 (逆时针回到起点)
# 4tick消失,快速旋转+翻滚
data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] transformation.translation set value [-2.2f,0.2f,-2.2f]
data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] transformation.scale set value [0f,0f,0f]
data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] transformation.right_rotation set value [0.5f,0.5f,0.5f,0.5f]

data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] interpolation_duration set value 4
data modify entity @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] start_interpolation set value 0

# 额外粒子效果（水花）
execute at @e[tag=sd_fish_reveal_display,limit=1,sort=nearest] run particle minecraft:splash ~ ~ ~ 0.2 0.2 0.2 0.05 15 force @a[distance=..20]
