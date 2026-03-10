# data/stardew/functions/tools/debug_weather_toggle.mcfunction
# [执行者: 玩家]

# 1. 逻辑取反 (Swap Logic)
# 我们利用 2 作为临时中间值，防止逻辑冲突

# 如果是 0 (晴)，先标记为 2 (临时)
execute if score Global sd_weather matches 0 run scoreboard players set Global sd_weather 2

# 如果是 1 (雨)，直接设为 0 (晴)
execute if score Global sd_weather matches 1 run scoreboard players set Global sd_weather 0

# 如果是 2 (刚才标记的)，设为 1 (雨)
execute if score Global sd_weather matches 2 run scoreboard players set Global sd_weather 1

# 2. 执行与反馈 (Execution & Feedback)

# 切换到 雨天 (1)
execute if score Global sd_weather matches 1 run weather rain
execute if score Global sd_weather matches 1 run tellraw @s {"text":"[Debug] 天气已切换为：雨天 🌧️","color":"blue","bold":true}
execute if score Global sd_weather matches 1 run playsound minecraft:weather.rain player @s ~ ~ ~ 1 1

# 切换到 晴天 (0)
execute if score Global sd_weather matches 0 run weather clear
execute if score Global sd_weather matches 0 run tellraw @s {"text":"[Debug] 天气已切换为：晴天 ☀️","color":"yellow","bold":true}
execute if score Global sd_weather matches 0 run playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 1 1