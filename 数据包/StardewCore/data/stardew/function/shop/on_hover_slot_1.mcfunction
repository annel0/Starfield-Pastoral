# 首次悬停槽位1 - 触发高光和tooltip
# [执行者: interaction slot_1]

# 1. 清除其他槽位的高光
execute as @e[type=item_display,tag=item_icon,tag=slot_2] run function stardew:shop/clear_hover
execute as @e[type=item_display,tag=item_icon,tag=slot_3] run function stardew:shop/clear_hover

# 2. 高光item_icon (放大+发光，保持原始rotation)
execute as @e[type=item_display,tag=item_icon,tag=slot_1,limit=1] run data merge entity @s {start_interpolation:0,interpolation_duration:5,transformation:{scale:[0.35f,0.35f,0.15f]},Glowing:1b}

# 3. 传送tooltip到item_icon西边
execute as @e[type=item_display,tag=item_icon,tag=slot_1,limit=1] at @s positioned ~-1.2 ~-0.15 ~-0.8 run tp @e[type=text_display,tag=shop_tooltip,limit=1] ~ ~ ~

# 4. 读取槽位1的物品数据并更新tooltip内容
data modify storage stardew:temp tooltip_data set from storage stardew:temp current_page[0]
function stardew:shop/update_tooltip

# 5. 启动tooltip展开动画
execute as @e[type=text_display,tag=shop_tooltip] run data merge entity @s {start_interpolation:0,interpolation_duration:8,transformation:{scale:[0.7f,0.7f,0.7f]}}
