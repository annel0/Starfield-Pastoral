# 测试火焰免疫 - 装备火行者靴
scoreboard players set @s sd_equip_boots 1
data modify storage stardew:equipment boots set value {defense:4,immunity:3,effects:{fire_immunity:1b}}
tellraw @s [{"text":"[测试] ","color":"green"},{"text":"已装备火行者靴 (火焰免疫)","color":"yellow"}]
tellraw @s [{"text":"提示: ","color":"gray"},{"text":"尝试站在火或岩浆中，你应该不会受到伤害","color":"white"}]
summon marker ~ ~ ~ {Tags:["fire_test"]}
execute at @e[type=marker,tag=fire_test,limit=1] run setblock ~ ~ ~ fire
kill @e[type=marker,tag=fire_test]
