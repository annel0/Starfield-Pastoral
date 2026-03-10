# 震地重击 - 伤害计算
# 伤害倍率已在主函数根据等级计算好，存储在 #damage sd_temp 中

# 【重构】统一的暴击计算系统
execute store result score #weapon_crit sd_temp run data get entity @p[tag=sd_using_ground_slam] SelectedItem.components."minecraft:custom_data".weapon_crit_chance 100
execute as @p[tag=sd_using_ground_slam] run function stardew:combat/calculate_crit
execute as @p[tag=sd_using_ground_slam] run function stardew:combat/apply_crit_damage

# 如果是怪物，扣除 sd_monster_hp
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp
execute if entity @s[tag=sd_monster] run scoreboard players operation #monster_hp sd_temp = @s sd_monster_hp
execute if entity @s[tag=sd_monster] run scoreboard players operation #monster_max_hp sd_temp = @s sd_monster_max_hp

# 【DPS统计】记录技能伤害
function stardew:combat/record_skill_damage

# 伤害数字显示
execute store result storage stardew:temp damage int 1 run scoreboard players get #damage sd_temp
data modify storage stardew:temp icon set value "🔨"
data modify storage stardew:temp color set value "gold"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp

# 粒子效果和音效（区分暴击）- 减少粒子数量
execute if score #is_critical sd_temp matches 1 run particle minecraft:crit ~ ~1 ~ 0.5 0.8 0.5 0.2 40 force
execute if score #is_critical sd_temp matches 1 run particle minecraft:enchanted_hit ~ ~1 ~ 0.4 0.6 0.4 0.15 30 force
execute unless score #is_critical sd_temp matches 1 run particle minecraft:damage_indicator ~ ~1 ~ 0.3 0.5 0.3 0.1 5 force
execute if score #skill_level sd_temp matches ..2 run particle minecraft:block{block_state:"minecraft:dirt"} ~ ~0.5 ~ 0.5 0.3 0.5 0.1 20 force
execute if score #skill_level sd_temp matches 3.. run particle minecraft:block{block_state:"minecraft:stone"} ~ ~0.5 ~ 0.5 0.3 0.5 0.1 30 force
execute if score #is_critical sd_temp matches 1 run playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 1.5 0.8
execute unless score #is_critical sd_temp matches 1 run playsound minecraft:entity.player.attack.strong player @a ~ ~ ~ 1 0.8
playsound minecraft:entity.generic.hurt hostile @a ~ ~ ~ 1 1

# 击退效果（根据等级增强：0.5/0.6/0.7/0.8/0.9）
execute if score #skill_level sd_temp matches 1 run data modify entity @s Motion[1] set value 0.5
execute if score #skill_level sd_temp matches 2 run data modify entity @s Motion[1] set value 0.6
execute if score #skill_level sd_temp matches 3 run data modify entity @s Motion[1] set value 0.7
execute if score #skill_level sd_temp matches 4 run data modify entity @s Motion[1] set value 0.8
execute if score #skill_level sd_temp matches 5.. run data modify entity @s Motion[1] set value 0.9
