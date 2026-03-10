# Debug: 查看筒仓状态
# 执行者: 玩家 (@s)

# 初始化筒仓数据
function stardew:grass/init_silo_data

# 显示详细状态
tellraw @s {"text":"========== 筒仓状态 ==========","color":"gold","bold":true}
tellraw @s [{"text":"筒仓数量: ","color":"white"},{"score":{"name":"@s","objective":"sd_silo_count"},"color":"yellow","bold":true},{"text":" 个","color":"white"}]
tellraw @s [{"text":"干草存储: ","color":"white"},{"score":{"name":"@s","objective":"sd_hay_stored"},"color":"green","bold":true},{"text":" / ","color":"gray"},{"score":{"name":"@s","objective":"sd_hay_capacity"},"color":"yellow","bold":true},{"text":" 个","color":"white"}]

# 计算百分比
execute if score @s sd_hay_capacity matches 1.. run scoreboard players operation temp_percentage sd_temp = @s sd_hay_stored
execute if score @s sd_hay_capacity matches 1.. run scoreboard players set 100 sd_temp 100
execute if score @s sd_hay_capacity matches 1.. run scoreboard players operation temp_percentage sd_temp *= 100 sd_temp
execute if score @s sd_hay_capacity matches 1.. run scoreboard players operation temp_percentage sd_temp /= @s sd_hay_capacity

execute if score @s sd_hay_capacity matches 1.. run tellraw @s [{"text":"使用率: ","color":"white"},{"score":{"name":"temp_percentage","objective":"sd_temp"},"color":"aqua","bold":true},{"text":"%","color":"aqua","bold":true}]
execute if score @s sd_hay_capacity matches ..0 run tellraw @s {"text":"使用率: 0%","color":"gray"}

tellraw @s {"text":"=============================","color":"gold","bold":true}