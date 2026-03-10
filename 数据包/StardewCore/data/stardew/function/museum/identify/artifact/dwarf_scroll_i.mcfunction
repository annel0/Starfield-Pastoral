# 鉴定矮人卷轴I
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_dwarf_scroll_i] run function stardew:museum/identify_item {item_type:"artifact",item_name:"dwarf_scroll_i",item_cn:"矮人卷轴I",cmd:7300}
execute unless entity @s[tag=sd_identified_dwarf_scroll_i] run function stardew:museum/cannot_identify {item_cn:"矮人卷轴I"}
