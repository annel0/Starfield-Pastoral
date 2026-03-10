# 处理门的右键点击
# 执行者：被点击的门交互体 (@s)

# 对点击的玩家执行传送
execute on target run function stardew:building/check_door_interaction

# 清除交互数据
data remove entity @s interaction
