# 设置陨星打击技能冷却时间最大值（根据武器tier显示不同标题）

# 移除旧bossbar
bossbar remove stardew:meteor_strike_cooldown

# 创建冷却bossbar - 根据tier显示不同标题
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} run bossbar add stardew:meteor_strike_cooldown {"text":"☄ 陨星打击 - 冷却中","color":"gray","bold":true}
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run bossbar add stardew:meteor_strike_cooldown {"text":"☄ 陨星打击 - 冷却中","color":"gray","bold":true}

bossbar set stardew:meteor_strike_cooldown color white
execute store result bossbar stardew:meteor_strike_cooldown max run scoreboard players get @s sd_skill_cooldown
execute store result bossbar stardew:meteor_strike_cooldown value run scoreboard players get @s sd_skill_cooldown
bossbar set stardew:meteor_strike_cooldown players @s
bossbar set stardew:meteor_strike_cooldown visible false
