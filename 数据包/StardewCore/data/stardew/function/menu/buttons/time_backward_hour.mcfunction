# data/stardew/function/menu/buttons/time_backward_hour.mcfunction
# 倒退一小时（60分钟）
# 执行者: 玩家 (@s)

# 1. 减少60分钟
scoreboard players remove Global sd_time 60

# 2. 同步游戏时间（-60分钟 = -960 ticks，需要减去）
# Minecraft的time命令不支持负数，所以我们需要特殊处理
# 1天 = 24000 ticks，减960 ticks = 加 23040 ticks
time add 23040

# 3. 限制最小时间为360（早上6:00）
execute if score Global sd_time matches ..359 run scoreboard players set Global sd_time 360
execute if score Global sd_time matches ..359 run time set 0

# 4. 计算当前小时和分钟
scoreboard players operation #CurrentHour sd_menu_ctrl = Global sd_time
scoreboard players operation #CurrentHour sd_menu_ctrl /= #60 sd_const
scoreboard players operation #CurrentMin sd_menu_ctrl = Global sd_time
scoreboard players operation #CurrentMin sd_menu_ctrl %= #60 sd_const

# 5. 处理24小时制显示
execute if score #CurrentHour sd_menu_ctrl matches 24.. run scoreboard players remove #CurrentHour sd_menu_ctrl 24

# 6. 发送消息
tellraw @s [{"text":"⏰ ","color":"gray"},{"text":"时间倒退一小时。现在是 ","color":"gray"},{"score":{"name":"#CurrentHour","objective":"sd_menu_ctrl"},"color":"white"},{"text":":"},{"score":{"name":"#CurrentMin","objective":"sd_menu_ctrl"},"color":"white"}]

# 7. 播放音效
playsound minecraft:block.bell.use player @s ~ ~ ~ 1.0 0.5
