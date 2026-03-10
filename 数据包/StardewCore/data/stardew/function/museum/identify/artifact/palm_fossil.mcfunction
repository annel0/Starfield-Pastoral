# 鉴定棕榈化石
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_palm_fossil] run function stardew:museum/identify_item {item_type:"artifact",item_name:"palm_fossil",item_cn:"棕榈化石",cmd:7380}
execute unless entity @s[tag=sd_identified_palm_fossil] run function stardew:museum/cannot_identify {item_cn:"棕榈化石"}
