# 鉴定三叶虫
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_trilobite] run function stardew:museum/identify_item {item_type:"artifact",item_name:"trilobite",item_cn:"三叶虫",cmd:7382}
execute unless entity @s[tag=sd_identified_trilobite] run function stardew:museum/cannot_identify {item_cn:"三叶虫"}
