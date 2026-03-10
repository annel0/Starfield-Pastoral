# 玩家攻击怪物（advancement触发）

# 检测是否持有星露谷武器
execute unless data entity @s SelectedItem.components."minecraft:custom_data".stardew_weapon run return run advancement revoke @s only stardew:combat/player_hurt_entity

# 重置暴击标记
scoreboard players set #is_critical sd_temp 0

# 获取武器数据
execute store result score #weapon_min_dmg sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_min
execute store result score #weapon_max_dmg sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_damage_max
execute store result score #weapon_crit sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_crit_chance 100
execute store result score #weapon_range sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_range 10

# 如果武器没有设置攻击范围，使用默认值30（3.0格）
execute unless score #weapon_range sd_temp matches 1.. run scoreboard players set #weapon_range sd_temp 30

# 检查武器数据是否有效
execute unless score #weapon_min_dmg sd_temp matches 1.. run return run advancement revoke @s only stardew:combat/player_hurt_entity

# 计算随机伤害（min到max之间）
scoreboard players operation #damage_range sd_temp = #weapon_max_dmg sd_temp
scoreboard players operation #damage_range sd_temp -= #weapon_min_dmg sd_temp
scoreboard players add #damage_range sd_temp 1
execute store result score #random_bonus sd_temp run random value 0..100
scoreboard players operation #random_bonus sd_temp %= #damage_range sd_temp
scoreboard players operation #damage sd_temp = #weapon_min_dmg sd_temp
scoreboard players operation #damage sd_temp += #random_bonus sd_temp

# 【新增】检查攻击冷却（如果未完全冷却，限制伤害为1）
function stardew:combat/check_attack_cooldown

# 【龙牙狂怒】+50%伤害加成
execute if entity @s[tag=sd_dragon_fury] run scoreboard players set #150 sd_const 150
execute if entity @s[tag=sd_dragon_fury] run scoreboard players operation #damage sd_temp *= #150 sd_const
execute if entity @s[tag=sd_dragon_fury] run scoreboard players set #100 sd_const 100
execute if entity @s[tag=sd_dragon_fury] run scoreboard players operation #damage sd_temp /= #100 sd_const

# 【泰坦之怒】伤害加成（银河+30%，无限+50%）
execute if entity @s[tag=sd_titan_wrath] if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} run scoreboard players set #130 sd_const 130
execute if entity @s[tag=sd_titan_wrath] if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} run scoreboard players operation #damage sd_temp *= #130 sd_const
execute if entity @s[tag=sd_titan_wrath] if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run scoreboard players set #150 sd_const 150
execute if entity @s[tag=sd_titan_wrath] if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run scoreboard players operation #damage sd_temp *= #150 sd_const
execute if entity @s[tag=sd_titan_wrath] run scoreboard players set #100 sd_const 100
execute if entity @s[tag=sd_titan_wrath] run scoreboard players operation #damage sd_temp /= #100 sd_const

# 【戒指攻击加成】应用攻击百分比加成 (sd_attack_bonus: 10 = +10%)
execute if score @s sd_attack_bonus matches 1.. run scoreboard players operation #attack_multiplier sd_temp = @s sd_attack_bonus
execute if score @s sd_attack_bonus matches 1.. run scoreboard players add #attack_multiplier sd_temp 100
execute if score @s sd_attack_bonus matches 1.. run scoreboard players operation #damage sd_temp *= #attack_multiplier sd_temp
execute if score @s sd_attack_bonus matches 1.. run scoreboard players set #100 sd_const 100
execute if score @s sd_attack_bonus matches 1.. run scoreboard players operation #damage sd_temp /= #100 sd_const

# 【重构】统一的暴击计算系统 - 冷却期间不触发暴击
execute if score #cooldown_penalty sd_temp matches 0 run function stardew:combat/calculate_crit
execute if score #cooldown_penalty sd_temp matches 0 run function stardew:combat/apply_crit_damage
execute if score #cooldown_penalty sd_temp matches 0 if score #is_critical sd_temp matches 1 run function stardew:combat/apply_critical

# 初始化射线距离
scoreboard players set #raycast_distance sd_temp 0

# 找到被攻击的实体并造成伤害
execute anchored eyes positioned ^ ^ ^0.5 run function stardew:combat/raycast_attack

# 音效反馈
playsound minecraft:entity.player.attack.sweep player @a ~ ~ ~ 0.8 1

# 武器耐久度减少（暂时注释掉，可能有问题）
# item modify entity @s weapon.mainhand stardew:weapon/damage_weapon

# 重置advancement
advancement revoke @s only stardew:combat/player_hurt_entity
