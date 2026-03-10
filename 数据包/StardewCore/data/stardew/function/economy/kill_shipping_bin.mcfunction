# data/stardew/functions/debug/kill_shipping_bin.mcfunction
# 清除附近所有“卖货箱” prefab（箱子+交互）

# 杀交互体
kill @e[type=interaction,tag=sd_shipping_root,distance=..32]
# 杀箱子显示体
kill @e[type=block_display,tag=sd_shipping_root,distance=..32]

tellraw @s ["",{"text":"[DEBUG] ","color":"yellow"},{"text":"已清理附近卖货箱。","color":"red"}]