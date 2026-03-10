# 鉴定破损的双耳瓶
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_chipped_amphora] run function stardew:museum/identify_item {item_type:"artifact",item_name:"chipped_amphora",item_cn:"破损的双耳瓶",cmd:7308}
execute unless entity @s[tag=sd_identified_chipped_amphora] run function stardew:museum/cannot_identify {item_cn:"破损的双耳瓶"}
