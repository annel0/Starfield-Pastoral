# 鉴定奇怪的玩偶(黄)
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_strange_doll_yellow] run function stardew:museum/identify_item {item_type:"artifact",item_name:"strange_doll_yellow",item_cn:"奇怪的玩偶(黄)",cmd:7360}
execute unless entity @s[tag=sd_identified_strange_doll_yellow] run function stardew:museum/cannot_identify {item_cn:"奇怪的玩偶(黄)"}
