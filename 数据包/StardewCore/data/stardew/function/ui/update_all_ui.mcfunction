# data/stardew/function/ui/update_all_ui.mcfunction
# 先将数据存入storage，然后用宏函数更新

# 1. 存储所有需要的数据
execute store result storage stardew:ui year int 1 run scoreboard players get Global sd_year
execute store result storage stardew:ui day int 1 run scoreboard players get Global sd_day
execute store result storage stardew:ui hour int 1 run scoreboard players get Global sd_ui_hour
execute store result storage stardew:ui min int 1 run scoreboard players get Global sd_ui_min

# 2. 设置季节文本和颜色
execute if score Global sd_season matches 1 run data modify storage stardew:ui season set value '{"text":"春","color":"green"}'
execute if score Global sd_season matches 2 run data modify storage stardew:ui season set value '{"text":"夏","color":"red"}'
execute if score Global sd_season matches 3 run data modify storage stardew:ui season set value '{"text":"秋","color":"gold"}'
execute if score Global sd_season matches 4 run data modify storage stardew:ui season set value '{"text":"冬","color":"white"}'

# 2.5 设置星期几文本（0=周日, 1=周一, ..., 6=周六）
execute if score Global sd_day_of_week matches 0 run data modify storage stardew:ui weekday set value "周日"
execute if score Global sd_day_of_week matches 1 run data modify storage stardew:ui weekday set value "周一"
execute if score Global sd_day_of_week matches 2 run data modify storage stardew:ui weekday set value "周二"
execute if score Global sd_day_of_week matches 3 run data modify storage stardew:ui weekday set value "周三"
execute if score Global sd_day_of_week matches 4 run data modify storage stardew:ui weekday set value "周四"
execute if score Global sd_day_of_week matches 5 run data modify storage stardew:ui weekday set value "周五"
execute if score Global sd_day_of_week matches 6 run data modify storage stardew:ui weekday set value "周六"

# 3. 设置时间颜色
execute if score Global sd_ui_hour matches 6..11 run data modify storage stardew:ui time_color set value "white"
execute if score Global sd_ui_hour matches 12..17 run data modify storage stardew:ui time_color set value "yellow"
execute if score Global sd_ui_hour matches 18..23 run data modify storage stardew:ui time_color set value "gold"
execute if score Global sd_ui_hour matches 0..5 run data modify storage stardew:ui time_color set value "red"

# 4. 设置天气
execute if score Global sd_weather matches 0 run data modify storage stardew:ui weather set value '{"text":"☀ 晴天","color":"yellow"}'
execute if score Global sd_weather matches 1 run data modify storage stardew:ui weather set value '{"text":"🌧 雨天","color":"blue"}'
execute if score Global sd_weather matches 2 run data modify storage stardew:ui weather set value '{"text":"⚡ 雷雨","color":"dark_purple"}'
execute if score Global sd_weather matches 3 run data modify storage stardew:ui weather set value '{"text":"❄ 下雪","color":"aqua"}'

# 5. 金币
execute as @a[limit=1,sort=nearest] store result storage stardew:ui gold int 1 run scoreboard players get @s sd_gold

# 5.5 DPS
execute store result storage stardew:ui dps int 1 run scoreboard players get #display sd_player_dps

# 6. 调用宏函数更新UI
function stardew:ui/apply_ui_macro with storage stardew:ui
