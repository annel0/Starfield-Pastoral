# stardew:monsters/spawn/types/skeleton.mcfunction
# 生成骷髅（根据层数决定属性）

# 获取当前层数
execute store result score #floor sd_temp run scoreboard players get @p[distance=..50] sd_mine_floor

# 骷髅: 高血量，高攻击
scoreboard players set #base_hp_multiplier sd_temp 3
scoreboard players set #base_atk_multiplier sd_temp 3
function stardew:monsters/spawn/calculate_stats

# 根据层数设置loot table
execute if score #floor sd_temp matches ..80 run data modify storage stardew:temp loot_table set value "stardew:monsters/skeleton"
execute if score #floor sd_temp matches 81..100 run data modify storage stardew:temp loot_table set value "stardew:monsters/skeleton_deep"

# 使用宏生成骷髅
# 根据层数决定是普通骷髅还是流浪者
execute if score #floor sd_temp matches ..80 run function stardew:monsters/spawn/types/summon_skeleton with storage stardew:temp
execute if score #floor sd_temp matches 81..100 run function stardew:monsters/spawn/types/summon_stray with storage stardew:temp
