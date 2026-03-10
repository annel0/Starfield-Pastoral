# 处理玩家点击对话框按钮

# 继续对话（点击任意位置或"继续"按钮）
execute if score @s stardew_dialogue_select matches 1 run function stardew:dialogue/icons_click/continue

# 关闭对话框
execute if score @s stardew_dialogue_select matches 99 run function stardew:dialogue/player_close

# 对话选项（如果有）
execute if score @s stardew_dialogue_select matches 10..20 run function stardew:dialogue/icons_click/option_select

# 重置选择
scoreboard players reset @s stardew_dialogue_select
