# 鉴定矮人小工具
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_dwarf_gadget] run function stardew:museum/identify_item {item_type:"artifact",item_name:"dwarf_gadget",item_cn:"矮人小工具",cmd:7350}
execute unless entity @s[tag=sd_identified_dwarf_gadget] run function stardew:museum/cannot_identify {item_cn:"矮人小工具"}
