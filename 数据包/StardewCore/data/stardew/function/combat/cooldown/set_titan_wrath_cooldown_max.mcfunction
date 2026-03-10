# 设置泰坦之怒技能冷却时间最大值（根据武器tier调整持续时间）

# 移除旧的bossbar（如果存在）
bossbar remove stardew:titan_wrath_cooldown
bossbar remove stardew:titan_wrath_duration

# 创建冷却bossbar（灰色，显示"冷却中"）
bossbar add stardew:titan_wrath_cooldown {"text":"⚡ 泰坦之怒 - 冷却中","color":"gray","bold":true}
bossbar set stardew:titan_wrath_cooldown color white
execute store result bossbar stardew:titan_wrath_cooldown max run scoreboard players get @s sd_skill_2_cooldown
execute store result bossbar stardew:titan_wrath_cooldown value run scoreboard players get @s sd_skill_2_cooldown
bossbar set stardew:titan_wrath_cooldown players @s
bossbar set stardew:titan_wrath_cooldown visible false

# 创建持续时间bossbar（红色，显示正常技能名，根据tier）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} run bossbar add stardew:titan_wrath_duration {"text":"⚡ 泰坦之怒","color":"#9933FF","bold":true}
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run bossbar add stardew:titan_wrath_duration {"text":"⚡ 泰坦之怒","color":"#FF00FF","bold":true}
bossbar set stardew:titan_wrath_duration color red

# 根据tier设置最大值（银河=200，无限=300）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} run bossbar set stardew:titan_wrath_duration max 200
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run bossbar set stardew:titan_wrath_duration max 300

execute store result bossbar stardew:titan_wrath_duration value run scoreboard players get @s sd_wrath_timer
bossbar set stardew:titan_wrath_duration players @s
bossbar set stardew:titan_wrath_duration visible true
