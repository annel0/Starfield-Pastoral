# data/stardew/function/menu/tick.mcfunction
# [每tick执行] 菜单UI动画和状态管理

# 1. 处理新召唤的实体放大动画
scoreboard players add @e[tag=sd_menu_display] sd_menu_ctrl 1

# 2. 在第2tick时执行放大动画
execute as @e[tag=sd_menu_display] if score @s sd_menu_ctrl matches 2 run function stardew:menu/animate_scale

# 2.5 减少玩家点击冷却
execute as @a[scores={sd_menu_sequence=1..,sd_menu_click_cd=1..}] run scoreboard players remove @s sd_menu_click_cd 1

# 3. 检测交互(右键点击)
execute as @e[tag=sd_menu_interact] at @s run function stardew:menu/detect_click

# 4. 检测鼠标移开(上一tick被瞄准,本tick未被瞄准)
execute as @e[tag=sd_menu_display,tag=sd_menu_button] if score @s sd_menu_targeted matches 0 if score @s sd_menu_targeted_prev matches 1 run function stardew:menu/hover/highlight_off
execute as @e[tag=sd_menu_display,tag=sd_menu_page_btn] if score @s sd_menu_targeted matches 0 if score @s sd_menu_targeted_prev matches 1 run function stardew:menu/hover/highlight_off

# 5. 更新上一tick状态(保存当前状态到prev)
execute as @e[tag=sd_menu_display,tag=sd_menu_button] store result score @s sd_menu_targeted_prev run scoreboard players get @s sd_menu_targeted
execute as @e[tag=sd_menu_display,tag=sd_menu_page_btn] store result score @s sd_menu_targeted_prev run scoreboard players get @s sd_menu_targeted

# 6. 重置所有按钮的瞄准状态(准备下一tick)
scoreboard players set @e[tag=sd_menu_display,tag=sd_menu_button] sd_menu_targeted 0
scoreboard players set @e[tag=sd_menu_display,tag=sd_menu_page_btn] sd_menu_targeted 0

# 6.5 存储系统：检测重命名（放在raycast前，避免被卡住）
execute as @a[scores={sd_storage_renaming=1}] if items entity @s weapon.offhand writable_book run function stardew:menu/storage/process_rename

# 7. 检测玩家瞄准(射线检测) - 必须放在最后,这样设置的targeted会在下一tick才被检查
execute as @a[scores={sd_menu_sequence=1..}] at @s run function stardew:menu/hover/raycast_start
