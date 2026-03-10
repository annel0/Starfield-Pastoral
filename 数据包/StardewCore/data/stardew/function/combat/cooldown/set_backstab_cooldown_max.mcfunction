# 设置背刺技能冷却时间最大值（从武器读取）
bossbar remove stardew:backstab_cooldown
bossbar add stardew:backstab_cooldown {"text":"🗡 背刺 - 冷却中","color":"gray","bold":true}
bossbar set stardew:backstab_cooldown color white
execute store result bossbar stardew:backstab_cooldown max run scoreboard players get @s sd_skill_cooldown
execute store result bossbar stardew:backstab_cooldown value run scoreboard players get @s sd_skill_cooldown
bossbar set stardew:backstab_cooldown players @s
