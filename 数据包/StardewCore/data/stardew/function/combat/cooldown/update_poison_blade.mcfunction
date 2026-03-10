# 更新剧毒之刃技能冷却
scoreboard players remove @s sd_skill_2_cooldown 1
execute store result bossbar stardew:poison_blade_cooldown value run scoreboard players get @s sd_skill_2_cooldown

# 冷却结束
execute if score @s sd_skill_2_cooldown matches ..0 run function stardew:combat/cooldown/end_poison_blade
