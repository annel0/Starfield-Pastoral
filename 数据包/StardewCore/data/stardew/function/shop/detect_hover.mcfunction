# 检测玩家是否悬停在商品槽位上
# 在main.mcfunction中每tick调用

# 清除上一次的悬停标记
scoreboard players reset @a[scores={sd_in_shop=1..}] sd_hover_slot

# 检测玩家视线 - 使用interaction实体的距离判断
# 槽位1: 如果玩家距离槽位1的interaction很近且在看着它
execute as @e[type=interaction,tag=slot_1,tag=shop_interaction] at @s as @a[distance=..0.8,scores={sd_in_shop=1..}] positioned as @s anchored eyes facing entity @e[type=interaction,tag=slot_1,limit=1,sort=nearest] eyes rotated ~ 0 positioned ^ ^ ^0.5 if entity @e[type=interaction,tag=slot_1,distance=..0.3] run scoreboard players set @s sd_hover_slot 1

# 槽位2
execute as @e[type=interaction,tag=slot_2,tag=shop_interaction] at @s as @a[distance=..0.8,scores={sd_in_shop=1..}] positioned as @s anchored eyes facing entity @e[type=interaction,tag=slot_2,limit=1,sort=nearest] eyes rotated ~ 0 positioned ^ ^ ^0.5 if entity @e[type=interaction,tag=slot_2,distance=..0.3] run scoreboard players set @s sd_hover_slot 2

# 槽位3
execute as @e[type=interaction,tag=slot_3,tag=shop_interaction] at @s as @a[distance=..0.8,scores={sd_in_shop=1..}] positioned as @s anchored eyes facing entity @e[type=interaction,tag=slot_3,limit=1,sort=nearest] eyes rotated ~ 0 positioned ^ ^ ^0.5 if entity @e[type=interaction,tag=slot_3,distance=..0.3] run scoreboard players set @s sd_hover_slot 3

# 根据悬停状态显示或隐藏tooltip
execute as @a[scores={sd_in_shop=1..,sd_hover_slot=1}] run function stardew:shop/show_tooltip_1
execute as @a[scores={sd_in_shop=1..,sd_hover_slot=2}] run function stardew:shop/show_tooltip_2
execute as @a[scores={sd_in_shop=1..,sd_hover_slot=3}] run function stardew:shop/show_tooltip_3

# 没有悬停任何槽位时,隐藏所有tooltip
execute as @a[scores={sd_in_shop=1..}] unless score @s sd_hover_slot matches 1..3 run kill @e[type=text_display,tag=shop_tooltip]
