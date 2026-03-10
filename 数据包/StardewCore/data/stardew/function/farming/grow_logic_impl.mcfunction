# data/stardew/functions/farming/grow_logic_impl.mcfunction

# 0. 检查是否已达到最大年龄（如果有设置max_age）
execute if score @s sd_max_crop_age matches 1.. if score @s sd_crop_age >= @s sd_max_crop_age run return 0

# 1. 增加年龄
scoreboard players add @s sd_crop_age 1

# 2. 视觉更新 (调用路由器)
function stardew:farming/visual_update_router