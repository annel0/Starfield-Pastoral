# 鉴定两栖动物化石
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_amphibian_fossil] run function stardew:museum/identify_item {item_type:"artifact",item_name:"amphibian_fossil",item_cn:"两栖动物化石",cmd:7378}
execute unless entity @s[tag=sd_identified_amphibian_fossil] run function stardew:museum/cannot_identify {item_cn:"两栖动物化石"}
