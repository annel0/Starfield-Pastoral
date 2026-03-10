# 鉴定红宝石
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_ruby] run function stardew:museum/identify_item {item_type:"gem",item_name:"ruby",item_cn:"红宝石",cmd:7105}
execute unless entity @s[tag=sd_identified_ruby] run function stardew:museum/cannot_identify {item_cn:"红宝石"}
