# 鉴定远古鼓
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_ancient_drum] run function stardew:museum/identify_item {item_type:"artifact",item_name:"ancient_drum",item_cn:"远古鼓",cmd:7352}
execute unless entity @s[tag=sd_identified_ancient_drum] run function stardew:museum/cannot_identify {item_cn:"远古鼓"}
