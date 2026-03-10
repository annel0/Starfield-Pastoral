# data/stardew/function/equipment/interact/return_boots.mcfunction
# [执行者: 玩家] 使用macro从loot table返还靴子物品
# $id: 靴子的ID (如: leather_boots)

$loot give @s loot stardew:items/boots/$(id)
tellraw @s [{"text":"已卸下: ","color":"yellow"},{"nbt":"boots.display_name","storage":"stardew:equipment","interpret":true}]
playsound minecraft:item.armor.equip_leather master @s ~ ~ ~ 1 0.8
