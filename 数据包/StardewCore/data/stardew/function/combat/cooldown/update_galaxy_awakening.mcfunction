# 更新银河觉醒技能冷却

# 如果还在觉醒状态中，不更新冷却bossbar（等觉醒结束后才开始冷却倒计时）
execute if score @s sd_awakening_timer matches 1.. run return 0

# 减少冷却时间
scoreboard players remove @s sd_skill_2_cooldown 1

# 更新冷却bossbar
execute store result bossbar stardew:galaxy_awakening_cooldown value run scoreboard players get @s sd_skill_2_cooldown

# 冷却结束
execute if score @s sd_skill_2_cooldown matches ..0 run function stardew:combat/cooldown/end_galaxy_awakening
