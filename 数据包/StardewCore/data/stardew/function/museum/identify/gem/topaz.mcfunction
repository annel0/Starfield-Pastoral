# 鉴定黄水晶
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_topaz] run function stardew:museum/identify_item {item_type:"gem",item_name:"topaz",item_cn:"黄水晶",cmd:7108}
execute unless entity @s[tag=sd_identified_topaz] run function stardew:museum/cannot_identify {item_cn:"黄水晶"}
