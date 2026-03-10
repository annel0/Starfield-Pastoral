# 鉴定地晶
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_earth_crystal] run function stardew:museum/identify_item {item_type:"gem",item_name:"earth_crystal",item_cn:"地晶",cmd:7102}
execute unless entity @s[tag=sd_identified_earth_crystal] run function stardew:museum/cannot_identify {item_cn:"地晶"}
