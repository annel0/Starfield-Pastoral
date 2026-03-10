# data/stardew/function/ui/apply_ui.mcfunction
# 宏参数: Year, Day, Hour, MinStr, SeasonName, SeasonColor, TimeColor, WeatherIcon, WeatherName, WeatherColor, GoldAmount

# 第一行：时间（时间根据时段用不同颜色）
$team modify sd_ui_2 suffix [{"text":"  🕐 ","color":"gray"},{"text":"$(Hour):$(MinStr)","color":"$(TimeColor)"}]

# 第二行：年份和季节（季节用对应的颜色）
$team modify sd_ui_1 suffix [{"text":"  第 ","color":"gray"},{"text":"$(Year)","color":"white"},{"text":" 年 ","color":"gray"},{"text":"$(SeasonName)","color":"$(SeasonColor)"},{"text":" ","color":"gray"},{"text":"$(Day)","color":"white"},{"text":" 日","color":"gray"}]

# 第三行：天气（天气用对应的表情符号和颜色）
$team modify sd_ui_3 suffix [{"text":"  $(WeatherIcon) ","color":"gray"},{"text":"$(WeatherName)","color":"$(WeatherColor)"}]

# 第四行：金币（根据玩家的设置显示或隐藏）
# 先设置金币文本
$team modify sd_ui_4 suffix [{"text":"  💰 ","color":"gold"},{"text":"$(GoldAmount)","color":"gold"},{"text":" G","color":"yellow"}]
# 根据设置决定是否显示
execute if entity @a[scores={sd_show_gold=1},limit=1] run scoreboard players set 金币 sd_sidebar 4
execute unless entity @a[scores={sd_show_gold=1},limit=1] run scoreboard players reset 金币 sd_sidebar


