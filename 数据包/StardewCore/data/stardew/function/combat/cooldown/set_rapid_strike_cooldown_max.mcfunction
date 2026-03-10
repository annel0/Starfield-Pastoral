# 设置连击技能冷却时间最大值（5秒=100 ticks）

bossbar remove stardew:rapid_strike_cooldown
bossbar add stardew:rapid_strike_cooldown {"text":"⚔ 连击 - 冷却中","color":"gray","bold":true}
bossbar set stardew:rapid_strike_cooldown color white
execute store result bossbar stardew:rapid_strike_cooldown max run scoreboard players get @s sd_skill_2_cooldown
execute store result bossbar stardew:rapid_strike_cooldown value run scoreboard players get @s sd_skill_2_cooldown
