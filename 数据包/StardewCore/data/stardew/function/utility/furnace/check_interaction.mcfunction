# data/stardew/function/utility/furnace/check_interaction.mcfunction
# 检测玩家与熔炉的交互
# 执行者: interaction 实体 (@s)

# 左键攻击 - 拆除熔炉
execute if data entity @s attack run execute on attacker run function stardew:utility/furnace/break_furnace

# 右键交互 - 根据手持物品和熔炉状态判断
execute if data entity @s interaction run function stardew:utility/furnace/interact_handler

# 清除交互状态 (防止重复触发)
data remove entity @s attack
data remove entity @s interaction
