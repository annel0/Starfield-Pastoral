# 宏：种植系统品质判定（基于原版Stardew Valley公式）
# 参数: {id: "crops/spring/tomato"}
# 
# 品质计算公式（原版）：
# - 银星概率 = (FarmingLevel + FertilizerBonus) / 50 = (Level + Bonus) * 2%
# - 金星概率 = (FarmingLevel + FertilizerBonus) / 100 = (Level + Bonus) * 1%
# - 钻石星概率 = (FarmingLevel + FertilizerBonus) / 200 = (Level + Bonus) * 0.5%
#
# FertilizerBonus:
# - 基础品质肥 (level 1): +1
# - 高级品质肥 (level 2): +2
# - 顶级品质肥 (level 3): +3

# 1. 获取农业等级和肥料加成（从附近玩家获取等级）
execute store result score @s sd_temp_quality run scoreboard players get @p[distance=..6] sd_farming_lvl

# 2. 计算肥料品质加成（只有品质肥料type=1才加成）
execute if score @s sd_fertilizer_type matches 1 if score @s sd_fertilizer_level matches 1 run scoreboard players add @s sd_temp_quality 1
execute if score @s sd_fertilizer_type matches 1 if score @s sd_fertilizer_level matches 2 run scoreboard players add @s sd_temp_quality 2
execute if score @s sd_fertilizer_type matches 1 if score @s sd_fertilizer_level matches 3 run scoreboard players add @s sd_temp_quality 3

# 3. 生成随机数 (1-1000，用于精确概率计算)
execute store result score @s sd_rng run random value 1..1000

# 4. 计算各品质阈值
# 银星阈值 = (Level + Bonus) * 20 (即 2% * 1000)
# 金星阈值 = (Level + Bonus) * 10 (即 1% * 1000)
# 钻石星阈值 = (Level + Bonus) * 5 (即 0.5% * 1000)
scoreboard players operation @s sd_temp_silver = @s sd_temp_quality
scoreboard players set #20 sd_const 20
scoreboard players operation @s sd_temp_silver *= #20 sd_const

scoreboard players operation @s sd_temp_gold = @s sd_temp_quality
scoreboard players set #10 sd_const 10
scoreboard players operation @s sd_temp_gold *= #10 sd_const

scoreboard players operation @s sd_temp_diamond = @s sd_temp_quality
scoreboard players set #5 sd_const 5
scoreboard players operation @s sd_temp_diamond *= #5 sd_const

# 5. 品质判定（从高到低）
# 钻石星：随机数 <= 钻石阈值
$execute if score @s sd_rng <= @s sd_temp_diamond run return run function stardew:farming/api/spawn_item {id:"$(id)", quality:"diamond"}

# 金星：随机数 <= 金星阈值
$execute if score @s sd_rng <= @s sd_temp_gold run return run function stardew:farming/api/spawn_item {id:"$(id)", quality:"gold"}

# 银星：随机数 <= 银星阈值
$execute if score @s sd_rng <= @s sd_temp_silver run return run function stardew:farming/api/spawn_item {id:"$(id)", quality:"silver"}

# 普通品质（兜底）
$function stardew:farming/api/spawn_item {id:"$(id)", quality:"base"}