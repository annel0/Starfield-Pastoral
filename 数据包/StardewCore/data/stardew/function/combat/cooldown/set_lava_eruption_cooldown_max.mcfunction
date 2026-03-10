# 设置熔岩爆发冷却 bossbar 最大值

# 设置最大值
scoreboard players operation #lava_eruption_cooldown_max sd_const = @s sd_skill_2_cooldown

# 创建或更新 bossbar
bossbar add stardew:lava_eruption_cooldown {"text":"🌋 熔岩爆发 - 冷却中","color":"gray","bold":true}
bossbar set stardew:lava_eruption_cooldown color white
bossbar set stardew:lava_eruption_cooldown style notched_10
execute store result bossbar stardew:lava_eruption_cooldown max run scoreboard players get #lava_eruption_cooldown_max sd_const
bossbar set stardew:lava_eruption_cooldown players @s
bossbar set stardew:lava_eruption_cooldown visible true
