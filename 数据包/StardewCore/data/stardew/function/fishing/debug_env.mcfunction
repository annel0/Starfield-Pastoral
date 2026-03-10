# data/stardew/functions/fishing/debug_env.mcfunction
# 调试信息显示

# 构建时间段文本
execute if score @s sd_time_slot matches 0 run data modify storage stardew:temp time_text set value '{"text":"午夜-凌晨2点","color":"dark_blue"}'
execute if score @s sd_time_slot matches 1 run data modify storage stardew:temp time_text set value '{"text":"凌晨2点-早6点","color":"dark_gray"}'
execute if score @s sd_time_slot matches 2 run data modify storage stardew:temp time_text set value '{"text":"早6点-上午9点","color":"yellow"}'
execute if score @s sd_time_slot matches 3 run data modify storage stardew:temp time_text set value '{"text":"上午9点-中午12点","color":"gold"}'
execute if score @s sd_time_slot matches 4 run data modify storage stardew:temp time_text set value '{"text":"中午12点-下午4点","color":"red"}'
execute if score @s sd_time_slot matches 5 run data modify storage stardew:temp time_text set value '{"text":"下午4点-晚7点","color":"light_purple"}'
execute if score @s sd_time_slot matches 6 run data modify storage stardew:temp time_text set value '{"text":"晚7点-晚10点","color":"dark_purple"}'
execute if score @s sd_time_slot matches 7 run data modify storage stardew:temp time_text set value '{"text":"晚10点-午夜","color":"dark_blue"}'

# 构建区域文本
execute if score @s sd_fish_region matches 1 run data modify storage stardew:temp region_text set value '{"text":"河流","color":"aqua"}'
execute if score @s sd_fish_region matches 2 run data modify storage stardew:temp region_text set value '{"text":"湖泊","color":"blue"}'
execute if score @s sd_fish_region matches 3 run data modify storage stardew:temp region_text set value '{"text":"海洋","color":"dark_aqua"}'
execute if score @s sd_fish_region matches 4 run data modify storage stardew:temp region_text set value '{"text":"神秘森林","color":"dark_green"}'
execute if score @s sd_fish_region matches 5 run data modify storage stardew:temp region_text set value '{"text":"森林池塘","color":"green"}'
execute if score @s sd_fish_region matches 6 run data modify storage stardew:temp region_text set value '{"text":"下水道","color":"gray"}'
execute if score @s sd_fish_region matches 7 run data modify storage stardew:temp region_text set value '{"text":"森林河流","color":"dark_aqua"}'

# 构建天气文本
execute if score Global sd_weather matches 0 run data modify storage stardew:temp weather_text set value '{"text":"☀ 晴天","color":"yellow"}'
execute if score Global sd_weather matches 1 run data modify storage stardew:temp weather_text set value '{"text":"☂ 雨天","color":"blue"}'

# 显示actionbar (使用macro)
title @s actionbar ["",{"text":"[钓鱼环境] ","color":"gray"},{"nbt":"weather_text","storage":"stardew:temp","interpret":true},{"text":" | ","color":"dark_gray"},{"nbt":"region_text","storage":"stardew:temp","interpret":true},{"text":" | ","color":"dark_gray"},{"nbt":"time_text","storage":"stardew:temp","interpret":true}]
