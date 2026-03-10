# 检测攻击冷却

# 检查玩家是否在攻击冷却中
# 如果sd_attack_cooldown > 0，表示还在冷却中，限制伤害为1

execute if score @s sd_attack_cooldown matches 1.. run scoreboard players set #damage sd_temp 1
execute if score @s sd_attack_cooldown matches 1.. run scoreboard players set #cooldown_penalty sd_temp 1
execute unless score @s sd_attack_cooldown matches 1.. run scoreboard players set #cooldown_penalty sd_temp 0

# 获取武器攻击速度（从NBT读取）
execute store result score #weapon_speed sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_attack_speed 10

# 如果武器没有攻击速度，使用默认值16（1.6攻击/秒）
execute unless score #weapon_speed sd_temp matches 1.. run scoreboard players set #weapon_speed sd_temp 16

# 计算冷却时间 = 20 / 攻击速度（ticks）
# 例如：1.6攻击/秒 = 20 / 1.6 ≈ 12.5 ticks
scoreboard players set #20 sd_temp 20
scoreboard players operation #cooldown_ticks sd_temp = #20 sd_temp
scoreboard players operation #cooldown_ticks sd_temp *= #10 sd_temp
scoreboard players operation #cooldown_ticks sd_temp /= #weapon_speed sd_temp

# 设置冷却（本次攻击后）
scoreboard players operation @s sd_attack_cooldown = #cooldown_ticks sd_temp
