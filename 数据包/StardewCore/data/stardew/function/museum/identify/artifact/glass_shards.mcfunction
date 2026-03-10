# 鉴定玻璃碎片
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_glass_shards] run function stardew:museum/identify_item {item_type:"artifact",item_name:"glass_shards",item_cn:"玻璃碎片",cmd:7342}
execute unless entity @s[tag=sd_identified_glass_shards] run function stardew:museum/cannot_identify {item_cn:"玻璃碎片"}
