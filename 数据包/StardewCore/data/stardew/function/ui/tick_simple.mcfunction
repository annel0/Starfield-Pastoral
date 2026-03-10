# data/stardew/function/ui/tick_simple.mcfunction
# 简化版UI更新 - 不使用宏函数，直接更新

# 时间行（第1行）
execute if score Global sd_ui_hour matches 6 if score Global sd_ui_min matches 0 run team modify sd_ui_2 suffix [{"text":"  🕐 ","color":"gray"},{"text":"6:00","color":"white"}]
execute if score Global sd_ui_hour matches 6 if score Global sd_ui_min matches 10 run team modify sd_ui_2 suffix [{"text":"  🕐 ","color":"gray"},{"text":"6:10","color":"white"}]
execute if score Global sd_ui_hour matches 12 if score Global sd_ui_min matches 0 run team modify sd_ui_2 suffix [{"text":"  🕐 ","color":"gray"},{"text":"12:00","color":"yellow"}]

# 日期行（第2行）- 测试固定内容
team modify sd_ui_1 suffix [{"text":"  第 ","color":"gray"},{"text":"1","color":"white"},{"text":" 年 ","color":"gray"},{"text":"春","color":"green"},{"text":" ","color":"gray"},{"text":"1","color":"white"},{"text":" 日","color":"gray"}]

# 天气行（第3行）
team modify sd_ui_3 suffix [{"text":"  ☀ ","color":"gray"},{"text":"晴","color":"yellow"}]

# 金币行（第4行）
execute if entity @a[scores={sd_show_gold=1},limit=1] run team modify sd_ui_4 suffix [{"text":"  💰 ","color":"gold"},{"text":"0","color":"gold"},{"text":" G","color":"yellow"}]
