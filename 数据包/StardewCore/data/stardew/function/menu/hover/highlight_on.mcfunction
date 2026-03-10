# data/stardew/function/menu/hover/highlight_on.mcfunction
# [执行者: 按钮] 开启高光效果

# 0. 首先获取菜单层级
execute store result score #MenuLevel sd_menu_ctrl run scoreboard players get @a[scores={sd_menu_sequence=1..},limit=1] sd_menu_level

# 0.1 阻止隐藏槽位的高光效果（根据菜单层级）
# Debug菜单 (level=1): slot 1 和 9 隐藏
execute if score #MenuLevel sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 1 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 9 run return 0
# 居民菜单 (level=4): slot 2-7 暂时隐藏
execute if score #MenuLevel sd_menu_ctrl matches 4 if score @s sd_menu_slot matches 2 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 4 if score @s sd_menu_slot matches 3 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 4 if score @s sd_menu_slot matches 4 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 4 if score @s sd_menu_slot matches 5 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 4 if score @s sd_menu_slot matches 6 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 4 if score @s sd_menu_slot matches 7 run return 0
# 季节子菜单 (level=21): slot 1, 2, 9 隐藏
execute if score #MenuLevel sd_menu_ctrl matches 21 if score @s sd_menu_slot matches 1 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 21 if score @s sd_menu_slot matches 2 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 21 if score @s sd_menu_slot matches 9 run return 0
# 天气子菜单 (level=22): slot 1, 2, 9 隐藏
execute if score #MenuLevel sd_menu_ctrl matches 22 if score @s sd_menu_slot matches 1 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 22 if score @s sd_menu_slot matches 2 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 22 if score @s sd_menu_slot matches 9 run return 0
# 等级选择菜单 (level=23): slot 1, 9 隐藏
execute if score #MenuLevel sd_menu_ctrl matches 23 if score @s sd_menu_slot matches 1 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 23 if score @s sd_menu_slot matches 9 run return 0
# 时间调整菜单 (level=24): slot 1 隐藏
execute if score #MenuLevel sd_menu_ctrl matches 24 if score @s sd_menu_slot matches 1 run return 0
# 设置菜单 (level=25): slot 2-7 隐藏
execute if score #MenuLevel sd_menu_ctrl matches 25 if score @s sd_menu_slot matches 2 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 25 if score @s sd_menu_slot matches 3 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 25 if score @s sd_menu_slot matches 4 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 25 if score @s sd_menu_slot matches 5 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 25 if score @s sd_menu_slot matches 6 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 25 if score @s sd_menu_slot matches 7 run return 0
# 技能调整菜单 (level=231-235): slot 1, 2, 3, 8, 9 隐藏
execute if score #MenuLevel sd_menu_ctrl matches 231..235 if score @s sd_menu_slot matches 1 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 231..235 if score @s sd_menu_slot matches 2 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 231..235 if score @s sd_menu_slot matches 3 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 231..235 if score @s sd_menu_slot matches 8 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 231..235 if score @s sd_menu_slot matches 9 run return 0

