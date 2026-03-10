# data/stardew/function/farming/level_up_check.mcfunction
# [执行者: 玩家]
# 作用：检查玩家农耕经验是否满足升级条件

# ==========================================
# 1. 经验需求表 (完整的 1-10 级总经验)
# ==========================================
# 存储在 sd_config 假玩家上
scoreboard players set #LVL_XP_1 sd_config 100
scoreboard players set #LVL_XP_2 sd_config 380
scoreboard players set #LVL_XP_3 sd_config 770
scoreboard players set #LVL_XP_4 sd_config 1300
scoreboard players set #LVL_XP_5 sd_config 2150
scoreboard players set #LVL_XP_6 sd_config 3300
scoreboard players set #LVL_XP_7 sd_config 4800
scoreboard players set #LVL_XP_8 sd_config 6900
scoreboard players set #LVL_XP_9 sd_config 10000
scoreboard players set #LVL_XP_10 sd_config 15000

# 2. 获取下一级所需总经验
# 默认值：设为无法升级的高值 (Level 10+)
scoreboard players set @s sd_level_xp_req 99999 

# 根据当前等级，设置下一级的总经验值
execute if score @s sd_farming_lvl matches 0 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_1 sd_config
execute if score @s sd_farming_lvl matches 1 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_2 sd_config
execute if score @s sd_farming_lvl matches 2 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_3 sd_config
execute if score @s sd_farming_lvl matches 3 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_4 sd_config
execute if score @s sd_farming_lvl matches 4 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_5 sd_config
execute if score @s sd_farming_lvl matches 5 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_6 sd_config
execute if score @s sd_farming_lvl matches 6 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_7 sd_config
execute if score @s sd_farming_lvl matches 7 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_8 sd_config
execute if score @s sd_farming_lvl matches 8 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_9 sd_config
execute if score @s sd_farming_lvl matches 9 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_10 sd_config

# 3. 判定是否满足升级条件
execute if score @s sd_farming_xp >= @s sd_level_xp_req run function stardew:farming/level_up_action
