# 测试靴子被动效果系统
# 使用方法: /function stardew:debug/test_boots_passive

tellraw @s [{"text":"========================================","color":"gold"}]
tellraw @s [{"text":"    靴子被动效果测试菜单","color":"yellow","bold":true}]
tellraw @s [{"text":"========================================","color":"gold"}]
tellraw @s {"text":""}

tellraw @s [{"text":"[1] ","color":"aqua","bold":true},{"text":"测试速度效果","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/boots_passive/test_speed"},"hoverEvent":{"action":"show_text","value":"装备太空靴(速度+0.5)或艾米丽魔法靴(速度+1)"}}]

tellraw @s [{"text":"[2] ","color":"aqua","bold":true},{"text":"测试跳跃效果","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/boots_passive/test_jump"},"hoverEvent":{"action":"show_text","value":"装备太空靴(跳跃增强)"}}]

tellraw @s [{"text":"[3] ","color":"aqua","bold":true},{"text":"测试火焰免疫","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/boots_passive/test_fire"},"hoverEvent":{"action":"show_text","value":"装备火行者靴或龙鳞靴(火焰免疫)"}}]

tellraw @s [{"text":"[4] ","color":"aqua","bold":true},{"text":"测试钓鱼加成","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/boots_passive/test_fishing"},"hoverEvent":{"action":"show_text","value":"装备美人鱼靴(钓鱼等级+2)"}}]

tellraw @s [{"text":"[5] ","color":"aqua","bold":true},{"text":"测试精灵祝福","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/boots_passive/test_nature_regen"},"hoverEvent":{"action":"show_text","value":"装备精灵鞋(每10秒回复5点生命和能量)"}}]

tellraw @s [{"text":"[6] ","color":"aqua","bold":true},{"text":"测试磁力效果","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/boots_passive/test_magnetism"},"hoverEvent":{"action":"show_text","value":"装备小丑鞋(物品吸引+2格)"}}]

tellraw @s [{"text":"[7] ","color":"aqua","bold":true},{"text":"测试暴击加成","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/boots_passive/test_crit"},"hoverEvent":{"action":"show_text","value":"装备牛仔靴(暴击率+3%)"}}]

tellraw @s [{"text":"[8] ","color":"aqua","bold":true},{"text":"测试额外防御","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:debug/boots_passive/test_defense_bonus"},"hoverEvent":{"action":"show_text","value":"装备龙鳞靴(防御+5+1额外)"}}]

tellraw @s {"text":""}
tellraw @s [{"text":"[清空装备] ","color":"red","bold":true},{"text":"移除所有靴子","color":"gray","clickEvent":{"action":"run_command","value":"/function stardew:debug/boots_passive/clear_boots"},"hoverEvent":{"action":"show_text","value":"重置装备状态"}}]

tellraw @s {"text":"========================================","color":"gold"}
