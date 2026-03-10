# 设置暗影步技能冷却条最大值

# 创建冷却条
bossbar add stardew:shadow_step_cooldown {"text":"👻 暗影步 - 冷却中","color":"gray","bold":true}
bossbar set stardew:shadow_step_cooldown color white
bossbar set stardew:shadow_step_cooldown style notched_10
bossbar set stardew:shadow_step_cooldown players @s
bossbar set stardew:shadow_step_cooldown visible true

# 设置最大值和当前值
execute store result bossbar stardew:shadow_step_cooldown max run scoreboard players get @s sd_skill_cooldown
execute store result bossbar stardew:shadow_step_cooldown value run scoreboard players get @s sd_skill_cooldown
