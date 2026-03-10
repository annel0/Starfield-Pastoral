# data/stardew/function/mining/highlight/raycast_start.mcfunction
# 开始射线检测 - 检测玩家是否指向矿石
# 执行者: 玩家 (@s)

# 1. 检查玩家是否持有镐子
execute unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=201] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=202] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=203] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=204] run return 0

# 2. 添加射线检测标签
tag @s add sd_mining_raycast_player

# 3. 初始化射线检测距离计数器
scoreboard players set #raycast_distance sd_temp 0

# 4. 从玩家眼睛位置开始射线检测
execute anchored eyes positioned ^ ^ ^ run function stardew:mining/highlight/raycast_loop

# 5. 清除标签
tag @s remove sd_mining_raycast_player
