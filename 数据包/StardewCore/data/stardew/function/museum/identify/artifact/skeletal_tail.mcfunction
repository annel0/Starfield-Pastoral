# 鉴定骨尾
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_skeletal_tail] run function stardew:museum/identify_item {item_type:"artifact",item_name:"skeletal_tail",item_cn:"骨尾",cmd:7374}
execute unless entity @s[tag=sd_identified_skeletal_tail] run function stardew:museum/cannot_identify {item_cn:"骨尾"}
