# data/stardew/function/menu/buttons/level_down_farming.mcfunction
# 降级耕种等级
# 执行者: 玩家 (@s)

# 1. 减少等级
scoreboard players remove @s sd_farming_lvl 1

# 2. 限制最小等级为0
execute if score @s sd_farming_lvl matches ..-1 run scoreboard players set @s sd_farming_lvl 0

# 3. 获取当前等级并发送消息
execute store result score #CurrentLevel sd_menu_ctrl run scoreboard players get @s sd_farming_lvl
tellraw @s [{"text":"🌾 ","color":"green"},{"text":"耕种等级已降级至 ","color":"gray"},{"score":{"name":"#CurrentLevel","objective":"sd_menu_ctrl"},"color":"white"},{"text":" 级","color":"gray"}]

# 4. 播放降级音效
playsound block.anvil.land player @s ~ ~ ~ 0.5 0.8
