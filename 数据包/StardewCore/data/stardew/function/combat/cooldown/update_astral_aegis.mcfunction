# 更新星辰护盾技能冷却

# 如果护盾还在持续中，不更新冷却bossbar（等护盾结束后才开始冷却倒计时）
execute if entity @s[tag=sd_has_shield] run return 0

# 减少冷却时间
scoreboard players remove @s sd_skill_cooldown 1

# 更新冷却bossbar
execute store result bossbar stardew:astral_aegis_cooldown value run scoreboard players get @s sd_skill_cooldown

# 冷却结束
execute if score @s sd_skill_cooldown matches ..0 run function stardew:combat/cooldown/end_astral_aegis
