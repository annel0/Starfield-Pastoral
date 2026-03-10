# data/stardew/function/menu/hover/on_hover_page.mcfunction
# [执行者: 被瞄准的翻页按钮] 处理翻页按钮悬停

# 1. 放大效果(翻页按钮悬停时放大)
data merge entity @s {start_interpolation:0,interpolation_duration:3,transformation:{scale:[0.6f,0.6f,0.6f]},Glowing:1b}

# 2. 杀死射线
kill @e[tag=sd_menu_ray,limit=1]
