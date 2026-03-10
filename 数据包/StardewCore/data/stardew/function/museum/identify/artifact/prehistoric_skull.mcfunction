# 鉴定史前头骨
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_prehistoric_skull] run function stardew:museum/identify_item {item_type:"artifact",item_name:"prehistoric_skull",item_cn:"史前头骨",cmd:7366}
execute unless entity @s[tag=sd_identified_prehistoric_skull] run function stardew:museum/cannot_identify {item_cn:"史前头骨"}
