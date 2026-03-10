# stardew:monsters/spawn/types/golem.mcfunction
# 生成石魔（根据层数决定属性）

# 获取当前层数
execute store result score #floor sd_temp run scoreboard players get @p[distance=..50] sd_mine_floor

# 石魔: 超高血量，高攻击
scoreboard players set #base_hp_multiplier sd_temp 4
scoreboard players set #base_atk_multiplier sd_temp 3
function stardew:monsters/spawn/calculate_stats

# 使用宏生成石魔
function stardew:monsters/spawn/types/summon_golem with storage stardew:temp
