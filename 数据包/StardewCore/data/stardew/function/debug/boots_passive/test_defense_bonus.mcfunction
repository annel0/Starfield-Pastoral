# 测试额外防御 - 装备龙鳞靴
scoreboard players set @s sd_equip_boots 1
data modify storage stardew:equipment boots set value {defense:5,immunity:4,effects:{defense_bonus:1,fire_immunity:1b}}

tellraw @s [{"text":"[测试] ","color":"green"},{"text":"已装备龙鳞靴 (防御5+1额外, 火焰免疫)","color":"yellow"}]
tellraw @s [{"text":"提示: ","color":"gray"},{"text":"总防御: 6 (基础5 + 额外1)","color":"white"}]
tellraw @s [{"text":"测试: ","color":"gray"},{"text":"使用测试伤害命令观察防御效果","color":"white"}]
tellraw @s [{"text":"[测试10点伤害] ","color":"red","clickEvent":{"action":"run_command","value":"/function stardew:debug/test_boots_damage"},"hoverEvent":{"action":"show_text","value":"点击测试防御减伤"}}]
