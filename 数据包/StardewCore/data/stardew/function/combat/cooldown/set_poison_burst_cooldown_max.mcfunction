# 设置剧毒爆发技能冷却时间最大值（10秒=200 ticks）
scoreboard players set @s sd_skill_2_cooldown 200
bossbar add stardew:poison_burst_cooldown {"text":"💥 剧毒爆发 - 冷却中","color":"gray","bold":true}
bossbar set stardew:poison_burst_cooldown color white
bossbar set stardew:poison_burst_cooldown max 200
