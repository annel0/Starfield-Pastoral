# stardew:mine/barrel/break.mcfunction
# 木桶破坏逻辑（范围破坏：玩家周围3x3）
# 执行者: 攻击木桶的玩家(@s)
# 执行位置: 玩家位置
# 上下文: 从check_interaction 通过 execute on attacker 调用

# 检查玩家是否持有武器
execute unless data entity @s SelectedItem.components."minecraft:custom_data".stardew_weapon run tag @e[tag=sd_current_barrel_target] remove sd_current_barrel_target
execute unless data entity @s SelectedItem.components."minecraft:custom_data".stardew_weapon run return 0

# 清除被攻击木桶的标记
tag @e[tag=sd_current_barrel_target] remove sd_current_barrel_target

# 破坏玩家周围3格范围内的所有木桶
execute at @s as @e[type=interaction,tag=sd_barrel_interaction,distance=..3] at @s run function stardew:mine/barrel/break_impl
