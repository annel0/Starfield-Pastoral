# 鉴定精灵珠宝
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_elvish_jewelry] run function stardew:museum/identify_item {item_type:"artifact",item_name:"elvish_jewelry",item_cn:"精灵珠宝",cmd:7314}
execute unless entity @s[tag=sd_identified_elvish_jewelry] run function stardew:museum/cannot_identify {item_cn:"精灵珠宝"}
