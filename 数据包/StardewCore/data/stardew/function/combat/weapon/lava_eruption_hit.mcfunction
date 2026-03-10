# 熔岩爆发 - 单个敌人的伤害和燃烧

# 应用伤害
scoreboard players operation @s sd_monster_hp -= #damage sd_temp

# 【DPS统计】记录技能伤害
function stardew:combat/record_skill_damage

# 显示伤害数字
execute store result storage stardew:temp damage int 1 run scoreboard players get #damage sd_temp
data modify storage stardew:temp icon set value "🌋"
data modify storage stardew:temp color set value "#FF4500"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp

# 施加5秒燃烧（100 ticks）
tag @s add sd_burning
scoreboard players set @s sd_burning_damage 10
scoreboard players set @s sd_burning_timer 100

# 火柱从脚下喷发的视觉效果
particle minecraft:lava ~ ~0.1 ~ 0.3 0 0.3 0 10 force
particle minecraft:flame ~ ~ ~ 0.3 1.5 0.3 0.15 50 force
particle minecraft:soul_fire_flame ~ ~ ~ 0.2 1 0.2 0.1 20 force
particle minecraft:large_smoke ~ ~1 ~ 0.3 0.5 0.3 0.05 15 force
particle minecraft:explosion ~ ~0.5 ~ 0 0 0 0 1 force

# 火柱音效
playsound minecraft:block.lava.pop hostile @a ~ ~ ~ 1 0.8
playsound minecraft:entity.blaze.hurt hostile @a ~ ~ ~ 0.8 1.5

# 击退效果（向上）
data merge entity @s {Motion:[0.0,0.5,0.0]}
