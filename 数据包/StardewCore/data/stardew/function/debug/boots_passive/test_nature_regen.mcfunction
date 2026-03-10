# 测试精灵祝福 - 装备精灵鞋
scoreboard players set @s sd_equip_boots 1
data modify storage stardew:equipment boots set value {defense:4,immunity:6,effects:{nature_regeneration:1b}}

# 降低生命和能量以便观察回复
scoreboard players remove @s sd_health 50
scoreboard players remove @s sd_energy 50

tellraw @s [{"text":"[测试] ","color":"green"},{"text":"已装备精灵鞋 (精灵祝福)","color":"yellow"}]
tellraw @s [{"text":"效果: ","color":"gray"},{"text":"每10秒回复5点生命和能量","color":"white"}]
tellraw @s [{"text":"提示: ","color":"gray"},{"text":"已为你降低50点生命和能量，请等待10秒观察回复效果","color":"white"}]
