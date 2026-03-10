#particle end_rod ~ ~ ~ 0 0 0 0 1 force @a
#碰到方块清除
execute unless block ~ ~ ~ #stardew:air_like run kill @s
#过远清除
execute if entity @a[tag=sd_raycast_player,distance=6..] run kill @s
#转变 - 检测菜单按钮和翻页按钮
execute as @e[tag=sd_menu_button,distance=..0.35] at @s if score @s sd_menu_entity_num = @a[tag=sd_raycast_player,limit=1] sd_menu_sequence run function stardew:menu/hover/on_hover
execute as @e[tag=sd_menu_page_btn,distance=..0.35] at @s if score @s sd_menu_entity_num = @a[tag=sd_raycast_player,limit=1] sd_menu_sequence run function stardew:menu/hover/on_hover
#前进
tp @s ^ ^ ^0.3
execute if entity @s at @s run function stardew:menu/hover/raycast_loop