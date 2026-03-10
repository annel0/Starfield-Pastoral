# 鉴定远古玩偶
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_ancient_doll] run function stardew:museum/identify_item {item_type:"artifact",item_name:"ancient_doll",item_cn:"远古玩偶",cmd:7312}
execute unless entity @s[tag=sd_identified_ancient_doll] run function stardew:museum/cannot_identify {item_cn:"远古玩偶"}
