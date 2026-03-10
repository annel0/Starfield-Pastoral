# data/stardew/function/utility/chest/check_interaction.mcfunction
# 检测玩家与箱子的交互
# 执行者: interaction 实体 (@s)

# 左键攻击 - 破坏箱子（只有拿镐子才能破坏）
execute if data entity @s attack run execute on attacker run function stardew:utility/chest/break_chest

# 右键交互 - 原版箱子UI会自动打开，不需要额外处理

# 清除交互状态（防止重复触发）
data remove entity @s attack
data remove entity @s interaction
