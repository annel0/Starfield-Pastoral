# 更新火焰斩冷却 bossbar

# 减少冷却时间
scoreboard players remove @s sd_flame_slash_cooldown 1

# 更新 bossbar 值
execute store result bossbar stardew:flame_slash_cooldown value run scoreboard players get @s sd_flame_slash_cooldown

# 冷却结束
execute if score @s sd_flame_slash_cooldown matches ..0 run tag @s remove sd_using_flame_slash
execute if score @s sd_flame_slash_cooldown matches ..0 run bossbar set stardew:flame_slash_cooldown visible false
