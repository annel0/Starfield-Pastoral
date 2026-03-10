# stardew:mine/ore/spawn_random_ore.mcfunction
# 根据当前主题和层数随机生成矿石
# 执行位置: 要生成矿石的位置

# 获取当前层数计算主题
execute store result score #current_floor sd_mine_temp run scoreboard players get @s sd_mine_floor

# 确定主题 (1-25: 主题1, 26-50: 主题2, 等等)
scoreboard players set #theme sd_mine_temp 1
execute if score #current_floor sd_mine_temp matches 26..50 run scoreboard players set #theme sd_mine_temp 2
execute if score #current_floor sd_mine_temp matches 51..75 run scoreboard players set #theme sd_mine_temp 3
execute if score #current_floor sd_mine_temp matches 76..100 run scoreboard players set #theme sd_mine_temp 4

# 根据主题调用对应的概率表
execute if score #theme sd_mine_temp matches 1 run function stardew:mine/ore/roll_theme1
execute if score #theme sd_mine_temp matches 2 run function stardew:mine/ore/roll_theme2
execute if score #theme sd_mine_temp matches 3 run function stardew:mine/ore/roll_theme3
execute if score #theme sd_mine_temp matches 4 run function stardew:mine/ore/roll_theme4
