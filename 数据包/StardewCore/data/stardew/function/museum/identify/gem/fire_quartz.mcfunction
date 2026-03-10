# 鉴定火水晶
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_fire_quartz] run function stardew:museum/identify_item {item_type:"gem",item_name:"fire_quartz",item_cn:"火水晶",cmd:7111}
execute unless entity @s[tag=sd_identified_fire_quartz] run function stardew:museum/cannot_identify {item_cn:"火水晶"}
