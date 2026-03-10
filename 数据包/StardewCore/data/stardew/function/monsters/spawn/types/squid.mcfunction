# stardew:monsters/spawn/types/squid.mcfunction
# 生成乌贼小子（根据层数决定属性）

# 获取当前层数
execute store result score #floor sd_temp run scoreboard players get @p[distance=..50] sd_mine_floor

# 乌贼小子: 中等血量，高攻击
scoreboard players set #base_hp_multiplier sd_temp 2
scoreboard players set #base_atk_multiplier sd_temp 3
function stardew:monsters/spawn/calculate_stats

# 根据层数设置loot_table
execute if score #floor sd_temp matches ..80 run data modify storage stardew:temp loot_table set value "stardew:monsters/vex"
execute if score #floor sd_temp matches 81..100 run data modify storage stardew:temp loot_table set value "stardew:monsters/vex_deep"

# 使用宏生成乌贼小子
function stardew:monsters/spawn/types/summon_squid with storage stardew:temp
