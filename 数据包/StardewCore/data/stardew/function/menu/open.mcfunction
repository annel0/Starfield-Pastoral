# data/stardew/function/menu/open.mcfunction
# [执行者: 玩家] 打开菜单UI

# 0. 清理可能残留的旧菜单实体（防止幽灵菜单）
execute if score @s sd_menu_sequence matches 1.. run function stardew:menu/close

# 1. 分配玩家唯一编号
execute store result score @s sd_menu_sequence run scoreboard players get #HaveUsed sd_menu_sequence
scoreboard players add #HaveUsed sd_menu_sequence 1

# 1.5 存储玩家编号到临时变量
execute store result score #CurrentPlayer sd_menu_ctrl run scoreboard players get @s sd_menu_sequence

# 2. 玩家标签
tag @s add sd_menu_opener

# 3. 召唤核心标记点 (用于让所有display朝向中心)
summon marker ~ ~ ~ {Tags:["sd_menu_new","sd_menu_center"]}

# 4. 召唤光幕 - 第一排(上方边框,纸 CMD:2) - 上移0.1格
execute rotated ~ 0 positioned ^ ^0.9 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~8 0 positioned ^ ^0.9 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~16 0 positioned ^ ^0.9 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~24 0 positioned ^ ^0.9 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~32 0 positioned ^ ^0.9 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~40 0 positioned ^ ^0.9 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-8 0 positioned ^ ^0.9 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-16 0 positioned ^ ^0.9 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-24 0 positioned ^ ^0.9 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-32 0 positioned ^ ^0.9 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-40 0 positioned ^ ^0.9 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}

# 5. 召唤光幕 - 第二排
# 5.1 先召唤背景(和第一/三排相同大小) - 纸 CMD:2
execute rotated ~-32 0 positioned ^ ^1.6 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-24 0 positioned ^ ^1.6 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-16 0 positioned ^ ^1.6 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-8 0 positioned ^ ^1.6 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~ 0 positioned ^ ^1.6 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~8 0 positioned ^ ^1.6 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~16 0 positioned ^ ^1.6 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~24 0 positioned ^ ^1.6 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~32 0 positioned ^ ^1.6 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~40 0 positioned ^ ^1.6 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-40 0 positioned ^ ^1.6 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
# 5.2 然后召唤按钮图标(在背景之上,向玩家方向前移0.05格) - 纸 CMD:3-13
execute rotated ~-32 0 positioned ^ ^1.6 ^4.95 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_button","sd_menu_slot_1"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":3}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-24 0 positioned ^ ^1.6 ^4.95 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_button","sd_menu_slot_2"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":4}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-16 0 positioned ^ ^1.6 ^4.95 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_button","sd_menu_slot_3"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":5}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-8 0 positioned ^ ^1.6 ^4.95 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_button","sd_menu_slot_4"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":6}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~ 0 positioned ^ ^1.6 ^4.95 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_button","sd_menu_slot_5"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":7}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~8 0 positioned ^ ^1.6 ^4.95 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_button","sd_menu_slot_6"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":8}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~16 0 positioned ^ ^1.6 ^4.95 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_button","sd_menu_slot_7"],item:{id:bundle,count:1},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~24 0 positioned ^ ^1.6 ^4.95 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_button","sd_menu_slot_8"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":10}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~32 0 positioned ^ ^1.6 ^4.95 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_button","sd_menu_slot_9"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":11}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~40 0 positioned ^ ^1.6 ^4.95 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_page_btn","sd_page_next"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":13}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-40 0 positioned ^ ^1.6 ^4.95 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_page_btn","sd_page_prev"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":12}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}

# 6. 召唤光幕 - 第三排(下方边框,纸 CMD:2) - 下移0.1格
execute rotated ~ 0 positioned ^ ^2.3 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~8 0 positioned ^ ^2.3 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~16 0 positioned ^ ^2.3 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~24 0 positioned ^ ^2.3 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~32 0 positioned ^ ^2.3 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~40 0 positioned ^ ^2.3 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-8 0 positioned ^ ^2.3 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-16 0 positioned ^ ^2.3 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-24 0 positioned ^ ^2.3 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-32 0 positioned ^ ^2.3 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
execute rotated ~-40 0 positioned ^ ^2.3 ^5 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_border"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":2}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}

# 7. 召唤文本显示实体(标题在菜单上方)
execute rotated ~ 0 positioned ^ ^0.4 ^4.7 run summon text_display ~ ~ ~ {text:'{"text":""}',Tags:["sd_menu_new","sd_menu_text","sd_text_title"],billboard:"fixed",brightness:{block:15,sky:15},line_width:1000,background:2130706432}
# 7.5. 召唤描述文本实体(在标题下方，用于显示材料需求等)
execute rotated ~ 0 positioned ^ ^0.2 ^4.7 run summon text_display ~ ~ ~ {text:'{"text":""}',Tags:["sd_menu_new","sd_menu_text","sd_text_desc"],billboard:"center",brightness:{block:15,sky:15},line_width:1000,background:2130706432}

# 8. 让所有display朝向中心点(面向玩家)
execute as @e[tag=sd_menu_new,tag=!sd_menu_center] at @s facing entity @e[tag=sd_menu_center,limit=1] eyes rotated ~180 0 run tp @s ~ ~ ~ ~ ~

