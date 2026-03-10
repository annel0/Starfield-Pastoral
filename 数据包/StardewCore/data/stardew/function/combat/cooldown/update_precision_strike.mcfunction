# 更新精准打击冷却 bossbar

# 如果精准打击还在持续中，不更新冷却bossbar（等持续结束后才开始冷却倒计时）
execute if entity @s[tag=sd_precision_active] run return 0

# 减少冷却时间
scoreboard players remove @s sd_skill_cooldown 1

# 更新冷却bossbar值
execute store result bossbar stardew:precision_strike_cooldown value run scoreboard players get @s sd_skill_cooldown

# 冷却结束 - 隐藏冷却bossbar
execute if score @s sd_skill_cooldown matches ..0 run tag @s remove sd_using_precision
execute if score @s sd_skill_cooldown matches ..0 run bossbar set stardew:precision_strike_cooldown visible false
