# 鉴定恐龙蛋
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_dinosaur_egg] run function stardew:museum/identify_item {item_type:"artifact",item_name:"dinosaur_egg",item_cn:"恐龙蛋",cmd:7320}
execute unless entity @s[tag=sd_identified_dinosaur_egg] run function stardew:museum/cannot_identify {item_cn:"恐龙蛋"}
