# data/stardew/functions/fishing/level_up_check.mcfunction
# [执行者: 玩家]
# 作用：循环检查玩家是否满足升级条件，直至无法继续升级。

# ==========================================
# 1. 经验需求表 (完整的 1-10 级总经验)
# ==========================================
# 存储在 sd_config 假玩家上
scoreboard players set #LVL_XP_1 sd_config 100
scoreboard players set #LVL_XP_2 sd_config 250
scoreboard players set #LVL_XP_3 sd_config 450
scoreboard players set #LVL_XP_4 sd_config 700
scoreboard players set #LVL_XP_5 sd_config 1000
scoreboard players set #LVL_XP_6 sd_config 1400
scoreboard players set #LVL_XP_7 sd_config 1900
scoreboard players set #LVL_XP_8 sd_config 2500
scoreboard players set #LVL_XP_9 sd_config 3300
scoreboard players set #LVL_XP_10 sd_config 4500


# 2. 获取下一级所需总经验 (存入 sd_level_xp_req，避免与其他系统冲突)
# 默认值：设为无法升级的高值 (Level 10+)
scoreboard players set @s sd_level_xp_req 99999 

# 根据当前等级，设置下一级的总经验值到 sd_level_xp_req
execute if score @s sd_fishing_lvl matches 0 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_1 sd_config
execute if score @s sd_fishing_lvl matches 1 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_2 sd_config
execute if score @s sd_fishing_lvl matches 2 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_3 sd_config
execute if score @s sd_fishing_lvl matches 3 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_4 sd_config
execute if score @s sd_fishing_lvl matches 4 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_5 sd_config
execute if score @s sd_fishing_lvl matches 5 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_6 sd_config
execute if score @s sd_fishing_lvl matches 6 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_7 sd_config
execute if score @s sd_fishing_lvl matches 7 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_8 sd_config
execute if score @s sd_fishing_lvl matches 8 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_9 sd_config
execute if score @s sd_fishing_lvl matches 9 run scoreboard players operation @s sd_level_xp_req = #LVL_XP_10 sd_config

# 3. 判定是否满足升级条件 (比较与 sd_level_xp_req)
execute if score @s sd_fishing_xp >= @s sd_level_xp_req run function stardew:fishing/level_up_action

# 4. 递归检查 (防止一次跳级)
# 如果 level_up_action 运行了 (等级增加了)，并且等级还在范围内 (1-10)
# [修复] 移除递归，因为 level_up.mcfunction 每 tick 都会调用本函数
# execute if score @s sd_fishing_lvl matches 1..10 run function stardew:fishing/level_up_check