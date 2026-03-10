# stardew:monsters/spawn/types/bat.mcfunction
# 生成蝙蝠（根据层数决定属性）

# 获取当前层数
execute store result score #floor sd_temp run scoreboard players get @p[distance=..50] sd_mine_floor

# 蝙蝠: 低血量，中等攻击
scoreboard players set #base_hp_multiplier sd_temp 1
scoreboard players set #base_atk_multiplier sd_temp 2
function stardew:monsters/spawn/calculate_stats

# 根据层数设置loot table
execute if score #floor sd_temp matches ..40 run data modify storage stardew:temp loot_table set value "stardew:monsters/bat"
execute if score #floor sd_temp matches 41..80 run data modify storage stardew:temp loot_table set value "stardew:monsters/bat_frost"
execute if score #floor sd_temp matches 81..100 run data modify storage stardew:temp loot_table set value "stardew:monsters/bat_lava"

# 使用宏生成蝙蝠
function stardew:monsters/spawn/types/summon_bat with storage stardew:temp
