# 选择对话选项
# 处理玩家选择的对话选项

playsound ui.button.click block @s ~ ~ ~ 0.5 1.2

# TODO: 根据选择的选项ID处理不同的对话分支

# 暂时显示选择反馈
tellraw @s {"text":"你选择了一个选项！","color":"yellow"}

# 继续后续对话或关闭
function stardew:dialogue/player_close
