# 鉴定海蓝宝石
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_aquamarine] run function stardew:museum/identify_item {item_type:"gem",item_name:"aquamarine",item_cn:"海蓝宝石",cmd:7109}
execute unless entity @s[tag=sd_identified_aquamarine] run function stardew:museum/cannot_identify {item_cn:"海蓝宝石"}
