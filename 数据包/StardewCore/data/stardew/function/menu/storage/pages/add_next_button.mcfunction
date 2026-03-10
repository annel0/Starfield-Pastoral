# data/stardew/function/menu/storage/pages/add_next_button.mcfunction
# 添加下一页按钮

summon interaction ~ ~ ~ {Tags:["sd_menu_new","sd_menu_interact","sd_menu_next_page"],width:0.5f,height:0.5f}
execute rotated ~44 0 positioned ^ ^1.6 ^4.95 run summon item_display ~ ~ ~ {Tags:["sd_menu_new","sd_menu_page_btn","sd_menu_next_page"],item:{id:paper,count:1,components:{"minecraft:custom_model_data":17}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],scale:[0.01f,0.01f,0.01f],translation:[0f,0f,0f]},brightness:{block:15,sky:15}}
