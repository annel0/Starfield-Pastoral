# data/stardew/function/menu/buttons/level_up_mining.mcfunction
# 升级挖矿等级
# 执行者: 玩家 (@s)

# 1. 增加等级
scoreboard players add @s sd_mining_lvl 1

# 2. 限制最大等级为10
execute if score @s sd_mining_lvl matches 11.. run scoreboard players set @s sd_mining_lvl 10

# 3. 获取当前等级并发送消息
execute store result score #CurrentLevel sd_menu_ctrl run scoreboard players get @s sd_mining_lvl
tellraw @s [{"text":"⛏ ","color":"gray"},{"text":"挖矿等级已升级至 ","color":"yellow"},{"score":{"name":"#CurrentLevel","objective":"sd_menu_ctrl"},"color":"gold","bold":true},{"text":" 级","color":"yellow"}]

# 4. 播放升级音效
playsound entity.player.levelup player @s ~ ~ ~ 1 1.2
