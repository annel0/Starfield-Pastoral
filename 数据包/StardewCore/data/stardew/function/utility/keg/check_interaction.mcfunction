# data/stardew/function/utility/keg/check_interaction.mcfunction
# 检测玩家与小桶的交互
# 执行者: interaction 实体 (@s)

# 左键攻击 - 拆除小桶
execute if data entity @s attack run execute on attacker run function stardew:utility/keg/break_keg

# 右键交互 - 根据手持物品和小桶状态判断
execute if data entity @s interaction run function stardew:utility/keg/interact_handler2

# 清除交互状态 (防止重复触发)
data remove entity @s attack
data remove entity @s interaction
