# 更新龙牙连击技能冷却

# 显示冷却bossbar
bossbar set stardew:dragon_combo_cooldown players @s
bossbar set stardew:dragon_combo_cooldown visible true

# 减少冷却时间
scoreboard players remove @s sd_dragon_combo_cooldown 1

# 更新冷却bossbar
execute store result bossbar stardew:dragon_combo_cooldown value run scoreboard players get @s sd_dragon_combo_cooldown

# 冷却结束
execute if score @s sd_dragon_combo_cooldown matches ..0 run function stardew:combat/cooldown/end_dragon_combo
