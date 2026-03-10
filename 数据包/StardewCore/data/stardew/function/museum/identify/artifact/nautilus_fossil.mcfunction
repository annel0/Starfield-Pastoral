# 鉴定鹦鹉螺化石
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_nautilus_fossil] run function stardew:museum/identify_item {item_type:"artifact",item_name:"nautilus_fossil",item_cn:"鹦鹉螺化石",cmd:7376}
execute unless entity @s[tag=sd_identified_nautilus_fossil] run function stardew:museum/cannot_identify {item_cn:"鹦鹉螺化石"}
