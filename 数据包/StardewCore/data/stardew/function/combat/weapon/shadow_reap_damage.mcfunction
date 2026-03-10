# 暗影收割伤害计算
# 如果目标HP < 30%，伤害翻倍（斩杀效果）

# Check if target HP is below 30%
scoreboard players operation #target_hp sd_temp = @s sd_monster_hp
scoreboard players operation #target_max_hp sd_temp = @s sd_monster_max_hp
scoreboard players set #30 sd_const 30
scoreboard players operation #target_max_hp sd_temp *= #30 sd_const
scoreboard players operation #target_max_hp sd_temp /= #100 sd_const

# If HP < 30%, double the damage (execute effect)
execute if score #target_hp sd_temp < #target_max_hp sd_temp run scoreboard players operation #damage sd_temp *= #2 sd_const
execute if score #target_hp sd_temp < #target_max_hp sd_temp run particle minecraft:soul_fire_flame ~ ~1 ~ 0.3 0.5 0.3 0.1 30 force
execute if score #target_hp sd_temp < #target_max_hp sd_temp run playsound minecraft:entity.wither.death player @a ~ ~ ~ 0.5 2
execute if score #target_hp sd_temp < #target_max_hp sd_temp run tellraw @a[distance=..10] [{"text":"💀 ","color":"dark_red","bold":true},{"text":"斩杀！","color":"red","bold":true}]

# Apply damage
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp

# 【DPS统计】记录技能伤害
function stardew:combat/record_skill_damage

# 伤害数字显示
execute store result storage stardew:temp damage int 1 run scoreboard players get #damage sd_temp
data modify storage stardew:temp icon set value "💀"
data modify storage stardew:temp color set value "#8B008B"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp

# Damage indicator
damage @s 0 minecraft:generic by @p

# Visual effects
particle minecraft:damage_indicator ~ ~1 ~ 0.3 0.5 0.3 0.3 5 force
particle minecraft:smoke ~ ~1 ~ 0.2 0.3 0.2 0.05 10 force

# Sound effect
playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 1 0.8
