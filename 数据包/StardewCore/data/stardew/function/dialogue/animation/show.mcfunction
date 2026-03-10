# 对话框显示动画
# 使用interpolation来实现淡入效果

# 先设置初始透明度为0
execute as @e[type=text_display,tag=dialogue_element,tag=!dialogue_animated] run data modify entity @s text_opacity set value 0
execute as @e[type=item_display,tag=dialogue_element,tag=!dialogue_animated] run data modify entity @s brightness set value {sky:0,block:0}

# 然后开始插值动画到完全可见
execute as @e[type=text_display,tag=dialogue_element,tag=!dialogue_animated] run data modify entity @s start_interpolation set value 0
execute as @e[type=text_display,tag=dialogue_element,tag=!dialogue_animated] run data modify entity @s interpolation_duration set value 8
execute as @e[type=text_display,tag=dialogue_element,tag=!dialogue_animated] run data modify entity @s text_opacity set value 255

execute as @e[type=item_display,tag=dialogue_element,tag=!dialogue_animated] run data modify entity @s start_interpolation set value 0
execute as @e[type=item_display,tag=dialogue_element,tag=!dialogue_animated] run data modify entity @s interpolation_duration set value 8
execute as @e[type=item_display,tag=dialogue_element,tag=!dialogue_animated] run data modify entity @s brightness set value {sky:15,block:15}

# 标记已动画
tag @e[type=text_display,tag=dialogue_element] add dialogue_animated
tag @e[type=item_display,tag=dialogue_element] add dialogue_animated

# 播放音效
execute as @a[tag=in_dialogue,tag=play_dialogue_animation] at @s run playsound block.note_block.chime block @s ~ ~ ~ 0.5 1.5

# 移除动画标签
tag @a[tag=play_dialogue_animation] remove play_dialogue_animation
