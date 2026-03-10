# data/stardew/function/equipment/interact/detect_equip.mcfunction
# [执行者: 玩家] 检测右键使用 carrot_on_a_stick

# 检查手持物品是否有 stardew custom_data
execute unless data entity @s SelectedItem.components."minecraft:custom_data".stardew run return 0

# 获取装备数据
data modify storage stardew:equipment temp set from entity @s SelectedItem.components."minecraft:custom_data".stardew

# 检查装备类型并调用对应函数
execute if data storage stardew:equipment temp{type:"boots"} run function stardew:equipment/interact/equip_boots
execute if data storage stardew:equipment temp{type:"ring"} run function stardew:equipment/interact/equip_ring

# 清空临时数据
data remove storage stardew:equipment temp
