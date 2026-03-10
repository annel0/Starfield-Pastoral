# stardew:mine/elevator/show_menu.mcfunction
# 显示电梯楼层选择菜单 (根据解锁进度显示)
# 执行者: 玩家 (@s)
# 需要记分板: sd_mine_deepest (记录玩家到过的最深层数)

# 播放电梯打开音效
playsound minecraft:block.iron_door.open master @s
playsound minecraft:ui.button.click master @s

tellraw @s {"text":"","extra":[{"text":"========== ","color":"gold","bold":true},{"text":"矿井电梯","color":"yellow","bold":true},{"text":" ==========","color":"gold","bold":true}]}
tellraw @s {"text":""}

# 0层 (地表，永远可用)
tellraw @s {"text":"  [ 第 0 层 - 地表 ]  ","color":"aqua","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_0"},"hoverEvent":{"action":"show_text","contents":"返回地表"}}

# 5-100层 (根据 sd_mine_deepest 显示)
execute if score @s sd_mine_deepest matches 5.. run tellraw @s {"text":"  [ 第 5 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_5"}}
execute if score @s sd_mine_deepest matches 10.. run tellraw @s {"text":"  [ 第 10 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_10"}}
execute if score @s sd_mine_deepest matches 15.. run tellraw @s {"text":"  [ 第 15 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_15"}}
execute if score @s sd_mine_deepest matches 20.. run tellraw @s {"text":"  [ 第 20 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_20"}}
execute if score @s sd_mine_deepest matches 25.. run tellraw @s {"text":"  [ 第 25 层 ]  ","color":"gold","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_25"},"hoverEvent":{"action":"show_text","contents":"宝箱层"}}
execute if score @s sd_mine_deepest matches 30.. run tellraw @s {"text":"  [ 第 30 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_30"}}
execute if score @s sd_mine_deepest matches 35.. run tellraw @s {"text":"  [ 第 35 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_35"}}
execute if score @s sd_mine_deepest matches 40.. run tellraw @s {"text":"  [ 第 40 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_40"}}
execute if score @s sd_mine_deepest matches 45.. run tellraw @s {"text":"  [ 第 45 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_45"}}
execute if score @s sd_mine_deepest matches 50.. run tellraw @s {"text":"  [ 第 50 层 ]  ","color":"gold","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_50"},"hoverEvent":{"action":"show_text","contents":"宝箱层"}}
execute if score @s sd_mine_deepest matches 55.. run tellraw @s {"text":"  [ 第 55 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_55"}}
execute if score @s sd_mine_deepest matches 60.. run tellraw @s {"text":"  [ 第 60 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_60"}}
execute if score @s sd_mine_deepest matches 65.. run tellraw @s {"text":"  [ 第 65 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_65"}}
execute if score @s sd_mine_deepest matches 70.. run tellraw @s {"text":"  [ 第 70 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_70"}}
execute if score @s sd_mine_deepest matches 75.. run tellraw @s {"text":"  [ 第 75 层 ]  ","color":"gold","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_75"},"hoverEvent":{"action":"show_text","contents":"宝箱层"}}
execute if score @s sd_mine_deepest matches 80.. run tellraw @s {"text":"  [ 第 80 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_80"}}
execute if score @s sd_mine_deepest matches 85.. run tellraw @s {"text":"  [ 第 85 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_85"}}
execute if score @s sd_mine_deepest matches 90.. run tellraw @s {"text":"  [ 第 90 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_90"}}
execute if score @s sd_mine_deepest matches 95.. run tellraw @s {"text":"  [ 第 95 层 ]  ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_95"}}
execute if score @s sd_mine_deepest matches 100.. run tellraw @s {"text":"  [ 第 100 层 - 最深处 ]  ","color":"dark_red","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_100"},"hoverEvent":{"action":"show_text","contents":"宝箱层 - 矿井最深处"}}

tellraw @s {"text":""}
tellraw @s {"text":"============================","color":"gold","bold":true}
