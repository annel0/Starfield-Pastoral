# 鉴定石英
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_quartz] run function stardew:museum/identify_item {item_type:"gem",item_name:"quartz",item_cn:"石英",cmd:7101}
execute unless entity @s[tag=sd_identified_quartz] run function stardew:museum/cannot_identify {item_cn:"石英"}