# 物品主菜单 (level=10): 第0页无隐藏槽位, 第1页 slot 3-7 隐藏
execute store result score #MenuPage sd_menu_ctrl run scoreboard players get @a[scores={sd_menu_sequence=1..},limit=1] sd_menu_page
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 3 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 4 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 5 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 6 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 7 run return 0
# 作物子菜单 (level=11): slot 1, 2, 9 隐藏
execute if score #MenuLevel sd_menu_ctrl matches 11 if score @s sd_menu_slot matches 1 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 11 if score @s sd_menu_slot matches 2 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 11 if score @s sd_menu_slot matches 9 run return 0
# 鱼类子菜单 (level=12): slot 9 隐藏
execute if score #MenuLevel sd_menu_ctrl matches 12 if score @s sd_menu_slot matches 9 run return 0
# 春季作物品质选择 (level=111): slot 1, 2, 9 隐藏
execute if score #MenuLevel sd_menu_ctrl matches 111 if score @s sd_menu_slot matches 1 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 111 if score @s sd_menu_slot matches 2 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 111 if score @s sd_menu_slot matches 9 run return 0
# 夏季作物品质选择 (level=112): slot 1, 2, 9 隐藏
execute if score #MenuLevel sd_menu_ctrl matches 112 if score @s sd_menu_slot matches 1 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 112 if score @s sd_menu_slot matches 2 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 112 if score @s sd_menu_slot matches 9 run return 0
# 秋季作物品质选择 (level=113): slot 1, 2, 9 隐藏
execute if score #MenuLevel sd_menu_ctrl matches 113 if score @s sd_menu_slot matches 1 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 113 if score @s sd_menu_slot matches 2 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 113 if score @s sd_menu_slot matches 9 run return 0
# 冬季作物品质选择 (level=114): slot 1, 2, 9 隐藏
execute if score #MenuLevel sd_menu_ctrl matches 114 if score @s sd_menu_slot matches 1 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 114 if score @s sd_menu_slot matches 2 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 114 if score @s sd_menu_slot matches 9 run return 0

# 1. 放大到0.7f并开启发光
data merge entity @s {start_interpolation:0,interpolation_duration:5,transformation:{scale:[0.7f,0.7f,0.7f]},Glowing:1b}

# 2. 获取按钮的entity_num、玩家菜单级别，并传送标题文字到按钮上方
execute store result score #Temp sd_menu_ctrl run scoreboard players get @s sd_menu_entity_num
execute at @s positioned ~ ~0.6 ~ as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run tp @s ~ ~ ~ ~180 ~

# 3. 主菜单文字 (level=0)
execute if score #MenuLevel sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 1 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"技能等级","color":"gold","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"合成","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"装备","color":"light_purple","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"居民","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"作弊模式","color":"light_purple","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"设置","color":"white","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"存储","color":"aqua","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"地图","color":"dark_green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 9 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 4. Debug菜单文字 (level=1)
# slot 1 和 9 在 debug 菜单中隐藏，不显示文字（直接返回）
execute if score #MenuLevel sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 1 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 9 run return 0

execute if score #MenuLevel sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"季节","color":"green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"天气","color":"aqua","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"时间","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"物品","color":"gold","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"升级","color":"light_purple","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 4.3. 居民菜单文字 (level=4)
# slot 2-7 暂时隐藏，不显示文字（在文件开头的阻止逻辑中已处理）
execute if score #MenuLevel sd_menu_ctrl matches 4 if score @s sd_menu_slot matches 1 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"阿比盖尔","color":"light_purple","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 4 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 4 if score @s sd_menu_slot matches 9 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 4.4. 背包列表文字 (level=5)
# Slot 1-7: 显示背包名称（背包#X）
execute if score #MenuLevel sd_menu_ctrl matches 5 if score @s sd_menu_slot matches 1 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"背包 #1","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 5 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"背包 #2","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 5 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"背包 #3","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 5 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"背包 #4","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 5 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"背包 #5","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 5 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"背包 #6","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 5 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"背包 #7","color":"yellow","bold":true}'
# Slot 8: 返回主菜单
execute if score #MenuLevel sd_menu_ctrl matches 5 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
# Slot 9: 关闭菜单
execute if score #MenuLevel sd_menu_ctrl matches 5 if score @s sd_menu_slot matches 9 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 4.5. 背包操作页文字 (level=51)
# Slot 1: 打开背包
execute if score #MenuLevel sd_menu_ctrl matches 51 if score @s sd_menu_slot matches 1 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"打开背包","color":"green","bold":true}'
# Slot 2: 重命名
execute if score #MenuLevel sd_menu_ctrl matches 51 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"重命名","color":"aqua","bold":true}'
# Slot 3: 换色
execute if score #MenuLevel sd_menu_ctrl matches 51 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"更换颜色","color":"light_purple","bold":true}'
# Slot 8: 返回
execute if score #MenuLevel sd_menu_ctrl matches 51 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
# Slot 9: 关闭菜单
execute if score #MenuLevel sd_menu_ctrl matches 51 if score @s sd_menu_slot matches 9 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 4.6. 装备菜单文字 (level=2)
# 槽位1: 鞋子槽位 - 空槽显示"空"，有装备显示装备名
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 1 run execute store result score #PlayerNum sd_temp run scoreboard players get @s sd_menu_entity_num
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 1 as @a if score @s sd_menu_sequence = #PlayerNum sd_temp if score @s sd_equip_boots matches 0 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"空","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 1 as @a if score @s sd_menu_sequence = #PlayerNum sd_temp if score @s sd_equip_boots matches 1.. run function stardew:equipment/display/hover_boots

