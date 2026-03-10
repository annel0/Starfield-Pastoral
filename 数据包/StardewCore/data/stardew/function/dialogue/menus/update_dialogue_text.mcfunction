# 更新对话文字内容（翻页时调用，不重新生成背景和头像）
# 此函数以 interaction 身份调用，@s 是 interaction

# 准备当前页的文本到临时storage
function stardew:dialogue/menus/prepare_text

# 4. 对话文本（第一行）
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
execute store result score #current_page sd_temp run data get storage stardew:dialogue current.dialogue_page
execute store result score #total_pages sd_temp run data get storage stardew:dialogue current.total_pages
scoreboard players add #current_page sd_temp 1

# 如果是最后一页（或只有1页），显示继续按钮
execute if score #current_page sd_temp >= #total_pages sd_temp run function stardew:dialogue/menus/show_continue_button
