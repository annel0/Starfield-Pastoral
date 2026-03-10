# =========================================================
# 洒水器路由器 - 根据类型调用浇水函数
# =========================================================

# Debug: 检查类型分数
execute unless score @s sd_sprinkler_type matches 1..3 run tellraw @a[tag=sd_debug] [{"text":"[洒水器] ","color":"aqua"},{"text":"警告: 洒水器类型分数无效! Type=","color":"red"},{"score":{"name":"@s","objective":"sd_sprinkler_type"},"color":"yellow"}]

# Type 1: 基础洒水器(十字4格)
execute if score @s sd_sprinkler_type matches 1 run function stardew:utility/sprinkler/water_area_t1

# Type 2: 优质洒水器(3x3-中心共8格)
execute if score @s sd_sprinkler_type matches 2 run function stardew:utility/sprinkler/water_area_t2

# Type 3: 钻石洒水器(5x5-中心共24格)
execute if score @s sd_sprinkler_type matches 3 run function stardew:utility/sprinkler/water_area_t3
