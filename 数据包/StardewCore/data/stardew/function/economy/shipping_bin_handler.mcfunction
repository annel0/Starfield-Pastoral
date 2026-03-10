# 1. 检查交互
execute unless data entity @s interaction run return 0

# 2. 执行出售
# 寻找最近的玩家进行结算
execute at @s as @p run function stardew:economy/sell_logic

# 3. 清除状态
data remove entity @s interaction