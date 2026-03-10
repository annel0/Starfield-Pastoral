# data/stardew/function/menu/animate_scale.mcfunction
# [执行者: display实体] 播放缩放动画

# 边框放大到0.7f(和高亮按钮一样大小,实现无缝衔接)
execute if entity @s[tag=sd_menu_border] run data merge entity @s {start_interpolation:0,interpolation_duration:5,transformation:{scale:[0.7f,0.7f,0.7f]}}
# 按钮放大到0.5f(未高亮状态)
execute if entity @s[tag=sd_menu_button] run data merge entity @s {start_interpolation:0,interpolation_duration:5,transformation:{scale:[0.5f,0.5f,0.5f]}}
execute if entity @s[tag=sd_menu_page_btn] run data merge entity @s {start_interpolation:0,interpolation_duration:5,transformation:{scale:[0.5f,0.5f,0.5f]}}

