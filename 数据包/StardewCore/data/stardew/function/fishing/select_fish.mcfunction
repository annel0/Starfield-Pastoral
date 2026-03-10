# data/stardew/functions/fishing/select_fish.mcfunction

# 1. 检测区域 & 环境
function stardew:fishing/detect_region
function stardew:fishing/check_env

# 2. 生成随机数
execute store result score Global sd_rng run random value 1..100

# 3. 季节分发
execute if score Global sd_season matches 1 run function stardew:fishing/select_fish_spring
execute if score Global sd_season matches 2 run function stardew:fishing/select_fish_summer
execute if score Global sd_season matches 3 run function stardew:fishing/select_fish_fall
execute if score Global sd_season matches 4 run function stardew:fishing/select_fish_winter

# 🛡️ 兜底机制：如果没选到任何鱼，给垃圾
execute if score @s sd_fish_type matches 0 run function stardew:fishing/select_trash

# 📊 调试信息（可注释）
# tellraw @s [{"text":"[钓鱼] 鱼类型: ","color":"gray"},{"score":{"name":"@s","objective":"sd_fish_type"},"color":"yellow"},{"text":" 咬钩时间: ","color":"gray"},{"score":{"name":"@s","objective":"sd_bite_time"},"color":"yellow"}]