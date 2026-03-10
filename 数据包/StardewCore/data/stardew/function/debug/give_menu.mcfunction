# ===================================================
# 星露谷调试菜单 - 物品给予
# 使用方法: /function stardew:debug/give_menu
# ===================================================

tellraw @s ["",{"text":"\n========== ","color":"gold","bold":true},{"text":"星露谷物品给予菜单","color":"yellow","bold":true},{"text":" ==========\n","color":"gold","bold":true}]

tellraw @s [{"text":"[全部物品] ","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:debug/give_all_items"},"hoverEvent":{"action":"show_text","contents":"给予所有 388 个物品"}},{"text":"(388个)","color":"gray"}]

tellraw @s [{"text":"[Crops / Fall] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_crops_fall"},"hoverEvent":{"action":"show_text","contents":"给予 40 个物品"}},{"text":"(40个)","color":"gray"}]
tellraw @s [{"text":"[Crops / Spring] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_crops_spring"},"hoverEvent":{"action":"show_text","contents":"给予 40 个物品"}},{"text":"(40个)","color":"gray"}]
tellraw @s [{"text":"[Crops / Summer] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_crops_summer"},"hoverEvent":{"action":"show_text","contents":"给予 48 个物品"}},{"text":"(48个)","color":"gray"}]
tellraw @s [{"text":"[Crops / Winter] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_crops_winter"},"hoverEvent":{"action":"show_text","contents":"给予 16 个物品"}},{"text":"(16个)","color":"gray"}]
tellraw @s [{"text":"[Debug] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_debug"},"hoverEvent":{"action":"show_text","contents":"给予 4 个物品"}},{"text":"(4个)","color":"gray"}]
tellraw @s [{"text":"[Fish / Fall] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_fish_fall"},"hoverEvent":{"action":"show_text","contents":"给予 36 个物品"}},{"text":"(36个)","color":"gray"}]
tellraw @s [{"text":"[Fish / Resource] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_fish_resource"},"hoverEvent":{"action":"show_text","contents":"给予 3 个物品"}},{"text":"(3个)","color":"gray"}]
tellraw @s [{"text":"[Fish / Special] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_fish_special"},"hoverEvent":{"action":"show_text","contents":"给予 24 个物品"}},{"text":"(24个)","color":"gray"}]
tellraw @s [{"text":"[Fish / Spring] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_fish_spring"},"hoverEvent":{"action":"show_text","contents":"给予 40 个物品"}},{"text":"(40个)","color":"gray"}]
tellraw @s [{"text":"[Fish / Summer] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_fish_summer"},"hoverEvent":{"action":"show_text","contents":"给予 44 个物品"}},{"text":"(44个)","color":"gray"}]
tellraw @s [{"text":"[Fish / Trash] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_fish_trash"},"hoverEvent":{"action":"show_text","contents":"给予 6 个物品"}},{"text":"(6个)","color":"gray"}]
tellraw @s [{"text":"[Fish / Winter] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_fish_winter"},"hoverEvent":{"action":"show_text","contents":"给予 20 个物品"}},{"text":"(20个)","color":"gray"}]
tellraw @s [{"text":"[Fishing] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_fishing"},"hoverEvent":{"action":"show_text","contents":"给予 11 个物品"}},{"text":"(11个)","color":"gray"}]
tellraw @s [{"text":"[Resource] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_resource"},"hoverEvent":{"action":"show_text","contents":"给予 2 个物品"}},{"text":"(2个)","color":"gray"}]
tellraw @s [{"text":"[Seeds] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_seeds"},"hoverEvent":{"action":"show_text","contents":"给予 38 个物品"}},{"text":"(38个)","color":"gray"}]
tellraw @s [{"text":"[Tools] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:debug/give_tools"},"hoverEvent":{"action":"show_text","contents":"给予 16 个物品"}},{"text":"(16个)","color":"gray"}]

tellraw @s {"text":"====================================","color":"gold","bold":true}