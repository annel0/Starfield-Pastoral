# 设置毒刃技能冷却时间最大值（4秒=80 ticks）
scoreboard players set @s sd_skill_cooldown 80
bossbar add stardew:poison_strike_cooldown {"text":"☠ 毒刃 - 冷却中","color":"gray","bold":true}
bossbar set stardew:poison_strike_cooldown color white
bossbar set stardew:poison_strike_cooldown max 80