# 槽位2: 戒指1槽位
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 2 run execute store result score #PlayerNum sd_temp run scoreboard players get @s sd_menu_entity_num
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 2 as @a if score @s sd_menu_sequence = #PlayerNum sd_temp if score @s sd_equip_ring1 matches 0 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"空","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 2 as @a if score @s sd_menu_sequence = #PlayerNum sd_temp if score @s sd_equip_ring1 matches 1.. run function stardew:equipment/display/hover_ring1

# 槽位3: 戒指2槽位
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 3 run execute store result score #PlayerNum sd_temp run scoreboard players get @s sd_menu_entity_num
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 3 as @a if score @s sd_menu_sequence = #PlayerNum sd_temp if score @s sd_equip_ring2 matches 0 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"空","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 3 as @a if score @s sd_menu_sequence = #PlayerNum sd_temp if score @s sd_equip_ring2 matches 1.. run function stardew:equipment/display/hover_ring2

# 槽位4: 戒指3槽位 (需要解锁)
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 4 run execute store result score #PlayerNum sd_temp run scoreboard players get @s sd_menu_entity_num
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 4 as @a if score @s sd_menu_sequence = #PlayerNum sd_temp if score @s sd_unlock_ring3 matches 0 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"未解锁","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 4 as @a if score @s sd_menu_sequence = #PlayerNum sd_temp if score @s sd_unlock_ring3 matches 1 if score @s sd_equip_ring3 matches 0 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"空","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 4 as @a if score @s sd_menu_sequence = #PlayerNum sd_temp if score @s sd_unlock_ring3 matches 1 if score @s sd_equip_ring3 matches 1.. run function stardew:equipment/display/hover_ring3

# 槽位5: 戒指4槽位 (需要解锁)
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 5 run execute store result score #PlayerNum sd_temp run scoreboard players get @s sd_menu_entity_num
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 5 as @a if score @s sd_menu_sequence = #PlayerNum sd_temp if score @s sd_unlock_ring4 matches 0 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"未解锁","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 5 as @a if score @s sd_menu_sequence = #PlayerNum sd_temp if score @s sd_unlock_ring4 matches 1 if score @s sd_equip_ring4 matches 0 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"空","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 5 as @a if score @s sd_menu_sequence = #PlayerNum sd_temp if score @s sd_unlock_ring4 matches 1 if score @s sd_equip_ring4 matches 1.. run function stardew:equipment/display/hover_ring4

# 槽位6-7: 隐藏
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 6..7 run return 0

