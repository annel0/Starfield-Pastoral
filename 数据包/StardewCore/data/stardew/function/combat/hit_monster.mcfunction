# 命中怪物（怪物已经在 raycast# 【龙牙狂怒AOE】对周围3格内的其他怪物造成相同伤害
execute if entity @p[tag=sd_dragon_fury] as @e[tag=sd_hit_target,limit=1] at @s run tag @e[tag=sd_monster,distance=0.1..3] add sd_fury_aoe_target
execute if entity @p[tag=sd_dragon_fury] as @e[tag=sd_fury_aoe_target] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp
execute if entity @p[tag=sd_dragon_fury] as @e[tag=sd_fury_aoe_target] at @s run damage @s 0 minecraft:generic by @p
execute if entity @p[tag=sd_dragon_fury] as @e[tag=sd_fury_aoe_target] at @s run particle minecraft:dragon_breath ~ ~1 ~ 0.3 0.5 0.3 0.1 15 force
execute if entity @p[tag=sd_dragon_fury] as @e[tag=sd_fury_aoe_target] at @s run particle minecraft:sweep_attack ~ ~1 ~ 0 0 0 0 1 force
execute if entity @p[tag=sd_dragon_fury] run tag @e[tag=sd_fury_aoe_target] remove sd_fury_aoe_target

# 【泰坦之怒AOE+回血】对周围怪物造成相同伤害，并回复生命
execute if entity @p[tag=sd_titan_wrath] as @e[tag=sd_hit_target,limit=1] at @s if data entity @p[tag=sd_titan_wrath] SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} run tag @e[tag=sd_monster,distance=0.1..2.5] add sd_wrath_aoe_target
execute if entity @p[tag=sd_titan_wrath] as @e[tag=sd_hit_target,limit=1] at @s if data entity @p[tag=sd_titan_wrath] SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} run tag @e[tag=sd_monster,distance=0.1..3] add sd_wrath_aoe_target
execute if entity @p[tag=sd_titan_wrath] as @e[tag=sd_wrath_aoe_target] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp
execute if entity @p[tag=sd_titan_wrath] as @e[tag=sd_wrath_aoe_target] at @s run damage @s 0 minecraft:generic by @p[tag=sd_titan_wrath]
execute if entity @p[tag=sd_titan_wrath] as @e[tag=sd_wrath_aoe_target] at @s run particle minecraft:flame ~ ~1 ~ 0.3 0.5 0.3 0.1 15 force
execute if entity @p[tag=sd_titan_wrath] as @e[tag=sd_wrath_aoe_target] at @s run particle minecraft:lava ~ ~0.5 ~ 0.2 0.2 0.2 0.05 5 force
execute if entity @p[tag=sd_titan_wrath] as @e[tag=sd_wrath_aoe_target] at @s run particle minecraft:sweep_attack ~ ~1 ~ 0 0 0 0 1 force
execute if entity @p[tag=sd_titan_wrath] as @e[tag=sd_wrath_aoe_target] at @s run playsound minecraft:entity.generic.hurt hostile @a ~ ~ ~ 1 1
execute if entity @p[tag=sd_titan_wrath] as @e[tag=sd_wrath_aoe_target] at @s run particle minecraft:damage_indicator ~ ~1 ~ 0.3 0.5 0.3 0.1 5
# 显示伤害数字（AOE目标）
execute if entity @p[tag=sd_titan_wrath] as @e[tag=sd_wrath_aoe_target] at @s run function stardew:combat/damage_display/spawn_normal_aoe with storage stardew:temp
execute if entity @p[tag=sd_titan_wrath] run tag @e[tag=sd_wrath_aoe_target] remove sd_wrath_aoe_target

# 【泰坦之怒回血】击中敌人回复生命（银河+4 HP，无限+6 HP）
execute if entity @p[tag=sd_titan_wrath] if data entity @p[tag=sd_titan_wrath] SelectedItem.components."minecraft:custom_data"{weapon_tier:"galaxy"} as @p[tag=sd_titan_wrath] run scoreboard players add @s sd_health 4
execute if entity @p[tag=sd_titan_wrath] if data entity @p[tag=sd_titan_wrath] SelectedItem.components."minecraft:custom_data"{weapon_tier:"infinity"} as @p[tag=sd_titan_wrath] run scoreboard players add @s sd_health 6
execute if entity @p[tag=sd_titan_wrath] as @p[tag=sd_titan_wrath] if score @s sd_health > @s sd_max_health run scoreboard players operation @s sd_health = @s sd_max_health
execute if entity @p[tag=sd_titan_wrath] as @p[tag=sd_titan_wrath] at @s run particle minecraft:heart ~ ~2 ~ 0.3 0.3 0.3 0.1 3 force
execute if entity @p[tag=sd_titan_wrath] as @p[tag=sd_titan_wrath] at @s run playsound minecraft:entity.player.levelup player @s ~ ~ ~ 0.3 2

