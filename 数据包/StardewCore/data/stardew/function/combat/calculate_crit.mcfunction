# ================================================================
# 统一的暴击计算系统
# ================================================================
# 输入:
#   - @s = 攻击者（玩家）
#   - #weapon_crit sd_temp = 武器暴击率（已经是百分比格式，如 6 = 6%）
# 输出:
#   - #is_critical sd_temp = 是否暴击 (1=暴击, 0=不暴击)
#   - #base_crit sd_temp = 最终暴击率（用于调试）
# ================================================================

# 重置暴击状态
scoreboard players set #is_critical sd_temp 0

# 基础暴击率 2%
scoreboard players set #base_crit sd_temp 2

# 武器暴击率（已经通过参数传入 #weapon_crit）
scoreboard players operation #base_crit sd_temp += #weapon_crit sd_temp

# 靴子暴击率加成（如牛仔靴+3%）
execute if score @s sd_equip_boots matches 1.. store result score #boots_crit sd_temp run data get storage stardew:equipment boots.effects.crit_chance 100
execute if score #boots_crit sd_temp matches 1.. run scoreboard players operation #base_crit sd_temp += #boots_crit sd_temp

# 戒指暴击率加成（如海蓝宝石戒指+10%）
execute if score @s sd_crit_chance matches 1.. run scoreboard players operation #base_crit sd_temp += @s sd_crit_chance

# 【关键】银河觉醒/无限觉醒暴击率加成（独立变量，不会被戒指扫描覆盖）
execute if score @s sd_awakening_crit_bonus matches 1.. run scoreboard players operation #base_crit sd_temp += @s sd_awakening_crit_bonus

# 精准打击暴击率加成（如果激活）
execute if entity @s[tag=sd_precision_active] store result score #precision_bonus sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_precision_crit_bonus 100
execute if entity @s[tag=sd_precision_active] unless score #precision_bonus sd_temp matches 1.. run scoreboard players set #precision_bonus sd_temp 25
execute if entity @s[tag=sd_precision_active] run scoreboard players operation #base_crit sd_temp += #precision_bonus sd_temp

# 暴击涌动额外加成（如果激活）
execute if entity @s[tag=sd_crit_surge_active] run scoreboard players add #base_crit sd_temp 50

# 暴击判定
execute store result score #crit_roll sd_temp run random value 1..100
execute if score #crit_roll sd_temp <= #base_crit sd_temp run scoreboard players set #is_critical sd_temp 1

# 调试信息（可选，正式版可以注释掉）
# execute if score #is_critical sd_temp matches 1 run tellraw @s [{"text":"[暴击] ","color":"gold"},{"text":"暴击率: ","color":"gray"},{"score":{"name":"#base_crit","objective":"sd_temp"},"color":"yellow"},{"text":"% | 骰子: ","color":"gray"},{"score":{"name":"#crit_roll","objective":"sd_temp"},"color":"white"}]
