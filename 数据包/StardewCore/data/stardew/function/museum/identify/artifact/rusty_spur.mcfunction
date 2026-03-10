# 鉴定生锈的马刺
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_rusty_spur] run function stardew:museum/identify_item {item_type:"artifact",item_name:"rusty_spur",item_cn:"生锈的马刺",cmd:7328}
execute unless entity @s[tag=sd_identified_rusty_spur] run function stardew:museum/cannot_identify {item_cn:"生锈的马刺"}
