# data/stardew/function/equipment/interact/return_ring.mcfunction
# [执行者: 玩家] 使用macro从loot table返还戒指物品
# $id: 戒指的ID (如: warrior_ring)
# $display_name: 戒指的显示名称

$loot give @s loot stardew:items/rings/$(id)
$tellraw @s [{"text":"已卸下: ","color":"yellow"},$(display_name)]
playsound minecraft:item.armor.equip_generic master @s ~ ~ ~ 1 0.8
