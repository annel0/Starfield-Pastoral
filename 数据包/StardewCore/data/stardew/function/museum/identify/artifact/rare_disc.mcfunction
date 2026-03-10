# 鉴定稀有光盘
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_rare_disc] run function stardew:museum/identify_item {item_type:"artifact",item_name:"rare_disc",item_cn:"稀有光盘",cmd:7322}
execute unless entity @s[tag=sd_identified_rare_disc] run function stardew:museum/cannot_identify {item_cn:"稀有光盘"}
