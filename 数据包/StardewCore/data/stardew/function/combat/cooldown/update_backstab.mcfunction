# 更新背刺技能冷却
scoreboard players remove @s sd_skill_cooldown 1
execute store result bossbar stardew:backstab_cooldown value run scoreboard players get @s sd_skill_cooldown

# 冷却结束
execute if score @s sd_skill_cooldown matches ..0 run function stardew:combat/cooldown/end_backstab
