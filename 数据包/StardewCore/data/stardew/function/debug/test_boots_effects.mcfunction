# ====================================
# 靴子效果测试命令
# ====================================
# 使用方法：/function stardew:debug/test_boots_effects

tellraw @s {"text":"=== 靴子效果测试系统 ===","color":"gold","bold":true}
tellraw @s {"text":""}

# 测试选项
tellraw @s [{"text":"[测试1] ","color":"yellow","bold":true},{"text":"设置测试靴子(defense=5, immunity=3)","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/test_boots_set"},"hoverEvent":{"action":"show_text","value":"点击设置测试靴子数据"}}]

tellraw @s [{"text":"[测试2] ","color":"yellow","bold":true},{"text":"查看当前靴子数据","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/test_boots_check"},"hoverEvent":{"action":"show_text","value":"查看storage中的靴子数据"}}]

tellraw @s [{"text":"[测试3] ","color":"yellow","bold":true},{"text":"受到10点测试伤害","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/test_boots_damage"},"hoverEvent":{"action":"show_text","value":"模拟受到10点伤害，测试防御减免"}}]

tellraw @s [{"text":"[测试4] ","color":"yellow","bold":true},{"text":"应用粘液效果(10秒)","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/test_boots_debuff"},"hoverEvent":{"action":"show_text","value":"应用粘液Debuff，测试免疫缩短时间"}}]

tellraw @s [{"text":"[测试5] ","color":"yellow","bold":true},{"text":"召唤史莱姆测试","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/test_boots_monster"},"hoverEvent":{"action":"show_text","value":"召唤带粘液效果的史莱姆"}}]

tellraw @s [{"text":"[测试6] ","color":"yellow","bold":true},{"text":"清除所有效果","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:status/clear_all"},"hoverEvent":{"action":"show_text","value":"清除所有Buff和Debuff"}}]

tellraw @s {"text":""}
tellraw @s {"text":"======================","color":"gold","bold":true}
