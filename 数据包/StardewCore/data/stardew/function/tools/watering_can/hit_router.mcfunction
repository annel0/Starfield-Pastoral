# data/stardew/functions/tools/watering_can/hit_router.mcfunction
# [执行位置: 目标耕地方块]
# [执行者: 玩家]

# 0. 检查冷却时间
execute if score @s sd_water_cd matches 1.. run playsound minecraft:block.note_block.bass player @s ~ ~ ~ 0.5 0.5
execute if score @s sd_water_cd matches 1.. run particle minecraft:angry_villager ~ ~0.5 ~ 0.1 0.1 0.1 0 1 force @a
execute if score @s sd_water_cd matches 1.. run return 0

# 0.5 检查能量（至少需要 2 点能量才能浇水）
execute if score @s sd_energy matches ..1 run function stardew:energy/warn_depleted
execute if score @s sd_energy matches ..1 run return 0

# 1. 获取水壶等级
# 读取 CMD 到 sd_const
execute store result score @s sd_const run data get entity @s SelectedItem.components."minecraft:custom_model_data"

# 2. 检查蓄力时间（仅当潜行时需要蓄力）
# 铜水壶 (301): 不需要蓄力（只能单格）
# 铁水壶 (302): 需要蓄力完成 (sd_charge_ready=1) 或 20 tick (1秒)
# 金水壶 (303): 需要蓄力完成 (sd_charge_ready=1) 或 30 tick (1.5秒)
# 钻石水壶 (304): 需要蓄力完成 (sd_charge_ready=1) 或 40 tick (2秒)

# 检查蓄力是否完成（sd_charge_ready=1 表示已经蓄力完成，无论现在是否还在蓄力）
execute if score @s sd_is_sneaking matches 1 if score @s sd_const matches 302 unless score @s sd_charge_ready matches 1 unless score @s sd_charge_time matches 20.. run return 0
execute if score @s sd_is_sneaking matches 1 if score @s sd_const matches 303 unless score @s sd_charge_ready matches 1 unless score @s sd_charge_time matches 30.. run return 0
execute if score @s sd_is_sneaking matches 1 if score @s sd_const matches 304 unless score @s sd_charge_ready matches 1 unless score @s sd_charge_time matches 40.. run return 0

# 3. 蓄力满了，重置蓄力状态（但不清除slowness，让它自然消失）
scoreboard players set @s sd_charge_time 0
scoreboard players set @s sd_charge_ready 0
scoreboard players set @s sd_special_type 0
scoreboard players set @s sd_special_value 0
scoreboard players set @s sd_special_max 0

# 3.5 初始化浇水格数计数器（用于能量消耗）
scoreboard players set @s sd_temp 0

# 4. 判定逻辑
# 没蹲下 (0) 或 铜水壶 (301) -> 单格
execute if score @s sd_is_sneaking matches 0 run function stardew:tools/watering_can/water_single_block
execute if score @s sd_const matches 301 run function stardew:tools/watering_can/water_single_block

# 5. 潜行 (1) + 高级水壶 -> 范围
# 铁水壶 (302) -> 3x3
execute if score @s sd_is_sneaking matches 1 if score @s sd_const matches 302 run function stardew:tools/watering_can/t2_water
# 金水壶 (303) -> 5x5
execute if score @s sd_is_sneaking matches 1 if score @s sd_const matches 303 run function stardew:tools/watering_can/t3_water
# 钻石水壶 (304) -> 7x7
execute if score @s sd_is_sneaking matches 1 if score @s sd_const matches 304 run function stardew:tools/watering_can/t4_water

# 5.5 计算并消耗能量
# 基础消耗: 每格 2 点能量
# sd_temp 现在存储了浇水的格数
# 工具等级: 301-304 -> 1-4
scoreboard players set #base_cost sd_temp 2
scoreboard players operation #base_cost sd_temp *= @s sd_temp
scoreboard players operation #tool_tier sd_temp = @s sd_const
scoreboard players remove #tool_tier sd_temp 300
function stardew:energy/calculate_tool_cost
function stardew:energy/consume

# 6. 设置冷却时间（1.5秒 = 30 ticks）
scoreboard players set @s sd_water_cd 30
bossbar set stardew:water_cooldown max 30
playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.3 0.5
