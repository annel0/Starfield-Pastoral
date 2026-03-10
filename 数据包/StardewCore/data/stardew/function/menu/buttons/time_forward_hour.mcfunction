# data/stardew/function/menu/buttons/time_forward_hour.mcfunction
# 快进一小时（60分钟）
# 执行者: 玩家 (@s)

# 1. 增加60分钟
scoreboard players add Global sd_time 60

# 2. 同步游戏时间（60分钟 = 60 * 16 = 960 ticks）
time add 960

# 3. 检查是否到达过夜时间（1560分钟 = 凌晨2点）
execute if score Global sd_time matches 1560.. run function stardew:time/new_day

# 4. 计算当前小时和分钟
scoreboard players operation #CurrentHour sd_menu_ctrl = Global sd_time
scoreboard players operation #CurrentHour sd_menu_ctrl /= #60 sd_const
scoreboard players operation #CurrentMin sd_menu_ctrl = Global sd_time
scoreboard players operation #CurrentMin sd_menu_ctrl %= #60 sd_const

# 5. 处理24小时制显示
execute if score #CurrentHour sd_menu_ctrl matches 24.. run scoreboard players remove #CurrentHour sd_menu_ctrl 24

# 6. 发送消息
tellraw @s [{"text":"⏰ ","color":"aqua"},{"text":"时间快进一小时！现在是 ","color":"yellow"},{"score":{"name":"#CurrentHour","objective":"sd_menu_ctrl"},"color":"gold","bold":true},{"text":":"},{"score":{"name":"#CurrentMin","objective":"sd_menu_ctrl"},"color":"gold"}]

# 7. 播放音效
playsound minecraft:block.bell.use player @s ~ ~ ~ 1.0 2.0
