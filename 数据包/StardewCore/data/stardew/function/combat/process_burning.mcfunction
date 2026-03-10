# 处理自定义燃烧效果
# 每秒造成固定伤害（每20 ticks一次）

# 每20 ticks（1秒）造成一次伤害
scoreboard players operation #burn_check sd_temp = @s sd_burning_timer
scoreboard players operation #burn_check sd_temp %= #20 sd_const

# 如果是20的倍数，造成燃烧伤害
execute if score #burn_check sd_temp matches 0 run scoreboard players operation @s sd_monster_hp -= @s sd_burning_damage

# 燃烧粒子效果（每3 tick一次）
scoreboard players operation #particle_check sd_temp = @s sd_burning_timer
scoreboard players set #3 sd_const 3
scoreboard players operation #particle_check sd_temp %= #3 sd_const
execute if score #particle_check sd_temp matches 0 run particle minecraft:flame ~ ~0.5 ~ 0.2 0.3 0.2 0.02 3 force
execute if score #particle_check sd_temp matches 0 run particle minecraft:smoke ~ ~0.5 ~ 0.2 0.3 0.2 0.01 2 force

# 燃烧音效（每40 tick一次）
scoreboard players operation #sound_check sd_temp = @s sd_burning_timer
scoreboard players set #40 sd_const 40
scoreboard players operation #sound_check sd_temp %= #40 sd_const
execute if score #sound_check sd_temp matches 0 run playsound minecraft:block.fire.ambient hostile @a ~ ~ ~ 0.5 1.5

# 减少燃烧计时器
scoreboard players remove @s sd_burning_timer 1

# 燃烧结束
execute if score @s sd_burning_timer matches 0 run scoreboard players set @s sd_burning_damage 0
execute if score @s sd_burning_timer matches 0 run particle minecraft:smoke ~ ~1 ~ 0.3 0.5 0.3 0.05 10 force
