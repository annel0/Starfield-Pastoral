# 鉴定史前椎骨
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_prehistoric_vertebra] run function stardew:museum/identify_item {item_type:"artifact",item_name:"prehistoric_vertebra",item_cn:"史前椎骨",cmd:7372}
execute unless entity @s[tag=sd_identified_prehistoric_vertebra] run function stardew:museum/cannot_identify {item_cn:"史前椎骨"}
