# 鉴定翡翠
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_jade] run function stardew:museum/identify_item {item_type:"gem",item_name:"jade",item_cn:"翡翠",cmd:7104}
execute unless entity @s[tag=sd_identified_jade] run function stardew:museum/cannot_identify {item_cn:"翡翠"}
