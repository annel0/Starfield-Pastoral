# 鉴定生锈的汤匙
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_rusty_spoon] run function stardew:museum/identify_item {item_type:"artifact",item_name:"rusty_spoon",item_cn:"生锈的汤匙",cmd:7326}
execute unless entity @s[tag=sd_identified_rusty_spoon] run function stardew:museum/cannot_identify {item_cn:"生锈的汤匙"}
