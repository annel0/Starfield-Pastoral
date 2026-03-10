# 商店UI关闭动画
# 所有shop_animate实体从原始大小→scale 0，持续8 ticks

# 对话框
execute as @e[type=item_display,tag=dialogue_frame,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:8,transformation:{scale:[0f,0f,0f]}}

# 头像框/NPC
execute as @e[type=item_display,tag=portrait_frame,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:8,transformation:{scale:[0f,0f,0f]}}
execute as @e[type=item_display,tag=portrait,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:8,transformation:{scale:[0f,0f,0f]}}

# 按钮
execute as @e[type=item_display,tag=shop_button,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:8,transformation:{scale:[0f,0f,0f]}}

# 商品区背景
execute as @e[type=item_display,tag=goods_background,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:8,transformation:{scale:[0f,0f,0f]}}

# 商品槽位框
execute as @e[type=item_display,tag=slot_frame,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:8,transformation:{scale:[0f,0f,0f]}}

# 金钱框
execute as @e[type=item_display,tag=money_frame,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:8,transformation:{scale:[0f,0f,0f]}}

# 商品图标
execute as @e[type=item_display,tag=item_icon,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:8,transformation:{scale:[0f,0f,0f]}}

# 所有文字
execute as @e[type=text_display,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:8,transformation:{scale:[0f,0f,0f]}}

# 8 ticks后kill所有实体
schedule function stardew:shop/kill_ui 8t
