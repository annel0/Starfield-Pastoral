# 鉴定五彩碎片
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_prismatic_shard] run function stardew:museum/identify_item {item_type:"gem",item_name:"prismatic_shard",item_cn:"五彩碎片",cmd:7107}
execute unless entity @s[tag=sd_identified_prismatic_shard] run function stardew:museum/cannot_identify {item_cn:"五彩碎片"}
