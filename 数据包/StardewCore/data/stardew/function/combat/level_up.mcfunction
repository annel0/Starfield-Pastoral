# stardew:combat/level_up.mcfunction
# 战斗等级提升系统入口

# 检测玩家是否达到升级条件（等级上限10）
execute if score @s sd_combat_level matches ..9 run function stardew:combat/level_up_check
