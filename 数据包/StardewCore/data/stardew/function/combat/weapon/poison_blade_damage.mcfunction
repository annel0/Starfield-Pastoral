# 剧毒之刃伤害计算
# 对被标记的敌人造成80%武器伤害 + 施加中毒效果
# 伤害已经在主函数计算好，存储在 #damage sd_temp 中

# 如果是怪物，扣除 sd_monster_hp（初始伤害）
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_monster_hp -= #damage sd_temp
execute if entity @s[tag=sd_monster] run scoreboard players operation #monster_hp sd_temp = @s sd_monster_hp
execute if entity @s[tag=sd_monster] run scoreboard players operation #monster_max_hp sd_temp = @s sd_monster_max_hp

# 【DPS统计】记录技能伤害
function stardew:combat/record_skill_damage

# 显示伤害数字
execute store result storage stardew:temp damage int 1 run scoreboard players get #damage sd_temp
data modify storage stardew:temp icon set value "☠"
data modify storage stardew:temp color set value "#228B22"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp

# 施加中毒状态
tag @s add sd_poisoned
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_poison_damage = #poison_damage sd_temp
execute if entity @s[tag=sd_monster] run scoreboard players operation @s sd_poison_timer = #poison_duration sd_temp

# 粒子效果和音效
particle minecraft:damage_indicator ~ ~1 ~ 0.3 0.5 0.3 0.1 15 force
particle minecraft:effect ~ ~1 ~ 0.4 0.6 0.4 0.15 25 force
particle minecraft:item_slime ~ ~1 ~ 0.3 0.5 0.3 0.1 20 force
particle minecraft:sneeze ~ ~1 ~ 0.3 0.5 0.3 0 10 force
playsound minecraft:entity.spider.hurt player @a ~ ~ ~ 1 0.8
playsound minecraft:entity.generic.hurt hostile @a ~ ~ ~ 1.2 1
