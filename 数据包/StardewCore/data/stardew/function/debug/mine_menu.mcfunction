# stardew:debug/mine_menu.mcfunction
# 矿洞测试菜单
# 使用方法: /function stardew:debug/mine_menu

tellraw @s {"text":"\n","color":"white"}
tellraw @s {"text":"========================================","color":"aqua","bold":true}
tellraw @s {"text":"      矿洞系统 - 测试菜单","color":"gold","bold":true}
tellraw @s {"text":"========================================","color":"aqua","bold":true}
tellraw @s {"text":""}

tellraw @s [{"text":"[","color":"gray"},{"text":"进入矿洞","color":"green","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:debug/summon_mine_entrance"},"hoverEvent":{"action":"show_text","contents":"传送到矿洞入口（第0层）"}},{"text":"]","color":"gray"}]

tellraw @s [{"text":"[","color":"gray"},{"text":"解锁全部楼层","color":"yellow","clickEvent":{"action":"run_command","value":"/function stardew:debug/unlock_all_floors"},"hoverEvent":{"action":"show_text","contents":"解锁1-100层，电梯可前往任意楼层"}},{"text":"]","color":"gray"}]

tellraw @s {"text":""}
tellraw @s {"text":"快速测试主题:","color":"white","bold":true}

tellraw @s [{"text":"[","color":"gray"},{"text":"Theme1 (1-25层)","color":"yellow","clickEvent":{"action":"run_command","value":"/data modify storage stardew:mine target_floor set value 10"},"hoverEvent":{"action":"show_text","contents":"点击后执行: /function stardew:mine/enter/to_floor"}},{"text":"]","color":"gray"}]

tellraw @s [{"text":"[","color":"gray"},{"text":"Theme2 (26-50层)","color":"aqua","clickEvent":{"action":"run_command","value":"/data modify storage stardew:mine target_floor set value 35"},"hoverEvent":{"action":"show_text","contents":"冰川主题 - 泪晶矿"}},{"text":"]","color":"gray"}]

tellraw @s [{"text":"[","color":"gray"},{"text":"Theme3 (51-75层)","color":"gold","clickEvent":{"action":"run_command","value":"/function stardew:debug/test_theme3"},"hoverEvent":{"action":"show_text","contents":"金矿10%, 翡翠2%, 红宝石1%"}},{"text":"]","color":"gray"}]

tellraw @s [{"text":"[","color":"gray"},{"text":"Theme4 (76-100层)","color":"light_purple","clickEvent":{"action":"run_command","value":"/function stardew:debug/test_theme4"},"hoverEvent":{"action":"show_text","contents":"钻石8%, 五彩碎片0.2%"}},{"text":"]","color":"gray"}]

tellraw @s {"text":""}
tellraw @s {"text":"宝箱层:","color":"white","bold":true}

tellraw @s [{"text":"[","color":"gray"},{"text":"第25层 ⭐","color":"gold","clickEvent":{"action":"run_command","value":"/data modify storage stardew:mine target_floor set value 25"},"hoverEvent":{"action":"show_text","contents":"Theme1 宝箱层"}},{"text":"]","color":"gray"},{"text":" "},{"text":"[","color":"gray"},{"text":"第50层 ⭐","color":"gold","clickEvent":{"action":"run_command","value":"/data modify storage stardew:mine target_floor set value 50"},"hoverEvent":{"action":"show_text","contents":"Theme2 宝箱层"}},{"text":"]","color":"gray"}]

tellraw @s [{"text":"[","color":"gray"},{"text":"第75层 ⭐","color":"gold","clickEvent":{"action":"run_command","value":"/data modify storage stardew:mine target_floor set value 75"},"hoverEvent":{"action":"show_text","contents":"Theme3 宝箱层"}},{"text":"]","color":"gray"},{"text":" "},{"text":"[","color":"gray"},{"text":"第100层 ⭐⭐","color":"gold","bold":true,"clickEvent":{"action":"run_command","value":"/data modify storage stardew:mine target_floor set value 100"},"hoverEvent":{"action":"show_text","contents":"矿洞最深处！"}},{"text":"]","color":"gray"}]

tellraw @s {"text":""}
tellraw @s {"text":"注意: 点击宝箱层后需要手动执行:","color":"gray","italic":true}
tellraw @s {"text":"/function stardew:mine/enter/to_floor","color":"gray","italic":true}

tellraw @s {"text":"========================================","color":"aqua","bold":true}
tellraw @s {"text":""}
