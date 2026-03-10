# 处理出口的右键点击
# 执行者：被点击的出口交互体 (@s)

# 对点击的玩家执行传送（只传送在室内的玩家）
execute on target if entity @s[tag=inside_building] run function stardew:building/exit/return_to_town

# 清除交互数据
data remove entity @s interaction
