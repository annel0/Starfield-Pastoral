# 对话系统主循环
# 在玩家有in_dialogue标签时执行

# 检测玩家是否试图打开对话框
execute if score @s stardew_dialogue_open matches 1.. run function stardew:dialogue/player_open

# 检测玩家是否试图关闭对话框
execute if score @s stardew_dialogue_close matches 1.. run function stardew:dialogue/player_close

# 检测玩家点击对话框
execute if score @s stardew_dialogue_select matches 1.. run function stardew:dialogue/player_click

# 光线投射检测（用于高亮可点击元素）
execute anchored eyes positioned ^ ^ ^1 run function stardew:dialogue/ray
