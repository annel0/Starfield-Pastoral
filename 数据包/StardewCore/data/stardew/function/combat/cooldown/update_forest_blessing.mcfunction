# 更新森林赐福技能冷却

# 如果还在回血中，不更新冷却bossbar（等回血结束后才开始冷却倒计时）
execute if score @s sd_regen_timer matches 1.. run return 0

# 减少冷却时间
scoreboard players remove @s sd_skill_cooldown 1

# 更新冷却bossbar
execute store result bossbar stardew:forest_blessing_cooldown value run scoreboard players get @s sd_skill_cooldown

# 冷却结束
execute if score @s sd_skill_cooldown matches ..0 run function stardew:combat/cooldown/end_forest_blessing
