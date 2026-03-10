# 鉴定紫水晶
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_amethyst] run function stardew:museum/identify_item {item_type:"gem",item_name:"amethyst",item_cn:"紫水晶",cmd:7106}
execute unless entity @s[tag=sd_identified_amethyst] run function stardew:museum/cannot_identify {item_cn:"紫水晶"}
