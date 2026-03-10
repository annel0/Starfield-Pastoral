# 更新商品图标 - 使用loot replace给item_display更换物品
# 读取storage中的item_id,通过loot table更新对应的图标

# 槽位1 - 使用更精确的选择器,按生成顺序选择
execute in stardew:interiors as @e[type=item_display,tag=shop_ui,tag=item_icon,tag=slot_1,sort=nearest,limit=1] at @s run function stardew:shop/update_icon_slot_1

# 槽位2
execute in stardew:interiors as @e[type=item_display,tag=shop_ui,tag=item_icon,tag=slot_2,sort=nearest,limit=1] at @s run function stardew:shop/update_icon_slot_2

# 槽位3
execute in stardew:interiors as @e[type=item_display,tag=shop_ui,tag=item_icon,tag=slot_3,sort=nearest,limit=1] at @s run function stardew:shop/update_icon_slot_3
