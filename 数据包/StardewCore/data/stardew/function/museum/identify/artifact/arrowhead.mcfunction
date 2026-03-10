# 鉴定箭头
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_arrowhead] run function stardew:museum/identify_item {item_type:"artifact",item_name:"arrowhead",item_cn:"箭头",cmd:7310}
execute unless entity @s[tag=sd_identified_arrowhead] run function stardew:museum/cannot_identify {item_cn:"箭头"}
