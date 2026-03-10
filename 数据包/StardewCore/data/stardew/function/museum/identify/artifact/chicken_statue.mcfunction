# 鉴定鸡雕像
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_chicken_statue] run function stardew:museum/identify_item {item_type:"artifact",item_name:"chicken_statue",item_cn:"鸡雕像",cmd:7332}
execute unless entity @s[tag=sd_identified_chicken_statue] run function stardew:museum/cannot_identify {item_cn:"鸡雕像"}
