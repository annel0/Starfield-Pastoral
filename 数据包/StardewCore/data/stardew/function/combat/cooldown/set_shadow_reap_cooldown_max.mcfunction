# 设置暗影收割技能冷却时间最大值（8秒=160 ticks）
scoreboard players set @s sd_skill_2_cooldown 160
bossbar add stardew:shadow_reap_cooldown {"text":"💀 暗影收割 - 冷却中","color":"gray","bold":true}
bossbar set stardew:shadow_reap_cooldown color white
bossbar set stardew:shadow_reap_cooldown max 160
