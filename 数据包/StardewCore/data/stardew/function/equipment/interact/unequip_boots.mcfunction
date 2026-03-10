# data/stardew/function/equipment/interact/unequip_boots.mcfunction
# [执行者: 玩家] 卸下鞋子

# 检查槽位是否有装备
execute unless score @s sd_equip_boots matches 1 run return 0

# 从storage读取装备ID并使用loot table生成物品
function stardew:equipment/interact/return_boots with storage stardew:equipment boots

# 清除槽位数据
scoreboard players set @s sd_equip_boots 0
scoreboard players set @s sd_equip_boots_cmd 0

# 清除storage数据
data remove storage stardew:equipment boots

# 移除装备效果（暂时简化处理）
# TODO: 应该从storage读取实际属性值进行减法

# 刷新装备菜单
execute if score @s sd_menu_level matches 2 run function stardew:menu/pages/equipment_menu
