# 设置剧毒之刃技能冷却时间最大值（从武器读取）
bossbar remove stardew:poison_blade_cooldown
bossbar add stardew:poison_blade_cooldown {"text":"☠ 剧毒之刃 - 冷却中","color":"gray","bold":true}
bossbar set stardew:poison_blade_cooldown color white
execute store result bossbar stardew:poison_blade_cooldown max run scoreboard players get @s sd_skill_2_cooldown
execute store result bossbar stardew:poison_blade_cooldown value run scoreboard players get @s sd_skill_2_cooldown
bossbar set stardew:poison_blade_cooldown players @s
