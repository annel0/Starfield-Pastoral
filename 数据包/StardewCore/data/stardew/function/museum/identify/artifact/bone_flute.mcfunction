# 鉴定骨笛
# 检查玩家是否已捐赠过此物品

execute if entity @s[tag=sd_identified_bone_flute] run function stardew:museum/identify_item {item_type:"artifact",item_name:"bone_flute",item_cn:"骨笛",cmd:7344}
execute unless entity @s[tag=sd_identified_bone_flute] run function stardew:museum/cannot_identify {item_cn:"骨笛"}
