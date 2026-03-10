# 鉴定史前工具
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_prehistoric_tool] run function stardew:museum/identify_item {item_type:"artifact",item_name:"prehistoric_tool",item_cn:"史前工具",cmd:7336}
execute unless entity @s[tag=sd_identified_prehistoric_tool] run function stardew:museum/cannot_identify {item_cn:"史前工具"}