# 槽位8-9: 返回和关闭
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 9 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 5. 季节子菜单文字 (level=21)
execute if score #MenuLevel sd_menu_ctrl matches 21 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"切换春季","color":"green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 21 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"切换夏季","color":"gold","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 21 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"切换秋季","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 21 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"切换冬季","color":"aqua","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 21 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 21 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 6. 天气子菜单文字 (level=22)
execute if score #MenuLevel sd_menu_ctrl matches 22 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"切换晴天","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 22 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"切换雨天","color":"blue","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 22 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"切换雷雨","color":"dark_purple","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 22 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"切换下雪","color":"aqua","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 22 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 22 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 6.5. 时间调整菜单文字 (level=24)
execute if score #MenuLevel sd_menu_ctrl matches 24 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"快进一天","color":"gold","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 24 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"倒退一天","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 24 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"快进一季","color":"green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 24 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"倒退一季","color":"dark_gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 24 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"快进一小时","color":"aqua","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 24 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"倒退一小时","color":"blue","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 24 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 24 if score @s sd_menu_slot matches 9 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 6.7. 设置菜单文字 (level=25)
execute if score #MenuLevel sd_menu_ctrl matches 25 if score @s sd_menu_slot matches 1 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"金币显示","color":"gold","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 25 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 25 if score @s sd_menu_slot matches 9 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 7. 等级选择菜单文字 (level=23)
execute if score #MenuLevel sd_menu_ctrl matches 23 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"耕种等级","color":"green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 23 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"战斗等级","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 23 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"钓鱼等级","color":"aqua","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 23 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"采集等级","color":"dark_green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 23 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"挖矿等级","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 23 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 23 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 8. 耕种等级调整菜单文字 (level=231)
execute if score #MenuLevel sd_menu_ctrl matches 231 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"升级","color":"green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 231 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"降级","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 231 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 231 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 9. 战斗等级调整菜单文字 (level=232)
execute if score #MenuLevel sd_menu_ctrl matches 232 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"升级","color":"green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 232 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"降级","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 232 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 232 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 10. 钓鱼等级调整菜单文字 (level=233)
execute if score #MenuLevel sd_menu_ctrl matches 233 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"升级","color":"green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 233 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"降级","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 233 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 233 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 11. 采集等级调整菜单文字 (level=234)
execute if score #MenuLevel sd_menu_ctrl matches 234 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"升级","color":"green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 234 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"降级","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 234 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 234 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 12. 挖矿等级调整菜单文字 (level=235)
execute if score #MenuLevel sd_menu_ctrl matches 235 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"升级","color":"green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 235 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"降级","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 235 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 235 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 13. 物品主菜单文字 (level=10)
# 需要获取玩家的页码来区分不同页面的hover文字
execute store result score #MenuPage sd_menu_ctrl run scoreboard players get @a[scores={sd_menu_sequence=1..},limit=1] sd_menu_page

# 第0页
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 1 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"作物","color":"green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"鱼类","color":"aqua","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"种子","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"工具","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"宝石","color":"light_purple","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"渔具","color":"blue","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"资源","color":"gold","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 0 if score @s sd_menu_slot matches 9 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"下一页","color":"blue","bold":true}'

# 第1页
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 1 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"调试工具","color":"light_purple","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"矿物生成","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"上一页","color":"blue","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 10 if score #MenuPage sd_menu_ctrl matches 1 if score @s sd_menu_slot matches 9 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'

# 14. 作物子菜单文字 (level=11)
execute if score #MenuLevel sd_menu_ctrl matches 11 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"春作物","color":"green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 11 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"夏作物","color":"gold","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 11 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"秋作物","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 11 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"冬作物","color":"aqua","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 11 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 11 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭","color":"red","bold":true}'

# 15. 鱼类子菜单文字 (level=12)
execute if score #MenuLevel sd_menu_ctrl matches 12 if score @s sd_menu_slot matches 1 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"春鱼","color":"green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 12 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"夏鱼","color":"gold","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 12 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"秋鱼","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 12 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"冬鱼","color":"aqua","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 12 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"特殊鱼","color":"light_purple","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 12 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"垃圾","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 12 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"资源鱼","color":"blue","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 12 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'

# 15.5. 种子子菜单文字 (level=13)
execute if score #MenuLevel sd_menu_ctrl matches 13 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"春季种子","color":"green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 13 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"夏季种子","color":"gold","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 13 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"秋季种子","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 13 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"冬季种子","color":"aqua","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 13 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"树木种子","color":"dark_green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 13 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 13 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭","color":"red","bold":true}'

