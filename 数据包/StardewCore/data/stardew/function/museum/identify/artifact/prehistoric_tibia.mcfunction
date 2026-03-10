# 鉴定史前胫骨
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_prehistoric_tibia] run function stardew:museum/identify_item {item_type:"artifact",item_name:"prehistoric_tibia",item_cn:"史前胫骨",cmd:7364}
execute unless entity @s[tag=sd_identified_prehistoric_tibia] run function stardew:museum/cannot_identify {item_cn:"史前胫骨"}
