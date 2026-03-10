# 鉴定远古之剑
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_ancient_sword] run function stardew:museum/identify_item {item_type:"artifact",item_name:"ancient_sword",item_cn:"远古之剑",cmd:7324}
execute unless entity @s[tag=sd_identified_ancient_sword] run function stardew:museum/cannot_identify {item_cn:"远古之剑"}