# 15.6. 工具子菜单文字 (level=14)
execute if score #MenuLevel sd_menu_ctrl matches 14 if score @s sd_menu_slot matches 1 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"斧子","color":"brown","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 14 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"锄头","color":"brown","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 14 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"镐子","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 14 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"镰刀","color":"gold","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 14 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"水壶","color":"aqua","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 14 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"武器(占位)","color":"dark_gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 14 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"防具(占位)","color":"dark_gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 14 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 14 if score @s sd_menu_slot matches 9 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭","color":"red","bold":true}'

# 16. 春/夏/秋/冬季作物品质选择文字 (level=111-114)
execute if score #MenuLevel sd_menu_ctrl matches 111..114 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"普通","color":"white","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 111..114 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"银星","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 111..114 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"金星","color":"gold","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 111..114 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"钻石星","color":"aqua","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 111..114 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 111..114 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭","color":"red","bold":true}'

# 17. 合成菜单文字 (level=30)
execute if score #MenuLevel sd_menu_ctrl matches 30 if score @s sd_menu_slot matches 1 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"工具","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 30 if score @s sd_menu_slot matches 2 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"设备","color":"gold","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 30 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"建筑","color":"brown","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 30 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"消耗品","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 30 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"家具","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 30 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 30 if score @s sd_menu_slot matches 9 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 18. 设备配方菜单 (level=302)
# 先清空描述文本(默认隐藏)
execute if score #MenuLevel sd_menu_ctrl matches 302 as @e[tag=sd_menu_text,tag=sd_text_desc] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":""}'

# 检查玩家是否解锁了熔炉配方 (stardew.recipe.201)
# 获取当前操作菜单的玩家
execute store result score #MenuPlayer sd_menu_ctrl run scoreboard players get @a[scores={sd_menu_sequence=1..},limit=1] sd_menu_sequence
# 只有当玩家解锁了配方时，才显示熔炉标题和材料
execute if score #MenuLevel sd_menu_ctrl matches 302 if score @s sd_menu_slot matches 1 as @a[scores={sd_menu_sequence=1..},limit=1] if score @s stardew.recipe.201 matches 1.. as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"熔炉","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 302 if score @s sd_menu_slot matches 1 as @a[scores={sd_menu_sequence=1..},limit=1] if score @s stardew.recipe.201 matches 1.. as @e[tag=sd_menu_text,tag=sd_text_desc] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"石头×25 铜矿石×20","color":"yellow"}'

# 如果未解锁，显示"???"
execute if score #MenuLevel sd_menu_ctrl matches 302 if score @s sd_menu_slot matches 1 as @a[scores={sd_menu_sequence=1..},limit=1] unless score @s stardew.recipe.201 matches 1.. as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"???","color":"gray","bold":true}'

# 检查玩家是否解锁了箱子配方 (stardew.recipe.202)
# 只有当玩家解锁了配方时，才显示箱子标题和材料
execute if score #MenuLevel sd_menu_ctrl matches 302 if score @s sd_menu_slot matches 2 as @a[scores={sd_menu_sequence=1..},limit=1] if score @s stardew.recipe.202 matches 1.. as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"箱子","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 302 if score @s sd_menu_slot matches 2 as @a[scores={sd_menu_sequence=1..},limit=1] if score @s stardew.recipe.202 matches 1.. as @e[tag=sd_menu_text,tag=sd_text_desc] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"木材×50","color":"yellow"}'

# 如果未解锁，显示"???"
execute if score #MenuLevel sd_menu_ctrl matches 302 if score @s sd_menu_slot matches 2 as @a[scores={sd_menu_sequence=1..},limit=1] unless score @s stardew.recipe.202 matches 1.. as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"???","color":"gray","bold":true}'

