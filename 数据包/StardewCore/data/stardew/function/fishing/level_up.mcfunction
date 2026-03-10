# data/stardew/functions/fishing/level_up.mcfunction
# [执行者: 玩家]
# 作用：开始钓鱼技能的升级检查和处理。

# 1. 如果等级未达到上限，开始检查升级条件
execute if score @s sd_fishing_lvl matches ..9 run function stardew:fishing/level_up_check