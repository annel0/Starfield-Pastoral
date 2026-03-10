# data/stardew/function/farming/level_up.mcfunction
# [执行者: 玩家]
# 作用：开始农耕技能的升级检查和处理

# 如果等级未达到上限，开始检查升级条件
execute if score @s sd_farming_lvl matches ..9 run function stardew:farming/level_up_check
