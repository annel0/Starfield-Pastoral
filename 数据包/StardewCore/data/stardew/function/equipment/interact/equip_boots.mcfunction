# data/stardew/function/equipment/interact/equip_boots.mcfunction
# [执行者: 玩家] 装备鞋子

# 检查槽位是否为空
execute if score @s sd_equip_boots matches 1.. run tellraw @s {"text":"鞋子槽位已被占用！请先卸下当前鞋子。","color":"red"}
execute if score @s sd_equip_boots matches 1.. run playsound minecraft:block.note_block.bass master @s ~ ~ ~ 1 0.5
execute if score @s sd_equip_boots matches 1.. run return 0

# 槽位为空，执行装备
function stardew:equipment/interact/equip_boots_impl
