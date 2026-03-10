# 获取阿比盖尔的对话
# @s = 玩家
# 根据：友谊度、星期、季节、特殊日期、事件等选择对话

# 默认对话
data modify storage stardew:dialogue current.text set value ['{"text":"……嗨。","color":"#2C1810"}']

# TODO: 实现完整的对话选择逻辑
# 1. 检查特殊日期（彩蛋节前一天、花舞节前一天等）
# 2. 检查已完成的事件
# 3. 检查友谊度+星期+季节的组合
# 4. 回退到基础对话（星期+季节）

# 按星期选择基础对话
# sd_day_of_week: 0=周日, 1=周一, 2=周二, ..., 6=周六
execute if score Global sd_day_of_week matches 1 run function stardew:npc/abigail/dialogue/weekday/mon
execute if score Global sd_day_of_week matches 2 run function stardew:npc/abigail/dialogue/weekday/tue
execute if score Global sd_day_of_week matches 3 run function stardew:npc/abigail/dialogue/weekday/wed
execute if score Global sd_day_of_week matches 4 run function stardew:npc/abigail/dialogue/weekday/thu
execute if score Global sd_day_of_week matches 5 run function stardew:npc/abigail/dialogue/weekday/fri
execute if score Global sd_day_of_week matches 6 run function stardew:npc/abigail/dialogue/weekday/sat
execute if score Global sd_day_of_week matches 0 run function stardew:npc/abigail/dialogue/weekday/sun