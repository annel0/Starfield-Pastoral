# 更新星辰护盾状态

# 减少持续时间
scoreboard players remove @s sd_shield_timer 1

# 更新持续时间bossbar
execute store result bossbar stardew:astral_aegis_duration value run scoreboard players get @s sd_shield_timer

# 增加旋转角度（5度/tick，正常旋转速度）
scoreboard players add @s sd_shield_rotation 5
execute if score @s sd_shield_rotation matches 360.. run scoreboard players set @s sd_shield_rotation 0

# 更新护盾球位置（让它们环绕玩家旋转）
execute as @e[tag=sd_shield_orb] if score @s sd_shield_id = @p[tag=sd_has_shield] sd_shield_id at @p[tag=sd_has_shield] run function stardew:combat/weapon/astral_aegis_rotate

# 中心粒子效果（让效果更华丽）
particle minecraft:end_rod ~ ~1 ~ 0.3 0.3 0.3 0.05 3 force
particle minecraft:enchant ~ ~1 ~ 0.4 0.4 0.4 0.5 5 force

# 护盾结束 - 切换到冷却bossbar
execute if score @s sd_shield_timer matches ..0 run bossbar set stardew:astral_aegis_duration visible false
execute if score @s sd_shield_timer matches ..0 store result bossbar stardew:astral_aegis_cooldown value run scoreboard players get @s sd_skill_cooldown
execute if score @s sd_shield_timer matches ..0 run bossbar set stardew:astral_aegis_cooldown visible true
execute if score @s sd_shield_timer matches ..0 run function stardew:combat/weapon/astral_aegis_end

# 每10 ticks更新一次actionbar显示
scoreboard players operation #shield_display sd_temp = @s sd_shield_timer
scoreboard players operation #shield_display sd_temp %= #10 sd_const
execute if score #shield_display sd_temp matches 0 run function stardew:combat/weapon/astral_aegis_display
