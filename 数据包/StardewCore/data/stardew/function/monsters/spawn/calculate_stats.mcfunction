# stardew:monsters/spawn/calculate_stats.mcfunction
# 根据层数和怪物基础属性计算最终属性
# 输入: #floor sd_temp (层数), #base_hp_multiplier sd_temp, #base_atk_multiplier sd_temp
# 输出: #final_hp sd_temp, #final_atk sd_temp

# 新的成长公式（大幅提升）：
# 血量 = base_hp + floor * growth_hp
# 攻击力 = base_atk + floor * growth_atk

# 根据怪物类型的倍率计算基础值
# base_hp_multiplier: 1=低血量(40), 2=中血量(50), 3=高血量(60), 4=超高血量(70)
# 血量成长：每层+5血（100层时+500血）

execute if score #base_hp_multiplier sd_temp matches 1 run scoreboard players set #base_hp sd_temp 40
execute if score #base_hp_multiplier sd_temp matches 2 run scoreboard players set #base_hp sd_temp 50
execute if score #base_hp_multiplier sd_temp matches 3 run scoreboard players set #base_hp sd_temp 60
execute if score #base_hp_multiplier sd_temp matches 4 run scoreboard players set #base_hp sd_temp 70

# 血量 = base_hp + floor * 5
scoreboard players operation #final_hp sd_temp = #floor sd_temp
scoreboard players set #5 sd_temp 5
scoreboard players operation #final_hp sd_temp *= #5 sd_temp
scoreboard players operation #final_hp sd_temp += #base_hp sd_temp

# base_atk_multiplier: 1=低攻击(5), 2=中攻击(6), 3=高攻击(8), 4=超高攻击(10)
# 攻击力成长：每层+0.2攻击（100层时+20攻击）

execute if score #base_atk_multiplier sd_temp matches 1 run scoreboard players set #base_atk sd_temp 5
execute if score #base_atk_multiplier sd_temp matches 2 run scoreboard players set #base_atk sd_temp 6
execute if score #base_atk_multiplier sd_temp matches 3 run scoreboard players set #base_atk sd_temp 8
execute if score #base_atk_multiplier sd_temp matches 4 run scoreboard players set #base_atk sd_temp 10

# 攻击力 = base_atk + floor * 0.2 (用 floor * 20 / 100 计算)
scoreboard players operation #atk_bonus sd_temp = #floor sd_temp
scoreboard players set #20 sd_temp 20
scoreboard players operation #atk_bonus sd_temp *= #20 sd_temp
scoreboard players set #100 sd_temp 100
scoreboard players operation #atk_bonus sd_temp /= #100 sd_temp
scoreboard players operation #final_atk sd_temp = #base_atk sd_temp
scoreboard players operation #final_atk sd_temp += #atk_bonus sd_temp

# 存储到storage
execute store result storage stardew:temp monster_hp int 1 run scoreboard players get #final_hp sd_temp
execute store result storage stardew:temp monster_atk int 1 run scoreboard players get #final_atk sd_temp