# 9. 召唤交互实体(用于检测点击) - Y坐标降低0.5格
# 翻页按钮
execute rotated ~40 0 positioned ^ ^1.1 ^5 run summon interaction ~ ~ ~ {Tags:["sd_menu_new","sd_menu_interact","sd_page_next"],height:0.7,width:0.7}
execute rotated ~-40 0 positioned ^ ^1.1 ^5 run summon interaction ~ ~ ~ {Tags:["sd_menu_new","sd_menu_interact","sd_page_prev"],height:0.7,width:0.7}
# 9个按钮
execute rotated ~-32 0 positioned ^ ^1.1 ^5 run summon interaction ~ ~ ~ {Tags:["sd_menu_new","sd_menu_interact","sd_menu_btn_click","sd_btn_slot_1"],height:0.7,width:0.7}
execute rotated ~-24 0 positioned ^ ^1.1 ^5 run summon interaction ~ ~ ~ {Tags:["sd_menu_new","sd_menu_interact","sd_menu_btn_click","sd_btn_slot_2"],height:0.7,width:0.7}
execute rotated ~-16 0 positioned ^ ^1.1 ^5 run summon interaction ~ ~ ~ {Tags:["sd_menu_new","sd_menu_interact","sd_menu_btn_click","sd_btn_slot_3"],height:0.7,width:0.7}
execute rotated ~-8 0 positioned ^ ^1.1 ^5 run summon interaction ~ ~ ~ {Tags:["sd_menu_new","sd_menu_interact","sd_menu_btn_click","sd_btn_slot_4"],height:0.7,width:0.7}
execute rotated ~ 0 positioned ^ ^1.1 ^5 run summon interaction ~ ~ ~ {Tags:["sd_menu_new","sd_menu_interact","sd_menu_btn_click","sd_btn_slot_5"],height:0.7,width:0.7}
execute rotated ~8 0 positioned ^ ^1.1 ^5 run summon interaction ~ ~ ~ {Tags:["sd_menu_new","sd_menu_interact","sd_menu_btn_click","sd_btn_slot_6"],height:0.7,width:0.7}
execute rotated ~16 0 positioned ^ ^1.1 ^5 run summon interaction ~ ~ ~ {Tags:["sd_menu_new","sd_menu_interact","sd_menu_btn_click","sd_btn_slot_7"],height:0.7,width:0.7}
execute rotated ~24 0 positioned ^ ^1.1 ^5 run summon interaction ~ ~ ~ {Tags:["sd_menu_new","sd_menu_interact","sd_menu_btn_click","sd_btn_slot_8"],height:0.7,width:0.7}
execute rotated ~32 0 positioned ^ ^1.1 ^5 run summon interaction ~ ~ ~ {Tags:["sd_menu_new","sd_menu_interact","sd_menu_btn_click","sd_btn_slot_9"],height:0.7,width:0.7}

# 10. 初始化所有新实体
execute as @e[tag=sd_menu_new] run scoreboard players set @s sd_menu_ctrl 0
execute as @e[tag=sd_menu_new] run scoreboard players operation @s sd_menu_entity_num = #CurrentPlayer sd_menu_ctrl

# 11. 给按钮赋予位置编号
scoreboard players set @e[tag=sd_menu_new,tag=sd_menu_slot_1] sd_menu_slot 1
scoreboard players set @e[tag=sd_menu_new,tag=sd_menu_slot_2] sd_menu_slot 2
scoreboard players set @e[tag=sd_menu_new,tag=sd_menu_slot_3] sd_menu_slot 3
scoreboard players set @e[tag=sd_menu_new,tag=sd_menu_slot_4] sd_menu_slot 4
scoreboard players set @e[tag=sd_menu_new,tag=sd_menu_slot_5] sd_menu_slot 5
scoreboard players set @e[tag=sd_menu_new,tag=sd_menu_slot_6] sd_menu_slot 6
scoreboard players set @e[tag=sd_menu_new,tag=sd_menu_slot_7] sd_menu_slot 7
scoreboard players set @e[tag=sd_menu_new,tag=sd_menu_slot_8] sd_menu_slot 8
scoreboard players set @e[tag=sd_menu_new,tag=sd_menu_slot_9] sd_menu_slot 9
# 给interaction按钮也分配编号
scoreboard players set @e[tag=sd_menu_new,tag=sd_btn_slot_1] sd_menu_slot 1
scoreboard players set @e[tag=sd_menu_new,tag=sd_btn_slot_2] sd_menu_slot 2
scoreboard players set @e[tag=sd_menu_new,tag=sd_btn_slot_3] sd_menu_slot 3
scoreboard players set @e[tag=sd_menu_new,tag=sd_btn_slot_4] sd_menu_slot 4
scoreboard players set @e[tag=sd_menu_new,tag=sd_btn_slot_5] sd_menu_slot 5
scoreboard players set @e[tag=sd_menu_new,tag=sd_btn_slot_6] sd_menu_slot 6
scoreboard players set @e[tag=sd_menu_new,tag=sd_btn_slot_7] sd_menu_slot 7
scoreboard players set @e[tag=sd_menu_new,tag=sd_btn_slot_8] sd_menu_slot 8
scoreboard players set @e[tag=sd_menu_new,tag=sd_btn_slot_9] sd_menu_slot 9

# 12. 初始化瞄准状态
execute as @e[tag=sd_menu_button,tag=sd_menu_new] run scoreboard players set @s sd_menu_targeted_prev 0
execute as @e[tag=sd_menu_page_btn,tag=sd_menu_new] run scoreboard players set @s sd_menu_targeted_prev 0

# 13. 初始化玩家页码和菜单级别
scoreboard players set @s sd_menu_page 0
scoreboard players set @s sd_menu_level 0

# 14. 移除new标签,添加永久标签
tag @e[tag=sd_menu_new] add sd_menu_display
tag @e[tag=sd_menu_new] remove sd_menu_new

# 15. 清理玩家标签
tag @s remove sd_menu_opener

# 16. 播放音效
playsound block.ender_chest.open player @s ~ ~ ~ 1 1.2
