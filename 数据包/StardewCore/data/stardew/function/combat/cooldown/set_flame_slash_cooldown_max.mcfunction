# 设置火焰斩冷却 bossbar 最大值

# 设置最大值
scoreboard players operation #flame_slash_cooldown_max sd_const = @s sd_flame_slash_cooldown

# 删除旧bossbar，创建新的
bossbar remove stardew:flame_slash_cooldown
bossbar add stardew:flame_slash_cooldown {"text":"🔥 火焰斩 - 冷却中","color":"gray","bold":true}
bossbar set stardew:flame_slash_cooldown color white
bossbar set stardew:flame_slash_cooldown style progress
execute store result bossbar stardew:flame_slash_cooldown max run scoreboard players get #flame_slash_cooldown_max sd_const
bossbar set stardew:flame_slash_cooldown players @s
bossbar set stardew:flame_slash_cooldown visible true
