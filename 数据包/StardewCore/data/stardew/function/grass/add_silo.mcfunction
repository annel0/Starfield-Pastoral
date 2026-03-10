# 添加一个筒仓
# 执行者: 玩家 (@s)

# 初始化筒仓数据（如果需要）
function stardew:grass/init_silo_data

# 增加筒仓数量
scoreboard players add @s sd_silo_count 1

# 增加干草容量（每个筒仓240个干草）
scoreboard players add @s sd_hay_capacity 240

# 显示消息
tellraw @s [{"text":"建造了筒仓！现在有 ","color":"green"},{"score":{"name":"@s","objective":"sd_silo_count"},"color":"yellow"},{"text":" 个筒仓，总容量: ","color":"green"},{"score":{"name":"@s","objective":"sd_hay_capacity"},"color":"yellow"},{"text":" 个干草","color":"green"}]