# data/stardew/function/menu/buttons/toggle_gold_display.mcfunction
# 切换侧边栏金币显示
# 执行者: 玩家 (@s)

# 初始化金币显示状态（如果不存在，默认开启）
execute unless score @s sd_show_gold matches 0.. run scoreboard players set @s sd_show_gold 1

# 存储当前状态到临时变量
scoreboard players operation @s sd_temp = @s sd_show_gold

# 切换状态（0变1，1变0）
execute if score @s sd_temp matches 0 run scoreboard players set @s sd_show_gold 1
execute if score @s sd_temp matches 1 run scoreboard players set @s sd_show_gold 0

# 如果开启显示
execute if score @s sd_show_gold matches 1 run tellraw @s [{"text":"[设置] ","color":"gold","bold":true},{"text":"已开启金币显示","color":"green"},{"text":"\n当前仅支持单人档显示，多人档请按","color":"gray"},{"text":" Tab ","color":"yellow","bold":true},{"text":"查看","color":"gray"}]
execute if score @s sd_show_gold matches 1 run playsound minecraft:block.note_block.pling player @s ~ ~ ~ 1 1.5

# 如果关闭显示
execute if score @s sd_show_gold matches 0 run tellraw @s [{"text":"[设置] ","color":"gold","bold":true},{"text":"已关闭金币显示","color":"gray"}]
execute if score @s sd_show_gold matches 0 run playsound minecraft:block.note_block.bass player @s ~ ~ ~ 1 0.8
