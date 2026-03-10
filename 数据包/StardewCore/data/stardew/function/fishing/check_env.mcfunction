# data/stardew/functions/fishing/check_env.mcfunction
# [执行者: 玩家]
# 作用：检测时间段、天气等环境信息，供选鱼逻辑使用

# ============================================================
# 1. 时间段判定 (基于 sd_time)
# ============================================================
# sd_time_slot 定义:
# 0 = 午夜-凌晨2点 (0:00-2:00)   [18000-20000]
# 1 = 凌晨2点-早6点 (2:00-6:00)  [20000-24000, 0]
# 2 = 早6点-上午9点 (6:00-9:00)  [0-3000]
# 3 = 上午9点-中午12点 (9:00-12:00) [3000-6000]
# 4 = 中午12点-下午4点 (12:00-16:00) [6000-10000]
# 5 = 下午4点-晚7点 (16:00-19:00) [10000-13000]
# 6 = 晚7点-晚10点 (19:00-22:00) [13000-16000]
# 7 = 晚10点-午夜 (22:00-0:00)   [16000-18000]

# 默认时段2 (白天)
scoreboard players set @s sd_time_slot 2

# 午夜-凌晨2点
execute if score Global sd_time matches 18000..19999 run scoreboard players set @s sd_time_slot 0
# 凌晨2点-早6点
execute if score Global sd_time matches 20000.. run scoreboard players set @s sd_time_slot 1
execute if score Global sd_time matches ..0 run scoreboard players set @s sd_time_slot 1
# 早6点-上午9点
execute if score Global sd_time matches 0..2999 run scoreboard players set @s sd_time_slot 2
# 上午9点-中午12点
execute if score Global sd_time matches 3000..5999 run scoreboard players set @s sd_time_slot 3
# 中午12点-下午4点
execute if score Global sd_time matches 6000..9999 run scoreboard players set @s sd_time_slot 4
# 下午4点-晚7点
execute if score Global sd_time matches 10000..12999 run scoreboard players set @s sd_time_slot 5
# 晚7点-晚10点
execute if score Global sd_time matches 13000..15999 run scoreboard players set @s sd_time_slot 6
# 晚10点-午夜
execute if score Global sd_time matches 16000..17999 run scoreboard players set @s sd_time_slot 7

# ============================================================
# 2. 简化的昼夜判定 (保留兼容旧代码)
# ============================================================
# sd_is_night: 0=白天, 1=晚上
scoreboard players set @s sd_is_night 0
execute if score Global sd_time matches 13000.. run scoreboard players set @s sd_is_night 1
execute if score Global sd_time matches ..0 run scoreboard players set @s sd_is_night 1

# ============================================================
# 3. 调试信息显示
# ============================================================
execute if entity @s[tag=sd_debug_mode] run function stardew:fishing/debug_env