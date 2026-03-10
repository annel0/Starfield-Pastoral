# data/stardew/function/ui/apply_ui_macro.mcfunction
# 宏参数: year, day, hour, min, season, weekday, time_color, weather, gold, dps

# 时间行
$team modify sd_ui_2 suffix [{"text":"  🕐 ","color":"gray"},{"text":"$(hour)","color":"$(time_color)"},{"text":":","color":"$(time_color)"},{"text":"$(min)","color":"$(time_color)"}]

# 日期行（添加星期几显示）
$team modify sd_ui_1 suffix [{"text":"  📅 第","color":"gray"},{"text":"$(year)","color":"white"},{"text":"年 ","color":"gray"},$(season),{"text":" ","color":"gray"},{"text":"$(day)","color":"white"},{"text":"日 ","color":"gray"},{"text":"$(weekday)","color":"aqua"}]

# 天气行
$team modify sd_ui_3 suffix [{"text":"  ","color":"gray"},$(weather)]

# 金币行
$execute if entity @a[scores={sd_show_gold=1},limit=1] run team modify sd_ui_4 suffix [{"text":"  💰 ","color":"gold"},{"text":"$(gold)","color":"gold"},{"text":" G","color":"yellow"}]
execute if entity @a[scores={sd_show_gold=1},limit=1] run scoreboard players set 金币 sd_sidebar 4
execute unless entity @a[scores={sd_show_gold=1},limit=1] run scoreboard players reset 金币 sd_sidebar

# DPS行
$team modify sd_ui_5 suffix [{"text":"  ⚔ ","color":"gold"},{"text":"DPS: ","color":"gray"},{"text":"$(dps)","color":"red","bold":true},{"text":"/s","color":"gray"}]
