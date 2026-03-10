# data/stardew/function/utility/apply_rotation.mcfunction
# 根据sd_rotation分数应用正确的旋转到视觉实体
# 执行者: 视觉实体 (@s)
# 需要: @s 必须有 sd_rotation 分数

# 0度: 无旋转
execute if score @s sd_rotation matches 0 run data merge entity @s {transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.2f,1.2f,1.2f]}}

# 90度: Y轴旋转90度
execute if score @s sd_rotation matches 90 run data merge entity @s {transformation:{left_rotation:[0f,0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.2f,1.2f,1.2f]}}

# 180度: Y轴旋转180度
execute if score @s sd_rotation matches 180 run data merge entity @s {transformation:{left_rotation:[0f,1f,0f,0f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.2f,1.2f,1.2f]}}

# 270度: Y轴旋转270度
execute if score @s sd_rotation matches 270 run data merge entity @s {transformation:{left_rotation:[0f,-0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.2f,1.2f,1.2f]}}
