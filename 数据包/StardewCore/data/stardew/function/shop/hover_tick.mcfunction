# 商店悬停状态管理 - 每tick执行
# 管理商品槽位的高光和tooltip显示，以及按钮高光

# === 槽位interaction系统 ===
# 1. 检测移开 (上一tick被悬停，本tick没被悬停) - 清除对应item_icon的高光
execute as @e[type=interaction,tag=slot_1,scores={sd_shop_hover=0,sd_shop_hover_prev=1}] run execute as @e[type=item_display,tag=item_icon,tag=slot_1] run function stardew:shop/clear_hover
execute as @e[type=interaction,tag=slot_2,scores={sd_shop_hover=0,sd_shop_hover_prev=1}] run execute as @e[type=item_display,tag=item_icon,tag=slot_2] run function stardew:shop/clear_hover
execute as @e[type=interaction,tag=slot_3,scores={sd_shop_hover=0,sd_shop_hover_prev=1}] run execute as @e[type=item_display,tag=item_icon,tag=slot_3] run function stardew:shop/clear_hover

# 2. 检测移开所有槽位 (没有任何interaction被悬停时，清空tooltip文本)
execute unless entity @e[type=interaction,tag=shop_interaction,scores={sd_shop_hover=1}] run data modify entity @e[type=text_display,tag=shop_tooltip,limit=1] text set value '{"text":""}'
execute unless entity @e[type=interaction,tag=shop_interaction,scores={sd_shop_hover=1}] run execute as @e[type=item_display,tag=item_icon] run function stardew:shop/clear_hover

# 3. 保存槽位interaction当前状态到prev
execute as @e[type=interaction,tag=shop_interaction,tag=slot_1] run scoreboard players operation @s sd_shop_hover_prev = @s sd_shop_hover
execute as @e[type=interaction,tag=shop_interaction,tag=slot_2] run scoreboard players operation @s sd_shop_hover_prev = @s sd_shop_hover
execute as @e[type=interaction,tag=shop_interaction,tag=slot_3] run scoreboard players operation @s sd_shop_hover_prev = @s sd_shop_hover

# 4. 清空槽位interaction当前状态
execute as @e[type=interaction,tag=shop_interaction,tag=slot_1] run scoreboard players set @s sd_shop_hover 0
execute as @e[type=interaction,tag=shop_interaction,tag=slot_2] run scoreboard players set @s sd_shop_hover 0
execute as @e[type=interaction,tag=shop_interaction,tag=slot_3] run scoreboard players set @s sd_shop_hover 0

# === 按钮item_display系统 ===
# 5. 检测按钮移开 (上一tick被悬停，本tick没被悬停)
execute as @e[type=item_display,tag=shop_button,scores={sd_shop_hover=0,sd_shop_hover_prev=1}] run function stardew:shop/hover_off

# 6. 保存shop_button当前状态到prev
execute as @e[type=item_display,tag=shop_button] run scoreboard players operation @s sd_shop_hover_prev = @s sd_shop_hover

# 7. 清空shop_button当前状态
execute as @e[type=item_display,tag=shop_button] run scoreboard players set @s sd_shop_hover 0

# === 统一射线检测 ===
# 8. 执行射线检测 (会设置sd_shop_hover=1给被击中的interaction和button)
execute as @a[scores={sd_in_shop=1..}] at @s run function stardew:shop/raycast_start
