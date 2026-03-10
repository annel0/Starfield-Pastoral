# data/stardew/function/mining/check_interaction.mcfunction
# 检测玩家与石头的交互
# 执行者: interaction 实体 (@s)

# 左键攻击 - 挖掘
# 切换上下文到攻击者(玩家)执行 on_mine
execute if data entity @s attack run execute on attacker run function stardew:mining/on_mine

# 右键交互 - 收集宝石（如果是宝石矿）
# 1. 标记当前交互实体
execute if data entity @s interaction if entity @s[tag=sd_gem_ore] run tag @s add sd_current_interaction
# 2. 切换上下文到交互者(玩家)执行 collect_gem
execute if entity @s[tag=sd_current_interaction] on target run function stardew:mining/collect_gem
# 注意: collect_gem 内部会 kill 实体,所以不需要清除标签

# 清除交互状态 (防止重复触发)
# 注意：interaction实体会自动清除attack/interaction吗？通常需要手动清除或等待
# 这里建议在 on_mine / collect_gem 内部处理清除，或者在这里清除
data remove entity @s attack
data remove entity @s interaction
