# 鉴定装饰扇
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_ornamental_fan] run function stardew:museum/identify_item {item_type:"artifact",item_name:"ornamental_fan",item_cn:"装饰扇",cmd:7318}
execute unless entity @s[tag=sd_identified_ornamental_fan] run function stardew:museum/cannot_identify {item_cn:"装饰扇"}
