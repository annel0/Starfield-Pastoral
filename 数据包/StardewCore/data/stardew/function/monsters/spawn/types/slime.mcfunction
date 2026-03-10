# stardew:monsters/spawn/types/slime.mcfunction
# 生成史莱姆（根据层数决定属性）

# 获取当前层数
execute store result score #floor sd_temp run scoreboard players get @p[distance=..50] sd_mine_floor

# 史莱姆: 中等血量，低攻击
scoreboard players set #base_hp_multiplier sd_temp 2
scoreboard players set #base_atk_multiplier sd_temp 1
function stardew:monsters/spawn/calculate_stats

# 根据层数设置Size和标签（调整：前期降低分裂数量，避免太难）
execute if score #floor sd_temp matches ..40 run data modify storage stardew:temp monster_size set value 0
execute if score #floor sd_temp matches 41..80 run data modify storage stardew:temp monster_size set value 1
execute if score #floor sd_temp matches 81..100 run data modify storage stardew:temp monster_size set value 2

execute if score #floor sd_temp matches ..40 run data modify storage stardew:temp monster_tier set value "sd_tier_1"
execute if score #floor sd_temp matches 41..80 run data modify storage stardew:temp monster_tier set value "sd_tier_2"
execute if score #floor sd_temp matches 81..100 run data modify storage stardew:temp monster_tier set value "sd_tier_3"

# 根据Size设置对应的DeathLootTable
execute if score #floor sd_temp matches ..40 run data modify storage stardew:temp loot_table set value "stardew:monsters/slime_small"
execute if score #floor sd_temp matches 41..80 run data modify storage stardew:temp loot_table set value "stardew:monsters/slime_medium"
execute if score #floor sd_temp matches 81..100 run data modify storage stardew:temp loot_table set value "stardew:monsters/slime_large"

# 使用宏生成史莱姆
function stardew:monsters/spawn/types/summon_slime with storage stardew:temp

