# combat/record_skill_damage.mcfunction
# 记录技能伤害到DPS系统
# 调用此函数前,#damage sd_temp 必须已经包含伤害值

# 将技能伤害累加到最近的玩家的DPS统计中
scoreboard players operation @p sd_player_total_dmg += #damage sd_temp
