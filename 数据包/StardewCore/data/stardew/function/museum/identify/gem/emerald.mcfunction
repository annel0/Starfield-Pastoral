# 鉴定祖母绿
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_emerald] run function stardew:museum/identify_item {item_type:"gem",item_name:"emerald",item_cn:"祖母绿",cmd:7110}
execute unless entity @s[tag=sd_identified_emerald] run function stardew:museum/cannot_identify {item_cn:"祖母绿"}
