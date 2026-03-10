# Debug: 添加筒仓
# 执行者: 玩家 (@s)

# 添加一个筒仓
function stardew:grass/add_silo

# 显示筒仓状态
tellraw @s [{"text":"[Debug] 筒仓状态: ","color":"aqua"},{"score":{"name":"@s","objective":"sd_silo_count"},"color":"yellow"},{"text":"个筒仓, 已存储","color":"aqua"},{"score":{"name":"@s","objective":"sd_hay_stored"},"color":"green"},{"text":"/","color":"aqua"},{"score":{"name":"@s","objective":"sd_hay_capacity"},"color":"yellow"},{"text":" 干草","color":"aqua"}]