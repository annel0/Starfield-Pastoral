# stardew:mining/level_up.mcfunction
# [执行者: 玩家]
# 作用：每 tick 调用，检查挖矿经验是否满足升级条件

# 只对等级未满 10 的玩家进行检查
execute if score @s sd_mining_lvl matches ..9 run function stardew:mining/level_up_check
