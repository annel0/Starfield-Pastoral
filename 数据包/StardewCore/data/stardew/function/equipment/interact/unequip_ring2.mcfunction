# data/stardew/function/equipment/interact/unequip_ring2.mcfunction
# [执行者: 玩家] 卸下戒指槽位2

execute unless score @s sd_equip_ring2 matches 1 run return 0

# 检查这个戒指是否有发光效果
execute store result score #this_ring_glow stardew.temp run data get storage stardew:equipment ring2.effects.glow 1

# 从storage读取装备ID并使用loot table生成物品
function stardew:equipment/interact/return_ring with storage stardew:equipment ring2

scoreboard players set @s sd_equip_ring2 0
scoreboard players set @s sd_equip_ring2_cmd 0

# 清除storage数据
data remove storage stardew:equipment ring2

# 如果卸下的戒指有发光效果,检查是否需要清理光源
execute if score #this_ring_glow stardew.temp matches 1.. at @s run function stardew:equipment/effects/rings/check_and_cleanup_glow

execute if score @s sd_menu_level matches 2 run function stardew:menu/pages/equipment_menu
