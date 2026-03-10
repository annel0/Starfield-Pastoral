# stardew:monsters/spawn/types/shadow.mcfunction
# 生成暗影兽（根据层数决定属性）

# 获取当前层数
execute store result score #floor sd_temp run scoreboard players get @p[distance=..50] sd_mine_floor

# 暗影兽: 高血量，超高攻击
scoreboard players set #base_hp_multiplier sd_temp 3
scoreboard players set #base_atk_multiplier sd_temp 4
function stardew:monsters/spawn/calculate_stats

# 根据层数设置loot_table
execute if score #floor sd_temp matches ..80 run data modify storage stardew:temp loot_table set value "stardew:monsters/wither_skeleton"
execute if score #floor sd_temp matches 81..100 run data modify storage stardew:temp loot_table set value "stardew:monsters/wither_skeleton_deep"

# 使用宏生成暗影兽
function stardew:monsters/spawn/types/summon_shadow with storage stardew:temp
