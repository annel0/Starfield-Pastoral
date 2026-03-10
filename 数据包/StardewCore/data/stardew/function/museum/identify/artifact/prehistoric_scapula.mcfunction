# 鉴定史前肩胛骨
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_prehistoric_scapula] run function stardew:museum/identify_item {item_type:"artifact",item_name:"prehistoric_scapula",item_cn:"史前肩胛骨",cmd:7362}
execute unless entity @s[tag=sd_identified_prehistoric_scapula] run function stardew:museum/cannot_identify {item_cn:"史前肩胛骨"}
