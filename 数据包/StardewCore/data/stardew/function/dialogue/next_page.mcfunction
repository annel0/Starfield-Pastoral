# 显示对话的下一页

# 更新storage中的当前页数
execute store result storage stardew:dialogue current.dialogue_page int 1 run scoreboard players get #current_page sd_temp

# 只清除对话文字和按钮，保留背景、头像和NPC名字（避免闪烁）
kill @e[type=text_display,tag=dialogue_text,distance=..5]
kill @e[type=item_display,tag=continue_button,distance=..5]

# 在 interaction 的位置更新对话文字
execute as @e[type=interaction,tag=dialogue_menu,distance=..5,limit=1,sort=nearest] at @s run function stardew:dialogue/menus/update_dialogue_text
