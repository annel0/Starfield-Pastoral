# data/stardew/function/status/debuff/weakness_tick.mcfunction
# 虚弱效果 tick 处理 (暗影生物攻击造成)
# 效果: 攻击力降低

# 持续时间倒计时
scoreboard players remove @s sd_weakness_duration 1

# 应用虚弱效果 (降低攻击力通过计算实现，这里只做标记)
# 实际效果在 combat/player_attack.mcfunction 中计算

# 粒子效果
particle minecraft:squid_ink ~ ~1 ~ 0.3 0.5 0.3 0.02 3

# 持续时间结束时清除效果
execute if score @s sd_weakness_duration matches ..0 run function stardew:status/debuff/weakness_remove
