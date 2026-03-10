# 鉴定远古种子
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_ancient_seed] run function stardew:museum/identify_item {item_type:"artifact",item_name:"ancient_seed",item_cn:"远古种子",cmd:7334}
execute unless entity @s[tag=sd_identified_ancient_seed] run function stardew:museum/cannot_identify {item_cn:"远古种子"}
