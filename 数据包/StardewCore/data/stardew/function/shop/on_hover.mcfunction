# 当鼠标悬停在按钮上时执行
# [执行者: item_display按钮]

# 1. 标记为当前被悬停
scoreboard players set @s sd_shop_hover 1

# 2. 如果是首次被悬停(上一tick未被悬停),触发高光效果
execute if score @s sd_shop_hover_prev matches 0 run data merge entity @s {Glowing:1b}
