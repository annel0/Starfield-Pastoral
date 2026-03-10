# 测试暴击加成 - 装备牛仔靴
scoreboard players set @s sd_equip_boots 1
data modify storage stardew:equipment boots set value {defense:2,immunity:2,effects:{crit_chance:0.03d}}

tellraw @s [{"text":"[测试] ","color":"green"},{"text":"已装备牛仔靴 (暴击率+3%)","color":"yellow"}]
tellraw @s [{"text":"提示: ","color":"gray"},{"text":"攻击怪物时，暴击率会额外增加3%","color":"white"}]
tellraw @s [{"text":"建议: ","color":"gray"},{"text":"生成一个测试怪物并攻击多次观察暴击率","color":"white"}]
tellraw @s [{"text":"[生成史莱姆] ","color":"gold","clickEvent":{"action":"run_command","value":"/function stardew:debug/test_boots_monster"},"hoverEvent":{"action":"show_text","value":"点击生成测试怪物"}}]
