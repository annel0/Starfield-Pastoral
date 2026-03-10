# 处理中毒效果
# 每秒造成固定伤害（每20 ticks一次）

# 每20 ticks（1秒）造成一次伤害
scoreboard players operation #poison_check sd_temp = @s sd_poison_timer
scoreboard players operation #poison_check sd_temp %= #20 sd_const

# 如果是20的倍数，造成中毒伤害
execute if score #poison_check sd_temp matches 0 run scoreboard players operation @s sd_monster_hp -= @s sd_poison_damage

# 中毒粒子效果（每3 tick一次）
scoreboard players operation #particle_check sd_temp = @s sd_poison_timer
scoreboard players set #3 sd_const 3
scoreboard players operation #particle_check sd_temp %= #3 sd_const
execute if score #particle_check sd_temp matches 0 run particle minecraft:effect ~ ~0.5 ~ 0.2 0.3 0.2 0.02 3 force
execute if score #particle_check sd_temp matches 0 run particle minecraft:sneeze ~ ~0.5 ~ 0.2 0.3 0.2 0.01 2 force
execute if score #particle_check sd_temp matches 0 run particle minecraft:item_slime ~ ~0.5 ~ 0.2 0.3 0.2 0 1 force

# 中毒音效（每40 tick一次）
scoreboard players operation #sound_check sd_temp = @s sd_poison_timer
scoreboard players set #40 sd_const 40
scoreboard players operation #sound_check sd_temp %= #40 sd_const
execute if score #sound_check sd_temp matches 0 run playsound minecraft:entity.spider.ambient hostile @a ~ ~ ~ 0.3 1.5

# 减少中毒计时器
scoreboard players remove @s sd_poison_timer 1

# 中毒结束
execute if score @s sd_poison_timer matches 0 run scoreboard players set @s sd_poison_damage 0
execute if score @s sd_poison_timer matches 0 run tag @s remove sd_poisoned
execute if score @s sd_poison_timer matches 0 run particle minecraft:happy_villager ~ ~1 ~ 0.3 0.5 0.3 0.05 10 force
