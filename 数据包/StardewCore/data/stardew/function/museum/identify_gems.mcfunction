# stardew:museum/identify_gems
# 检查玩家是否已捐赠对应宝石,并进行鉴定

# 标记是否有物品被鉴定
tag @s remove sd_identified_success

# 石英
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"quartz_unknown"}] if score @s sd_donated matches 1.. run function stardew:museum/identify_item {item_type:"gem",item_name:"quartz",display_name:"石英",cmd:7101}
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"quartz_unknown"}] if score @s sd_donated matches 1.. run tag @s add sd_identified_success

# 地晶
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"earth_crystal_unknown"}] if score @s sd_donated matches 2.. run function stardew:museum/identify_item {item_type:"gem",item_name:"earth_crystal",display_name:"地晶",cmd:7102}
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"earth_crystal_unknown"}] if score @s sd_donated matches 2.. run tag @s add sd_identified_success

# 冻泪石
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"frozen_tear_unknown"}] if score @s sd_donated matches 3.. run function stardew:museum/identify_item {item_type:"gem",item_name:"frozen_tear",display_name:"冻泪石",cmd:7103}
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"frozen_tear_unknown"}] if score @s sd_donated matches 3.. run tag @s add sd_identified_success

# 翡翠
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"jade_unknown"}] if score @s sd_donated matches 4.. run function stardew:museum/identify_item {item_type:"gem",item_name:"jade",display_name:"翡翠",cmd:7104}
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"jade_unknown"}] if score @s sd_donated matches 4.. run tag @s add sd_identified_success

# 红宝石
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"ruby_unknown"}] if score @s sd_donated matches 5.. run function stardew:museum/identify_item {item_type:"gem",item_name:"ruby",display_name:"红宝石",cmd:7105}
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"ruby_unknown"}] if score @s sd_donated matches 5.. run tag @s add sd_identified_success

# 紫水晶
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"amethyst_unknown"}] if score @s sd_donated matches 6.. run function stardew:museum/identify_item {item_type:"gem",item_name:"amethyst",display_name:"紫水晶",cmd:7106}
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"amethyst_unknown"}] if score @s sd_donated matches 6.. run tag @s add sd_identified_success

# 五彩碎片
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"prismatic_shard_unknown"}] if score @s sd_donated matches 7.. run function stardew:museum/identify_item {item_type:"gem",item_name:"prismatic_shard",display_name:"五彩碎片",cmd:7107}
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"prismatic_shard_unknown"}] if score @s sd_donated matches 7.. run tag @s add sd_identified_success

# 黄玉
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"topaz_unknown"}] if score @s sd_donated matches 8.. run function stardew:museum/identify_item {item_type:"gem",item_name:"topaz",display_name:"黄玉",cmd:7108}
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"topaz_unknown"}] if score @s sd_donated matches 8.. run tag @s add sd_identified_success

# 海蓝宝石
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"aquamarine_unknown"}] if score @s sd_donated matches 9.. run function stardew:museum/identify_item {item_type:"gem",item_name:"aquamarine",display_name:"海蓝宝石",cmd:7109}
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"aquamarine_unknown"}] if score @s sd_donated matches 9.. run tag @s add sd_identified_success

# 祖母绿
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"emerald_unknown"}] if score @s sd_donated matches 10.. run function stardew:museum/identify_item {item_type:"gem",item_name:"emerald",display_name:"祖母绿",cmd:7110}
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"emerald_unknown"}] if score @s sd_donated matches 10.. run tag @s add sd_identified_success

# 火石英
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"fire_quartz_unknown"}] if score @s sd_donated matches 11.. run function stardew:museum/identify_item {item_type:"gem",item_name:"fire_quartz",display_name:"火石英",cmd:7111}
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"fire_quartz_unknown"}] if score @s sd_donated matches 11.. run tag @s add sd_identified_success

# 如果没有成功鉴定任何物品，显示失败提示
execute unless entity @s[tag=sd_identified_success] run tellraw @s {"text":"❌ 你还没有向博物馆捐赠过此类物品,无法鉴别!","color":"red"}

# 清理标签
tag @s remove sd_identified_success
