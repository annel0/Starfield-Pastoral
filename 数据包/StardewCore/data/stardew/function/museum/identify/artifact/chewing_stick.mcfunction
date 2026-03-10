# 鉴定咀嚼棒
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_chewing_stick] run function stardew:museum/identify_item {item_type:"artifact",item_name:"chewing_stick",item_cn:"咀嚼棒",cmd:7316}
execute unless entity @s[tag=sd_identified_chewing_stick] run function stardew:museum/cannot_identify {item_cn:"咀嚼棒"}
