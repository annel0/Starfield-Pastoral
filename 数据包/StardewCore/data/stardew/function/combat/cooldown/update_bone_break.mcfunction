# 更新骨裂打击技能冷却

# 显示bossbar
bossbar set stardew:bone_break_cooldown players @s
bossbar set stardew:bone_break_cooldown visible true

# 减少冷却
scoreboard players remove @s sd_skill_cooldown 1

# 更新bossbar值
execute store result bossbar stardew:bone_break_cooldown value run scoreboard players get @s sd_skill_cooldown

# 冷却结束
execute if score @s sd_skill_cooldown matches ..0 run function stardew:combat/cooldown/end_bone_break
