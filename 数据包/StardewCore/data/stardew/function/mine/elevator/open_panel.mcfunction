# stardew:mine/elevator/open_panel.mcfunction
# 打开电梯面板
# 执行者: 玩家 (@s)

# 播放音效
playsound minecraft:block.iron_door.open master @s ~ ~ ~ 1 1.2

# 获取玩家最深到达层
execute store result score #deepest sd_mine_temp run scoreboard players get @s sd_mine_deepest

# 显示可选楼层 (简易版 - 用 tellraw)
tellraw @s {"text":"\n========= 矿洞电梯 =========","color":"aqua","bold":true}
tellraw @s {"text":"点击楼层传送:","color":"gray"}

# 0 层 (始终可用)
tellraw @s [{"text":"[","color":"gray"},{"text":" 0层 - 入口 ","color":"green","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_0"},"hoverEvent":{"action":"show_text","contents":"点击传送到入口"}},{"text":"]","color":"gray"}]

# 5 层
execute if score #deepest sd_mine_temp matches 5.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 5层 ","color":"yellow","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_5"},"hoverEvent":{"action":"show_text","contents":"点击传送到第5层"}},{"text":"]","color":"gray"}]

# 10 层
execute if score #deepest sd_mine_temp matches 10.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 10层 ","color":"yellow","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_10"},"hoverEvent":{"action":"show_text","contents":"点击传送到第10层"}},{"text":"]","color":"gray"}]

# 15 层
execute if score #deepest sd_mine_temp matches 15.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 15层 ","color":"yellow","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_15"},"hoverEvent":{"action":"show_text","contents":"点击传送到第15层"}},{"text":"]","color":"gray"}]

# 20 层
execute if score #deepest sd_mine_temp matches 20.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 20层 ","color":"yellow","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_20"},"hoverEvent":{"action":"show_text","contents":"点击传送到第20层"}},{"text":"]","color":"gray"}]

# 25 层 (宝箱层)
execute if score #deepest sd_mine_temp matches 25.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 25层 ⭐","color":"gold","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_25"},"hoverEvent":{"action":"show_text","contents":"宝箱层！点击传送"}},{"text":"]","color":"gray"}]

# 30 层
execute if score #deepest sd_mine_temp matches 30.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 30层 ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_30"},"hoverEvent":{"action":"show_text","contents":"点击传送到第30层"}},{"text":"]","color":"gray"}]

# 35 层
execute if score #deepest sd_mine_temp matches 35.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 35层 ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_35"},"hoverEvent":{"action":"show_text","contents":"点击传送到第35层"}},{"text":"]","color":"gray"}]

# 40 层
execute if score #deepest sd_mine_temp matches 40.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 40层 ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_40"},"hoverEvent":{"action":"show_text","contents":"点击传送到第40层"}},{"text":"]","color":"gray"}]

# 45 层
execute if score #deepest sd_mine_temp matches 45.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 45层 ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_45"},"hoverEvent":{"action":"show_text","contents":"点击传送到第45层"}},{"text":"]","color":"gray"}]

# 50 层 (宝箱层)
execute if score #deepest sd_mine_temp matches 50.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 50层 ⭐","color":"gold","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_50"},"hoverEvent":{"action":"show_text","contents":"宝箱层！点击传送"}},{"text":"]","color":"gray"}]

# 55 层
execute if score #deepest sd_mine_temp matches 55.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 55层 ","color":"gold","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_55"},"hoverEvent":{"action":"show_text","contents":"点击传送到第55层"}},{"text":"]","color":"gray"}]

# 60 层
execute if score #deepest sd_mine_temp matches 60.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 60层 ","color":"gold","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_60"},"hoverEvent":{"action":"show_text","contents":"点击传送到第60层"}},{"text":"]","color":"gray"}]

# 65 层
execute if score #deepest sd_mine_temp matches 65.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 65层 ","color":"gold","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_65"},"hoverEvent":{"action":"show_text","contents":"点击传送到第65层"}},{"text":"]","color":"gray"}]

# 70 层
execute if score #deepest sd_mine_temp matches 70.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 70层 ","color":"gold","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_70"},"hoverEvent":{"action":"show_text","contents":"点击传送到第70层"}},{"text":"]","color":"gray"}]

# 75 层 (宝箱层)
execute if score #deepest sd_mine_temp matches 75.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 75层 ⭐","color":"gold","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_75"},"hoverEvent":{"action":"show_text","contents":"宝箱层！点击传送"}},{"text":"]","color":"gray"}]

# 80 层
execute if score #deepest sd_mine_temp matches 80.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 80层 ","color":"light_purple","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_80"},"hoverEvent":{"action":"show_text","contents":"点击传送到第80层"}},{"text":"]","color":"gray"}]

# 85 层
execute if score #deepest sd_mine_temp matches 85.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 85层 ","color":"light_purple","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_85"},"hoverEvent":{"action":"show_text","contents":"点击传送到第85层"}},{"text":"]","color":"gray"}]

# 90 层
execute if score #deepest sd_mine_temp matches 90.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 90层 ","color":"light_purple","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_90"},"hoverEvent":{"action":"show_text","contents":"点击传送到第90层"}},{"text":"]","color":"gray"}]

# 95 层
execute if score #deepest sd_mine_temp matches 95.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 95层 ","color":"light_purple","clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_95"},"hoverEvent":{"action":"show_text","contents":"点击传送到第95层"}},{"text":"]","color":"gray"}]

# 100 层 (最终宝箱层 - 矿洞最深处)
execute if score #deepest sd_mine_temp matches 100.. run tellraw @s [{"text":"[","color":"gray"},{"text":" 100层 ⭐⭐","color":"gold","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:mine/elevator/goto_100"},"hoverEvent":{"action":"show_text","contents":"矿洞最深处！点击传送"}},{"text":"]","color":"gray"}]

# 显示未解锁的层数提示
execute unless score #deepest sd_mine_temp matches 5.. run tellraw @s {"text":"(到达更深层解锁更多楼层)","color":"dark_gray","italic":true}

tellraw @s {"text":"================================\n","color":"aqua"}
