# 鉴定史前手斧
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_prehistoric_handaxe] run function stardew:museum/identify_item {item_type:"artifact",item_name:"prehistoric_handaxe",item_cn:"史前手斧",cmd:7346}
execute unless entity @s[tag=sd_identified_prehistoric_handaxe] run function stardew:museum/cannot_identify {item_cn:"史前手斧"}
