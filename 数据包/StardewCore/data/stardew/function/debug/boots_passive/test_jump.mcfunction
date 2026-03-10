# 测试跳跃效果 - 装备太空靴
scoreboard players set @s sd_equip_boots 1
data modify storage stardew:equipment boots set value {defense:4,immunity:4,effects:{speed:0.5d,jump_boost:1b}}
tellraw @s [{"text":"[测试] ","color":"green"},{"text":"已装备太空靴 (速度+0.5, 跳跃增强)","color":"yellow"}]
tellraw @s [{"text":"提示: ","color":"gray"},{"text":"尝试跳跃，你应该跳得更高","color":"white"}]