# 清除标记（在使用完所有数据后再清除）被标记为 sd_hit_target）

# 检查是否找到目标
execute unless entity @e[tag=sd_hit_target] run return 0

# 【全局DPS统计】记录玩家造成的伤害 (此时执行者是玩家)
scoreboard players operation @p sd_player_total_dmg += #damage sd_temp

# 检查怪物是否有血量数据（防御性检查）
execute as @e[tag=sd_hit_target,limit=1] unless score @s sd_monster_hp matches -2147483648..2147483647 run scoreboard players set @s sd_monster_hp 20
execute as @e[tag=sd_hit_target,limit=1] unless score @s sd_monster_max_hp matches -2147483648..2147483647 run scoreboard players set @s sd_monster_max_hp 20

# 扣除怪物血量
execute as @e[tag=sd_hit_target,limit=1] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp

# 将怪物血量复制到临时变量（使用 operation，更可靠）
execute as @e[tag=sd_hit_target,limit=1] run scoreboard players operation #monster_hp sd_temp = @s sd_monster_hp
execute as @e[tag=sd_hit_target,limit=1] run scoreboard players operation #monster_max_hp sd_temp = @s sd_monster_max_hp

# 伤害显示（粒子和音效）- 减少粒子数量
execute as @e[tag=sd_hit_target,limit=1] at @s run particle minecraft:damage_indicator ~ ~1 ~ 0.3 0.5 0.3 0.1 5
execute as @e[tag=sd_hit_target,limit=1] at @s run playsound minecraft:entity.generic.hurt hostile @a ~ ~ ~ 1 1

# 【新】伤害数字显示 - 根据伤害类型召唤不同样式的text_display
# 将伤害值存到storage用于宏函数
execute store result storage stardew:temp damage int 1 run scoreboard players get #damage sd_temp

# 排除使用技能的玩家（技能有自己的伤害显示）
execute if entity @p[tag=sd_using_shadow_reap] run return 0
execute if entity @p[tag=sd_using_poison_strike] run return 0

# 冷却惩罚（灰色1）
execute if score #cooldown_penalty sd_temp matches 1 as @e[tag=sd_hit_target,limit=1] at @s run function stardew:combat/damage_display/spawn_cooldown
# 暴击（红色+emoji+感叹号）
execute if score #cooldown_penalty sd_temp matches 0 if score #is_critical sd_temp matches 1 as @e[tag=sd_hit_target,limit=1] at @s run function stardew:combat/damage_display/spawn_critical with storage stardew:temp
# 普通伤害（黄色）
execute if score #cooldown_penalty sd_temp matches 0 if score #is_critical sd_temp matches 0 as @e[tag=sd_hit_target,limit=1] at @s run function stardew:combat/damage_display/spawn_normal with storage stardew:temp

# 【龙牙狂怒AOE】对周围3格内的其他怪物造成相同伤害
execute if entity @p[tag=sd_dragon_fury] as @e[tag=sd_hit_target,limit=1] at @s run tag @e[tag=sd_monster,distance=0.1..3] add sd_fury_aoe_target
execute if entity @p[tag=sd_dragon_fury] as @e[tag=sd_fury_aoe_target] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp
execute if entity @p[tag=sd_dragon_fury] as @e[tag=sd_fury_aoe_target] at @s run damage @s 0 minecraft:generic by @p
execute if entity @p[tag=sd_dragon_fury] as @e[tag=sd_fury_aoe_target] at @s run particle minecraft:dragon_breath ~ ~1 ~ 0.3 0.5 0.3 0.1 15 force
execute if entity @p[tag=sd_dragon_fury] as @e[tag=sd_fury_aoe_target] at @s run particle minecraft:sweep_attack ~ ~1 ~ 0 0 0 0 1 force
execute if entity @p[tag=sd_dragon_fury] run tag @e[tag=sd_fury_aoe_target] remove sd_fury_aoe_target

# 清除标记（在使用完所有数据后再清除）
tag @e[tag=sd_hit_target] remove sd_hit_target

# 重置射线距离
scoreboard players reset #raycast_distance sd_temp
