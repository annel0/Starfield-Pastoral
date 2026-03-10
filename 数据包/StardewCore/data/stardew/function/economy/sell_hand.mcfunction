# data/stardew/functions/economy/sell_hand.mcfunction
# [执行者: 交互实体]
# 我们需要找到那个交互的玩家 (存放在 interaction.player UUID 中)

# 1. 定位玩家并让玩家执行出售
# 这是一个比较高级的技巧：通过 UUID 匹配玩家
# 简单替代方案：直接找最近的玩家 (distance=..4)
execute at @s as @p[distance=..4] run function stardew:economy/sell_logic

# 2. 清除交互状态 (防止无限触发)
data remove entity @s interaction