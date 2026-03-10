# 鉴定生锈的齿轮
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_rusty_cog] run function stardew:museum/identify_item {item_type:"artifact",item_name:"rusty_cog",item_cn:"生锈的齿轮",cmd:7330}
execute unless entity @s[tag=sd_identified_rusty_cog] run function stardew:museum/cannot_identify {item_cn:"生锈的齿轮"}
