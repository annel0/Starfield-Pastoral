# data/stardew/function/equipment/display/show_ring1.mcfunction
# [执行者: 玩家] 在菜单中显示戒指1

execute store result score #CurrentPlayer sd_menu_ctrl run scoreboard players get @s sd_menu_sequence

# 显示戒指：设置为胡萝卜钓竿 + CMD
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl run data merge entity @s {item:{id:"minecraft:carrot_on_a_stick",count:1}}
execute as @e[type=item_display,tag=sd_menu_button,tag=sd_menu_slot_2] if score @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl store result entity @s item.components."minecraft:custom_model_data" int 1 run scoreboard players get @a[limit=1,sort=nearest] sd_equip_ring1_cmd
