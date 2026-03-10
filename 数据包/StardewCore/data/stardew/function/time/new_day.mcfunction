# data/stardew/functions/time/new_day.mcfunction

# 1. 重置时间
scoreboard players set Global sd_time 360
time set 0

# 1.1 重置每日事件标记
scoreboard players set Global sd_event_1800 0
scoreboard players set Global sd_event_2200 0
scoreboard players set Global sd_event_0000 0
scoreboard players set Global sd_event_0130 0

# 1.5 能量恢复（根据是否耗尽标签决定）
# 重要：先处理没有透支的玩家（100%），再处理透支的玩家（50%），最后移除标签

# 没有耗尽标签的玩家恢复100%能量
execute as @a[tag=!sd_energy_depleted] run scoreboard players operation @s sd_energy = @s sd_max_energy

# 有 sd_energy_depleted 标签的玩家只恢复50%
execute as @a[tag=sd_energy_depleted] run scoreboard players operation @s sd_energy = @s sd_max_energy
execute as @a[tag=sd_energy_depleted] run scoreboard players set #50 sd_temp 50
execute as @a[tag=sd_energy_depleted] run scoreboard players set #100 sd_temp 100
execute as @a[tag=sd_energy_depleted] run scoreboard players operation @s sd_energy *= #50 sd_temp
execute as @a[tag=sd_energy_depleted] run scoreboard players operation @s sd_energy /= #100 sd_temp
execute as @a[tag=sd_energy_depleted] run tellraw @s {"text":"⚠ 昨天体力透支，今天只恢复了50%的能量...","color":"yellow","bold":true}

# 最后移除透支标签（确保上面的判断都执行完毕）
tag @a[tag=sd_energy_depleted] remove sd_energy_depleted

# --- 通用实用设施过夜奖励（为活跃的实用设施加 60 分钟）
# 使用通用标记 sd_utility_active 来判断哪些实体为"正在进行中"的 utility
execute as @e[type=interaction,tag=sd_utility] if score @s sd_utility_active matches 1 run scoreboard players add @s sd_utility_bonus 60

# 6. 动物系统每日重置
# 应用被困在外面的心情惩罚
execute as @e[type=#stardew:animals,tag=stardew.animal.stuck_outside] run scoreboard players remove @s stardew.animal.mood 20
execute as @e[type=#stardew:animals,tag=stardew.animal.stuck_outside] run tag @s remove stardew.animal.stuck_outside

# 重置所有动物的今日抚摸标记
scoreboard players set @e[type=#stardew:animals,tag=stardew.animal] stardew.animal.friendship_today 0
# 重置所有动物的今日喂食标记
scoreboard players set @e[type=#stardew:animals,tag=stardew.animal] stardew.animal.fed_today 0
# 增加所有动物的年龄
execute as @e[type=#stardew:animals,tag=stardew.animal] run scoreboard players add @s stardew.animal.age 1

# 6.5. NPC系统每日重置
# 重置所有玩家与各个NPC的今日对话标记
scoreboard players set @a stardew.talked.abigail 0
# 未来添加更多NPC：
# scoreboard players set @a stardew.talked.emily 0
# scoreboard players set @a stardew.talked.haley 0
# ...

# 6.6. NPC系统每周重置（每周一重置送礼计数）
# 计算星期几：sd_day % 7 (0=周一, 6=周日)
scoreboard players operation #week_day stardew.temp = Global sd_day
scoreboard players set #7 stardew.const 7
scoreboard players operation #week_day stardew.temp %= #7 stardew.const

# 保存星期几到Global变量（供对话系统使用）
scoreboard players operation Global sd_day_of_week = #week_day stardew.temp

# 如果是周一(sd_day % 7 == 1)，重置所有玩家的送礼计数
execute if score #week_day stardew.temp matches 1 run scoreboard players set @a stardew.gifted.abigail 0
# 未来添加更多NPC：
# execute if score #week_day stardew.temp matches 1 run scoreboard players set @a stardew.gifted.emily 0
# execute if score #week_day stardew.temp matches 1 run scoreboard players set @a stardew.gifted.haley 0

# TODO: 友谊值每日衰减（如果没有对话）
# TODO: NPC日程系统（根据时间移动NPC）

# 鸡产蛋判定
function stardew:animal/produce/check_chicken_produce

# 鸭子产物判定
function stardew:animal/produce/check_duck_produce

# 兔子产物判定
function stardew:animal/produce/check_rabbit_produce

# 牛产奶判定
function stardew:animal/produce/check_cow_produce

# 绵羊产羊毛判定
function stardew:animal/produce/check_sheep_produce

# 山羊产羊奶判定
function stardew:animal/produce/check_goat_produce

# 猪产松露判定
function stardew:animal/produce/check_pig_produce

scoreboard players set Global sd_ui_stage 0
scoreboard players set Global sd_ui_flash_timer 0

# 2. 日期与季节
scoreboard players add Global sd_day 1
execute if score Global sd_day matches 29.. run scoreboard players add Global sd_season 1
execute if score Global sd_day matches 29.. run scoreboard players set Global sd_day 1
execute if score Global sd_day matches 1 run function stardew:farming/wither_logic
execute if score Global sd_season matches 5.. run scoreboard players add Global sd_year 1
execute if score Global sd_season matches 5.. run scoreboard players set Global sd_season 1

# 3. 作物生长 (Grow)
# [核心修复] 必须选择逻辑实体 (marker)，而不是 visual 实体
execute as @e[type=marker,tag=sd_crop] at @s run function stardew:farming/grow_manager

# B. 树木生长 (Interaction 实体)
execute as @e[type=interaction,tag=sd_tree] at @s run function stardew:tree/grow_check

# C. 草扩散 (Interaction 实体)
function stardew:grass/daily_growth

# D. 验证草方块一致性
function stardew:grass/validate_grass_blocks

# 4. 干涸结算 (Dry)
# 
# 策略：每天所有耕地都变干（除了有保湿土壤的）
# 保湿土壤（fertilizer_type=3）会保护耕地不干涸
# 玩家需要每天重新浇水才能让作物生长（除非使用保湿土壤）
#

# 调用干涸处理函数（已优化，考虑保湿土壤）
function stardew:farming/dry_farmland

# C. 洒水器自动浇水（在晒干之后！）
function stardew:utility/sprinkler/auto_water

# D. 所有的树又可以摇种子了
execute as @e[type=interaction,tag=sd_tree] run scoreboard players set @s sd_shaked 0

# 5. 提示
title @a title {"score":{"name":"Global","objective":"sd_day"},"color":"yellow","bold":true}
title @a subtitle {"text":"新的一天开始 | 耕地已干涸","color":"aqua"}

# --- 通用实用设施过夜奖励（为活跃的实用设施加 60 分钟）
# 使用通用标记 sd_utility_active 来判断哪些实体为“正在进行中”的 utility
execute as @e[type=interaction,tag=sd_utility] if score @s sd_utility_active matches 1 run scoreboard players add @s sd_utility_bonus 60