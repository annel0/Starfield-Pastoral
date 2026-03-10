# Debug: 移除筒仓
# 执行者: 玩家 (@s)

# 初始化筒仓数据
function stardew:grass/init_silo_data

# 检查是否有筒仓
execute if score @s sd_silo_count matches ..0 run return run tellraw @s {"text":"[Debug] 你没有筒仓可以移除","color":"red"}

# 减少筒仓数量
scoreboard players remove @s sd_silo_count 1

# 减少干草容量（每个筒仓240个干草）
scoreboard players remove @s sd_hay_capacity 240

# 如果容量变为负数或0，清除所有干草
execute if score @s sd_hay_capacity matches ..0 run scoreboard players set @s sd_hay_stored 0
execute if score @s sd_hay_capacity matches ..0 run scoreboard players set @s sd_hay_capacity 0

# 如果存储的干草超过新容量，调整为新容量
execute if score @s sd_hay_stored > @s sd_hay_capacity run scoreboard players operation @s sd_hay_stored = @s sd_hay_capacity

# 显示消息
tellraw @s [{"text":"[Debug] 移除了1个筒仓！现在有 ","color":"yellow"},{"score":{"name":"@s","objective":"sd_silo_count"},"color":"green"},{"text":" 个筒仓，容量: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_hay_capacity"},"color":"green"},{"text":" 个干草","color":"yellow"}]

# 显示筒仓状态
tellraw @s [{"text":"[Debug] 筒仓状态: ","color":"aqua"},{"score":{"name":"@s","objective":"sd_silo_count"},"color":"yellow"},{"text":"个筒仓, 已存储","color":"aqua"},{"score":{"name":"@s","objective":"sd_hay_stored"},"color":"green"},{"text":"/","color":"aqua"},{"score":{"name":"@s","objective":"sd_hay_capacity"},"color":"yellow"},{"text":" 干草","color":"aqua"}]