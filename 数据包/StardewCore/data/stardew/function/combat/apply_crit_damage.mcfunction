# ================================================================
# 统一的暴击伤害应用系统
# ================================================================
# 输入:
#   - @s = 攻击者（玩家）
#   - #damage sd_temp = 基础伤害
#   - #is_critical sd_temp = 是否暴击 (1=暴击, 0=不暴击)
# 输出:
#   - #damage sd_temp = 应用暴击后的伤害
# ================================================================

# 只有在暴击时才应用
execute unless score #is_critical sd_temp matches 1 run return 0

# 检查是否有暴击涌动状态（4倍暴击）
execute if entity @s[tag=sd_crit_surge_active] run scoreboard players operation #damage sd_temp *= #4 sd_const
execute if entity @s[tag=sd_crit_surge_active] run return 0

# 检查是否有特殊暴击倍率（从武器读取）
execute store result score #crit_multiplier sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_crit_multiplier 100
execute if score #crit_multiplier sd_temp matches 1.. run scoreboard players operation #damage sd_temp *= #crit_multiplier sd_temp
execute if score #crit_multiplier sd_temp matches 1.. run scoreboard players set #100 sd_const 100
execute if score #crit_multiplier sd_temp matches 1.. run scoreboard players operation #damage sd_temp /= #100 sd_const
execute if score #crit_multiplier sd_temp matches 1.. run return 0

# 默认3倍暴击伤害
scoreboard players operation #damage sd_temp *= #3 sd_const
