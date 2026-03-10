# 鉴定黄金文物
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_golden_relic] run function stardew:museum/identify_item {item_type:"artifact",item_name:"golden_relic",item_cn:"黄金文物",cmd:7356}
execute unless entity @s[tag=sd_identified_golden_relic] run function stardew:museum/cannot_identify {item_cn:"黄金文物"}
