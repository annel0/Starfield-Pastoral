# stardew:mine/ui/show_floor_title.mcfunction
# 显示当前层数标题
# 执行者: 玩家 (@s)

# 获取层数
execute store result score #show_floor sd_mine_temp run scoreboard players get @s sd_mine_floor

# 根据层数显示不同主题颜色
execute if score #show_floor sd_mine_temp matches 0 run title @s subtitle {"text":"矿洞入口","color":"gray"}
execute if score #show_floor sd_mine_temp matches 1..25 run title @s subtitle {"text":"普通矿洞","color":"gray"}
execute if score #show_floor sd_mine_temp matches 26..50 run title @s subtitle {"text":"冰川矿洞","color":"aqua"}
execute if score #show_floor sd_mine_temp matches 51..75 run title @s subtitle {"text":"暗黑矿洞","color":"dark_purple"}
execute if score #show_floor sd_mine_temp matches 76..100 run title @s subtitle {"text":"熔岩矿洞","color":"red"}

# 宝箱层特殊显示
execute if score #show_floor sd_mine_temp matches 25 run title @s subtitle {"text":"⭐ 宝箱层 ⭐","color":"gold"}
execute if score #show_floor sd_mine_temp matches 50 run title @s subtitle {"text":"⭐ 宝箱层 ⭐","color":"gold"}
execute if score #show_floor sd_mine_temp matches 75 run title @s subtitle {"text":"⭐ 宝箱层 ⭐","color":"gold"}
execute if score #show_floor sd_mine_temp matches 100 run title @s subtitle {"text":"⭐ 最终宝箱层 ⭐","color":"gold"}

# 显示层数
title @s title [{"text":"第 ","color":"white"},{"score":{"name":"@s","objective":"sd_mine_floor"},"color":"gold"},{"text":" 层","color":"white"}]
