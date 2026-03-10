# 鉴定奇怪的玩偶(绿)
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_strange_doll_green] run function stardew:museum/identify_item {item_type:"artifact",item_name:"strange_doll_green",item_cn:"奇怪的玩偶(绿)",cmd:7358}
execute unless entity @s[tag=sd_identified_strange_doll_green] run function stardew:museum/cannot_identify {item_cn:"奇怪的玩偶(绿)"}
