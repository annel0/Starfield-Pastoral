# 鉴定黄金面具
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_golden_mask] run function stardew:museum/identify_item {item_type:"artifact",item_name:"golden_mask",item_cn:"黄金面具",cmd:7354}
execute unless entity @s[tag=sd_identified_golden_mask] run function stardew:museum/cannot_identify {item_cn:"黄金面具"}
