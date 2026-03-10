# 更新龙牙狂怒技能冷却

# 如果还在狂怒状态中，不更新冷却bossbar（等狂怒结束后才开始冷却倒计时）
execute if score @s sd_fury_timer matches 1.. run return 0

# 减少冷却时间
scoreboard players remove @s sd_skill_2_cooldown 1

# 更新冷却bossbar
execute store result bossbar stardew:dragon_fury_cooldown value run scoreboard players get @s sd_skill_2_cooldown

# 冷却结束
execute if score @s sd_skill_2_cooldown matches ..0 run function stardew:combat/cooldown/end_dragon_fury

