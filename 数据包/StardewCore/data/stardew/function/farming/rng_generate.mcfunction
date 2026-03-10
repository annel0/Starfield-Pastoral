# data/stardew/functions/farming/rng_generate.mcfunction
# 生成 1 到 100 之间的随机数，存入 Global sd_rng

# 1. 核心随机数生成 (使用 Area Effect Cloud)
# 生成一个临时的云，使用其 UUID 随机性
summon area_effect_cloud ~ ~ ~ {Duration:1,Tags:["rng_entity"]}

# 2. 将云的 UUID 低位存入 sd_rng
execute store result score Global sd_rng run data get entity @e[type=area_effect_cloud,tag=rng_entity,limit=1] UUID[0]

# 3. 取绝对值 (UUID 可能是负数)
scoreboard players operation Global sd_rng *= Global sd_const

# 4. 限制范围 1-100
scoreboard players set #MAX_RNG sd_config 100
scoreboard players operation Global sd_rng %= #MAX_RNG sd_config
scoreboard players add Global sd_rng 1

# 5. 清理实体
kill @e[type=area_effect_cloud,tag=rng_entity]