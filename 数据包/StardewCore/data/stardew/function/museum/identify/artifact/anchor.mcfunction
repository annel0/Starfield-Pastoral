# 鉴定锚
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_anchor] run function stardew:museum/identify_item {item_type:"artifact",item_name:"anchor",item_cn:"锚",cmd:7340}
execute unless entity @s[tag=sd_identified_anchor] run function stardew:museum/cannot_identify {item_cn:"锚"}
