# stardew:monsters/spawn/types/silverfish.mcfunction
# 生成蠹虫（根据层数决定属性）

# 获取当前层数
execute store result score #floor sd_temp run scoreboard players get @p[distance=..50] sd_mine_floor

# 蠹虫: 低血量，低攻击
scoreboard players set #base_hp_multiplier sd_temp 1
scoreboard players set #base_atk_multiplier sd_temp 1
function stardew:monsters/spawn/calculate_stats

# 根据层数设置loot table
execute if score #floor sd_temp matches ..80 run data modify storage stardew:temp loot_table set value "stardew:monsters/silverfish"
execute if score #floor sd_temp matches 81..100 run data modify storage stardew:temp loot_table set value "stardew:monsters/silverfish_deep"

# 使用宏生成蠹虫
function stardew:monsters/spawn/types/summon_silverfish with storage stardew:temp
