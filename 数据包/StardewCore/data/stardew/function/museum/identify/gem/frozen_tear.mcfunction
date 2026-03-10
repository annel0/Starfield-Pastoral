# 鉴定冻泪石
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_frozen_tear] run function stardew:museum/identify_item {item_type:"gem",item_name:"frozen_tear",item_cn:"冻泪石",cmd:7103}
execute unless entity @s[tag=sd_identified_frozen_tear] run function stardew:museum/cannot_identify {item_cn:"冻泪石"}
