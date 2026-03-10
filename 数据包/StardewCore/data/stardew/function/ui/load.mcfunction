# data/stardew/function/ui/load.mcfunction

# 1. 重置计分板 (防止报错，确保干净)
scoreboard objectives remove sd_sidebar
scoreboard objectives add sd_sidebar dummy {"text":"StardewCraft","color":"gold","bold":true}

# 2. 【核心修复】全局隐藏侧边栏数字 (1.21+ 专用)
scoreboard objectives modify sd_sidebar numberformat blank

# 3. 设置显示位置
scoreboard objectives setdisplay sidebar sd_sidebar

# 4. 创建占位符 (显示时间、日期、天气和金币)
scoreboard players set 时间 sd_sidebar 1
scoreboard players set 日期 sd_sidebar 2
scoreboard players set 天气 sd_sidebar 3
scoreboard players set 金币 sd_sidebar 4
scoreboard players set DPS sd_sidebar 5

# 4.5 设置金币显示在玩家列表下方
scoreboard objectives modify sd_gold displayname [{"text":"💰 ","color":"gold"},{"text":"金币","color":"yellow"}]
scoreboard objectives setdisplay list sd_gold

# 5. 创建队伍 (用于显示彩色字)
team remove sd_ui_1
team remove sd_ui_2
team remove sd_ui_3
team remove sd_ui_4
team remove sd_ui_5

team add sd_ui_1
team add sd_ui_2
team add sd_ui_3
team add sd_ui_4
team add sd_ui_5

# 6. 把占位符加入队伍，设置颜色和格式
team join sd_ui_2 时间
team modify sd_ui_2 color gray
team modify sd_ui_2 prefix ""
team modify sd_ui_2 suffix [{"text":"  🕐 ","color":"gray"},{"text":"加载中...","color":"white"}]

team join sd_ui_1 日期
team modify sd_ui_1 color gray
team modify sd_ui_1 prefix ""
team modify sd_ui_1 suffix [{"text":"  📅 ","color":"gray"},{"text":"加载中...","color":"white"}]

team join sd_ui_3 天气
team modify sd_ui_3 color gray
team modify sd_ui_3 prefix ""
team modify sd_ui_3 suffix [{"text":"  🌤 ","color":"gray"},{"text":"加载中...","color":"white"}]

team join sd_ui_4 金币
team modify sd_ui_4 color gray
team modify sd_ui_4 prefix ""
team modify sd_ui_4 suffix [{"text":"  💰 ","color":"gray"},{"text":"加载中...","color":"white"}]

team join sd_ui_5 DPS
team modify sd_ui_5 color gray
team modify sd_ui_5 prefix ""
team modify sd_ui_5 suffix [{"text":"  ⚔ ","color":"gray"},{"text":"未激活","color":"dark_gray"}]

# 7. 静态数据
data modify storage stardew:ui Seasons set value {s1:{text:"春",color:"green"},s2:{text:"夏",color:"red"},s3:{text:"秋",color:"gold"},s4:{text:"冬",color:"white"}}
data modify storage stardew:ui TimeColors set value {t0:"white",t1:"yellow",t2:"red"}