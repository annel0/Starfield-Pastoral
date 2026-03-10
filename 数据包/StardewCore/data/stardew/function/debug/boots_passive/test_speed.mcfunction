# 测试速度效果 - 装备太空靴(速度+0.5)
scoreboard players set @s sd_equip_boots 1
data modify storage stardew:equipment boots set value {defense:4,immunity:4,effects:{speed:0.5d,jump_boost:1b}}
tellraw @s [{"text":"[测试] ","color":"green"},{"text":"已装备太空靴 (速度+0.5, 跳跃增强)","color":"yellow"}]
tellraw @s [{"text":"提示: ","color":"gray"},{"text":"你应该感受到轻微的速度提升和更高的跳跃","color":"white"}]
