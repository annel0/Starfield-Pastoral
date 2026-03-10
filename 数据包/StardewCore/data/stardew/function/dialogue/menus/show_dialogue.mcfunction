# 显示对话框UI（星露谷物语风格）
# 使用 string CMD 11001 的统一背景 和 12000+ 的NPC头像

# 1. 对话框统一背景（string CMD 11001 - 包含边框、背景、头像框等所有UI元素）
summon item_display ~ ~ ~ {Tags:["dialogue_element","dialogue_bg","new_element"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[4.0f,4.0f,0.1f],right_rotation:[0f,1f,0f,0f]},item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":11001}}}
data modify entity @e[type=item_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] Rotation set from entity @s Rotation
data modify entity @e[type=item_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] Rotation[1] set value 0f
tag @e[type=item_display,tag=new_element,distance=..3,limit=1,sort=nearest] remove new_element

# 2. NPC Portrait（头像 - 从 storage 读取 portrait，如果没有则使用默认 CMD 12000）
summon item_display ~ ~ ~ {Tags:["dialogue_element","npc_portrait","new_element"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.84f,0.84f,0.1f],right_rotation:[0f,1f,0f,0f]},item:{id:"minecraft:string",count:1,components:{"minecraft:custom_model_data":12000}}}
execute if data storage stardew:dialogue current.portrait run data modify entity @e[type=item_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] item set from storage stardew:dialogue current.portrait
execute rotated as @s rotated ~ 0 positioned as @e[type=item_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] run tp @e[type=item_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] ^1.1875 ^0.125 ^ ~ ~
tag @e[type=item_display,tag=new_element,distance=..3,limit=1,sort=nearest] remove new_element

# 3. NPC名字标签（从 storage 读取 npc_display_name）
summon text_display ~ ~ ~ {Tags:["dialogue_element","npc_name","new_element"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.1f],right_rotation:[0f,1f,0f,0f]},text:'{"text":"NPC","color":"#6B4423","bold":true}',background:0,text_opacity:255,shadow:1b,alignment:"center"}
execute if data storage stardew:dialogue current.npc_display_name run data modify entity @e[type=text_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] text set from storage stardew:dialogue current.npc_display_name
execute rotated as @s rotated ~ 0 positioned as @e[type=text_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] run tp @e[type=text_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] ^1.1875 ^-0.5 ^-0.0625 ~ ~
tag @e[type=text_display,tag=new_element,distance=..3,limit=1,sort=nearest] remove new_element

# 4. 对话文本（第一行）- 从 storage 读取当前页的文本
# 先准备当前页的文本到临时storage
function stardew:dialogue/menus/prepare_text

summon text_display ~ ~ ~ {Tags:["dialogue_element","dialogue_text","dialogue_line_1","new_element"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.45f,0.45f,0.1f],right_rotation:[0f,1f,0f,0f]},text:'{"text":""}',background:0,text_opacity:255,line_width:380,alignment:"left"}
execute if data storage stardew:dialogue temp_text[0] run data modify entity @e[type=text_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] text set from storage stardew:dialogue temp_text[0]
execute rotated as @s rotated ~ 0 positioned as @e[type=text_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] run tp @e[type=text_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] ^-0.75 ^0.375 ^-0.0625 ~ ~
tag @e[type=text_display,tag=new_element,distance=..3,limit=1,sort=nearest] remove new_element

# 5. 对话文本（第二行）
summon text_display ~ ~ ~ {Tags:["dialogue_element","dialogue_text","dialogue_line_2","new_element"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.45f,0.45f,0.1f],right_rotation:[0f,1f,0f,0f]},text:'{"text":""}',background:0,text_opacity:255,line_width:380,alignment:"left"}
execute if data storage stardew:dialogue temp_text[1] run data modify entity @e[type=text_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] text set from storage stardew:dialogue temp_text[1]
execute rotated as @s rotated ~ 0 positioned as @e[type=text_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] run tp @e[type=text_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] ^-0.75 ^ ^-0.0625 ~ ~
tag @e[type=text_display,tag=new_element,distance=..3,limit=1,sort=nearest] remove new_element

# 6. 对话文本（第三行）
summon text_display ~ ~ ~ {Tags:["dialogue_element","dialogue_text","dialogue_line_3","new_element"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.45f,0.45f,0.1f],right_rotation:[0f,1f,0f,0f]},text:'{"text":""}',background:0,text_opacity:255,line_width:380,alignment:"left"}
execute if data storage stardew:dialogue temp_text[2] run data modify entity @e[type=text_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] text set from storage stardew:dialogue temp_text[2]
execute rotated as @s rotated ~ 0 positioned as @e[type=text_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] run tp @e[type=text_display,tag=new_element,distance=..0.2,limit=1,sort=nearest] ^-0.75 ^-0.375 ^-0.0625 ~ ~
tag @e[type=text_display,tag=new_element,distance=..3,limit=1,sort=nearest] remove new_element

# 7. 继续按钮（只在最后一页显示 CMD 11004）
# 获取当前页（从0开始）和总页数，判断是否是最后一页
execute store result score #current_page sd_temp run data get storage stardew:dialogue current.dialogue_page
execute store result score #total_pages sd_temp run data get storage stardew:dialogue current.total_pages

# dialogue_page 从0开始，所以第1页是0，第2页是1，第3页是2
# total_pages 是实际页数，所以要判断 dialogue_page == total_pages - 1
# 即：dialogue_page + 1 >= total_pages
scoreboard players add #current_page sd_temp 1

# 如果是最后一页（或只有1页），显示继续按钮
execute if score #current_page sd_temp >= #total_pages sd_temp run function stardew:dialogue/menus/show_continue_button

# 播放动画
tag @p[tag=in_dialogue,distance=..2] add play_dialogue_animation
schedule function stardew:dialogue/animation/show 2t replace
