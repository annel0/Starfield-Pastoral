# data/stardew/functions/tools/charge_visual_test.mcfunction
# 测试蓄力视觉效果

# 显示调试信息
title @s actionbar [{"text":"蓄力: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_charge_time"},"color":"green"},{"text":"/","color":"white"},{"score":{"name":"@s","objective":"sd_const"},"color":"aqua"}]

# 测试音效
playsound minecraft:block.note_block.hat master @s ~ ~ ~ 1.0 1.5

# 测试粒子
particle minecraft:dust{color:[1.0,1.0,0.0],scale:1.0} ~ ~1.5 ~ 0.3 0.3 0.3 0 5 force @s
