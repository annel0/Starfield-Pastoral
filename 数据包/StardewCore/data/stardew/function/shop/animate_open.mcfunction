# 商店UI打开动画
# 所有shop_animate实体从scale 0→原始大小，持续10 ticks

# 对话框: 0.7
execute as @e[type=item_display,tag=dialogue_frame,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:10,transformation:{scale:[0.7f,0.7f,0.7f]}}

# 头像框/NPC: 0.5, 1.0
execute as @e[type=item_display,tag=portrait_frame,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:10,transformation:{scale:[0.5f,0.5f,0.5f]}}
execute as @e[type=item_display,tag=portrait,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:10,transformation:{scale:[1f,1f,1f]}}

# 按钮: 0.5
execute as @e[type=item_display,tag=shop_button,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:10,transformation:{scale:[0.5f,0.5f,0.5f]}}

# 商品区背景: 1.4, 1.27, 0.6
execute as @e[type=item_display,tag=goods_background,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:10,transformation:{scale:[1.4f,1.27f,0.6f]}}

# 商品槽位框: 1.3
execute as @e[type=item_display,tag=slot_frame,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:10,transformation:{scale:[1.3f,1.3f,1f]}}

# 金钱框: 0.8, 0.6, 0.3
execute as @e[type=item_display,tag=money_frame,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:10,transformation:{scale:[0.8f,0.6f,0.3f]}}

# 商品图标: 0.2, 0.2, 0.1
execute as @e[type=item_display,tag=item_icon,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:10,transformation:{scale:[0.2f,0.2f,0.1f]}}

# 所有文字: 1.0
execute as @e[type=text_display,tag=shop_animate] run data merge entity @s {start_interpolation:0,interpolation_duration:10,transformation:{scale:[1f,1f,1f]}}
