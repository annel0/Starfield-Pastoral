# 清空所有靴子装备
scoreboard players set @s sd_equip_boots 0
data remove storage stardew:equipment boots
scoreboard players set @s sd_fishing_bonus 0
scoreboard players set @s sd_nature_regen_timer 0

# 移除所有效果
effect clear @s speed
effect clear @s jump_boost
effect clear @s fire_resistance

tellraw @s [{"text":"[测试] ","color":"red"},{"text":"已移除所有靴子装备和效果","color":"white"}]
