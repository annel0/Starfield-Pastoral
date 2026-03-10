# 对话系统测试 - 强制关闭所有对话框

# 移除所有对话相关的实体
kill @e[type=item_display,tag=dialogue_base]
kill @e[type=interaction,tag=dialogue_menu]
kill @e[type=text_display,tag=dialogue_element]
kill @e[type=item_display,tag=dialogue_element]

# 移除所有玩家的对话标签
tag @a remove in_dialogue
tag @a remove closing
tag @a remove play_dialogue_animation

# 清除数据
data remove storage stardew:dialogue current

# 重置计分板
scoreboard players reset @a stardew_dialogue_open
scoreboard players reset @a stardew_dialogue_close
scoreboard players reset @a stardew_dialogue_select

tellraw @s {"text":"[对话系统] 已强制关闭所有对话框","color":"yellow"}
