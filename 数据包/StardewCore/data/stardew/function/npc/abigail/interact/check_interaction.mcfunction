# 检查玩家右键交互时的手持物品，决定是对话还是送礼
# @s = 玩家

# 如果空手  对话
execute unless data entity @s SelectedItem run return run function stardew:npc/abigail/interact/talk

# 如果手持工具（有tool_type标记） 对话
execute if data entity @s SelectedItem.components."minecraft:custom_data".tool_type run return run function stardew:npc/abigail/interact/talk

# 如果手持武器（后续可以添加武器判断） 对话
# TODO: 添加武器判断

# 其他情况（手持物品但不是工具/武器） 送礼
function stardew:npc/abigail/interact/gift
