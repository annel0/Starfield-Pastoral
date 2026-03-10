# 鉴定干海星
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_dried_starfish] run function stardew:museum/identify_item {item_type:"artifact",item_name:"dried_starfish",item_cn:"干海星",cmd:7338}
execute unless entity @s[tag=sd_identified_dried_starfish] run function stardew:museum/cannot_identify {item_cn:"干海星"}