# 检查玩家是否解锁了小桶配方 (stardew.recipe.203)
# 只有当玩家解锁了配方时，才显示小桶标题和材料
execute if score #MenuLevel sd_menu_ctrl matches 302 if score @s sd_menu_slot matches 3 as @a[scores={sd_menu_sequence=1..},limit=1] if score @s stardew.recipe.203 matches 1.. as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"小桶","color":"gray","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 302 if score @s sd_menu_slot matches 3 as @a[scores={sd_menu_sequence=1..},limit=1] if score @s stardew.recipe.203 matches 1.. as @e[tag=sd_menu_text,tag=sd_text_desc] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"木材×30 铜锭×1 铁锭×1 橡树树脂×1","color":"yellow"}'

# 如果未解锁，显示"???"
execute if score #MenuLevel sd_menu_ctrl matches 302 if score @s sd_menu_slot matches 3 as @a[scores={sd_menu_sequence=1..},limit=1] unless score @s stardew.recipe.203 matches 1.. as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"???","color":"gray","bold":true}'

execute if score #MenuLevel sd_menu_ctrl matches 302 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 302 if score @s sd_menu_slot matches 9 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 19. 存储系统菜单文字

# 背包列表菜单 (level=26)
# 检查当前槽位是否已解锁
execute if score #MenuLevel sd_menu_ctrl matches 26 if score @s sd_menu_slot matches 1..7 run function stardew:menu/storage/hover/check_bag_unlock
execute if score #MenuLevel sd_menu_ctrl matches 26 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 26 if score @s sd_menu_slot matches 9 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 背包详情菜单 (level=261): slot 2, 8-9 隐藏, slot 1 显示当前背包名
execute if score #MenuLevel sd_menu_ctrl matches 261 if score @s sd_menu_slot matches 1 run function stardew:menu/storage/hover/show_selected_bag_name
execute if score #MenuLevel sd_menu_ctrl matches 261 if score @s sd_menu_slot matches 2 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 261 if score @s sd_menu_slot matches 8 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 261 if score @s sd_menu_slot matches 9 run return 0

execute if score #MenuLevel sd_menu_ctrl matches 261 if score @s sd_menu_slot matches 3 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"打开背包","color":"green","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 261 if score @s sd_menu_slot matches 4 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"重命名","color":"yellow","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 261 if score @s sd_menu_slot matches 5 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"更换颜色","color":"light_purple","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 261 if score @s sd_menu_slot matches 6 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 261 if score @s sd_menu_slot matches 7 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 颜色选择菜单 (level=262): 第2页slot 4-7 隐藏
execute store result score #MenuPage sd_menu_ctrl run scoreboard players get @a[scores={sd_menu_sequence=1..},limit=1] sd_menu_page
execute if score #MenuLevel sd_menu_ctrl matches 262 if score #MenuPage sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 4 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 262 if score #MenuPage sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 5 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 262 if score #MenuPage sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 6 run return 0
execute if score #MenuLevel sd_menu_ctrl matches 262 if score #MenuPage sd_menu_ctrl matches 2 if score @s sd_menu_slot matches 7 run return 0

execute if score #MenuLevel sd_menu_ctrl matches 262 if score @s sd_menu_slot matches 1..7 run function stardew:menu/storage/hover/show_color_name
execute if score #MenuLevel sd_menu_ctrl matches 262 if score @s sd_menu_slot matches 8 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"返回","color":"red","bold":true}'
execute if score #MenuLevel sd_menu_ctrl matches 262 if score @s sd_menu_slot matches 9 as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"关闭菜单","color":"red","bold":true}'

# 99. 翻页按钮文字 (所有级别通用)
execute if score @s[tag=sd_page_prev] sd_menu_entity_num matches 1.. as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"上一页","color":"blue","bold":true}'
execute if score @s[tag=sd_page_next] sd_menu_entity_num matches 1.. as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"下一页","color":"blue","bold":true}'
