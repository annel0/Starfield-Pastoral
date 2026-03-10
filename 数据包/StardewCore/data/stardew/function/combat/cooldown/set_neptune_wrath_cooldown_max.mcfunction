# 设置海王怒涛冷却 bossbar 最大值

# 设置最大值
scoreboard players operation #neptune_wrath_cooldown_max sd_const = @s sd_skill_2_cooldown

# 创建或更新 bossbar
bossbar add stardew:neptune_wrath_cooldown {"text":"🌊 海王怒涛 - 冷却中","color":"gray","bold":true}
bossbar set stardew:neptune_wrath_cooldown color white
bossbar set stardew:neptune_wrath_cooldown style progress
execute store result bossbar stardew:neptune_wrath_cooldown max run scoreboard players get #neptune_wrath_cooldown_max sd_const
bossbar set stardew:neptune_wrath_cooldown players @s
bossbar set stardew:neptune_wrath_cooldown visible true
