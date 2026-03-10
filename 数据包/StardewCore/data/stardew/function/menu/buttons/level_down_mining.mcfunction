# data/stardew/function/menu/buttons/level_down_mining.mcfunction
# 降级挖矿等级
# 执行者: 玩家 (@s)

# 1. 减少等级
scoreboard players remove @s sd_mining_lvl 1

# 2. 限制最小等级为0
execute if score @s sd_mining_lvl matches ..-1 run scoreboard players set @s sd_mining_lvl 0

# 3. 获取当前等级并发送消息
execute store result score #CurrentLevel sd_menu_ctrl run scoreboard players get @s sd_mining_lvl
tellraw @s [{"text":"⛏ ","color":"gray"},{"text":"挖矿等级已降级至 ","color":"gray"},{"score":{"name":"#CurrentLevel","objective":"sd_menu_ctrl"},"color":"white"},{"text":" 级","color":"gray"}]

# 4. 播放降级音效
playsound block.anvil.land player @s ~ ~ ~ 0.5 0.8
