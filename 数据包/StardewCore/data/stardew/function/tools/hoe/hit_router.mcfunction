# data/stardew/functions/tools/hoe/hit_router.mcfunction
# [执行位置: 目标泥土方块]
# [执行者: 玩家]

# 0. 检查冷却时间
execute if score @s sd_hoe_cd matches 1.. run playsound minecraft:block.note_block.bass player @s ~ ~ ~ 0.5 0.5
execute if score @s sd_hoe_cd matches 1.. run particle minecraft:angry_villager ~ ~0.5 ~ 0.1 0.1 0.1 0 1 force @a
execute if score @s sd_hoe_cd matches 1.. run return 0

# 0.5 能量检查（最低需要1点能量）
execute if score @s sd_energy matches ..0 run function stardew:energy/warn_depleted
execute if score @s sd_energy matches ..0 run return 0

# 1. 获取锄头等级
# 读取 CMD 到 sd_const
execute store result score @s sd_const run data get entity @s SelectedItem.components."minecraft:custom_model_data"

# 2. 初始化耕地块数计数器
scoreboard players set @s sd_temp 0

# 3. 检查蓄力时间（仅当潜行时需要蓄力）
# 如果不潜行，跳过蓄力检查（但保留已完成的蓄力状态）
# 铜锄 (501): 不需要蓄力（只能单格）
# 铁锄 (502): 需要蓄力完成 (sd_charge_ready=1) 或 20 tick
# 金锄 (503): 需要蓄力完成 (sd_charge_ready=1) 或 30 tick  
# 钻锄 (504): 需要蓄力完成 (sd_charge_ready=1) 或 40 tick

# 检查蓄力是否完成（sd_charge_ready=1 表示已经蓄力完成，无论现在是否还在蓄力）
execute if score @s sd_is_sneaking matches 1 if score @s sd_const matches 502 unless score @s sd_charge_ready matches 1 unless score @s sd_charge_time matches 20.. run return 0
execute if score @s sd_is_sneaking matches 1 if score @s sd_const matches 503 unless score @s sd_charge_ready matches 1 unless score @s sd_charge_time matches 30.. run return 0
execute if score @s sd_is_sneaking matches 1 if score @s sd_const matches 504 unless score @s sd_charge_ready matches 1 unless score @s sd_charge_time matches 40.. run return 0

# 4. 蓄力满了，重置蓄力状态（但不清除slowness，让它自然消失）
scoreboard players set @s sd_charge_time 0
scoreboard players set @s sd_charge_ready 0
scoreboard players set @s sd_special_type 0
scoreboard players set @s sd_special_value 0
scoreboard players set @s sd_special_max 0

# 5. 执行耕地操作（会自动计数到 sd_temp）
# 没蹲下 (0) 或 铜锄头 (501) -> 单格
execute if score @s sd_is_sneaking matches 0 run function stardew:tools/hoe/till_single_block
execute if score @s sd_const matches 501 run function stardew:tools/hoe/till_single_block

# 潜行 (1) + 高级锄头 -> 范围
# 铁锄 (502) -> 3x3
execute if score @s sd_is_sneaking matches 1 if score @s sd_const matches 502 run function stardew:tools/hoe/t2_till
# 金锄 (503) -> 5x5
execute if score @s sd_is_sneaking matches 1 if score @s sd_const matches 503 run function stardew:tools/hoe/t3_till
# 钻锄 (504) -> 7x7
execute if score @s sd_is_sneaking matches 1 if score @s sd_const matches 504 run function stardew:tools/hoe/t4_till

# 6. 计算并消耗能量（基础消耗：每块耕地1点能量）
# #base_cost = 1 × sd_temp (耕地块数)
scoreboard players operation #base_cost sd_temp = @s sd_temp

# 计算工具等级 (501-504 -> 1-4)
scoreboard players operation #tool_tier sd_temp = @s sd_const
scoreboard players remove #tool_tier sd_temp 500

# 计算实际能量消耗 (应用工具效率)
function stardew:energy/calculate_tool_cost

# 扣除能量
function stardew:energy/consume

# 7. 设置冷却时间（1秒 = 20 ticks）
scoreboard players set @s sd_hoe_cd 20
bossbar set stardew:hoe_cooldown max 20
playsound minecraft:block.note_block.hat player @s ~ ~ ~ 0.3 0.5