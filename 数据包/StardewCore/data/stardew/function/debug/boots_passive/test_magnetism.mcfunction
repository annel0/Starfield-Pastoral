# 测试磁力效果 - 装备小丑鞋
scoreboard players set @s sd_equip_boots 1
data modify storage stardew:equipment boots set value {defense:1,immunity:5,effects:{magnetism:2}}

tellraw @s [{"text":"[测试] ","color":"green"},{"text":"已装备小丑鞋 (物品吸引+2格)","color":"yellow"}]
tellraw @s [{"text":"提示: ","color":"gray"},{"text":"在你附近3.5格内生成一些物品，观察它们是否被吸引","color":"white"}]

# 在玩家周围生成测试物品
summon item ~2 ~ ~2 {Item:{id:"minecraft:apple",count:1},PickupDelay:10s}
summon item ~-2 ~ ~2 {Item:{id:"minecraft:apple",count:1},PickupDelay:10s}
summon item ~2 ~ ~-2 {Item:{id:"minecraft:apple",count:1},PickupDelay:10s}
summon item ~-2 ~ ~-2 {Item:{id:"minecraft:apple",count:1},PickupDelay:10s}

tellraw @s [{"text":"已在你周围生成4个苹果，观察磁力效果","color":"aqua"}]
