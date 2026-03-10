# 鉴定史前肋骨
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_prehistoric_rib] run function stardew:museum/identify_item {item_type:"artifact",item_name:"prehistoric_rib",item_cn:"史前肋骨",cmd:7370}
execute unless entity @s[tag=sd_identified_prehistoric_rib] run function stardew:museum/cannot_identify {item_cn:"史前肋骨"}
