# 关闭对话框
scoreboard players reset @s stardew_dialogue_close

# 播放关闭音效
playsound ui.toast.out block @s ~ ~ ~ 1 1.0

# 移除所有对话框实体
kill @e[type=interaction,tag=dialogue_menu,distance=..3]
kill @e[type=text_display,tag=dialogue_element,distance=..3]
kill @e[type=item_display,tag=dialogue_element,distance=..3]

# 移除骑乘用的基座实体
kill @e[type=item_display,tag=dialogue_base,distance=..3]

# 移除玩家标签
tag @s remove in_dialogue
tag @s remove closing

# 清除对话数据
data remove storage stardew:dialogue current
