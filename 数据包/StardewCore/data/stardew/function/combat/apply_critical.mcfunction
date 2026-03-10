# 应用暴击

# 计算暴击倍率: 基础150% (1.5倍) + 戒指暴击伤害加成
# sd_crit_power: 10 = +10% 暴击伤害,即总暴击倍率变为160%
scoreboard players set #crit_multiplier sd_temp 150
execute if score @s sd_crit_power matches 1.. run scoreboard players operation #crit_multiplier sd_temp += @s sd_crit_power

# 应用暴击倍率: damage = damage * multiplier / 100
scoreboard players operation #damage sd_temp *= #crit_multiplier sd_temp
scoreboard players set #100 sd_const 100
scoreboard players operation #damage sd_temp /= #100 sd_const

# 标记暴击
scoreboard players set #is_critical sd_temp 1

# 暴击音效
playsound minecraft:entity.player.attack.crit player @a ~ ~ ~ 1 1.2
