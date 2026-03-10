# 更新槽位3的图标 - 通过item_id映射到loot table
# [执行者: item_display]

# 使用execute if data检测item_id并替换对应的loot
execute if data storage stardew:temp {item_id_3:"parsnip_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_parsnip
execute if data storage stardew:temp {item_id_3:"green_bean_starter"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_green_bean
execute if data storage stardew:temp {item_id_3:"cauliflower_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_cauliflower
execute if data storage stardew:temp {item_id_3:"potato_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_potato
execute if data storage stardew:temp {item_id_3:"tulip_bulb"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_tulip
execute if data storage stardew:temp {item_id_3:"kale_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_kale
execute if data storage stardew:temp {item_id_3:"jazz_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_blue_jazz
execute if data storage stardew:temp {item_id_3:"garlic_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_garlic
execute if data storage stardew:temp {item_id_3:"strawberry_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_strawberry
execute if data storage stardew:temp {item_id_3:"melon_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_melon
execute if data storage stardew:temp {item_id_3:"tomato_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_tomato
execute if data storage stardew:temp {item_id_3:"blueberry_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_blueberry
execute if data storage stardew:temp {item_id_3:"pepper_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_hot_pepper
execute if data storage stardew:temp {item_id_3:"wheat_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_wheat
execute if data storage stardew:temp {item_id_3:"radish_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_radish
execute if data storage stardew:temp {item_id_3:"poppy_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_poppy
execute if data storage stardew:temp {item_id_3:"spangle_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_spangle
execute if data storage stardew:temp {item_id_3:"hops_starter"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_hops
execute if data storage stardew:temp {item_id_3:"corn_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_corn
execute if data storage stardew:temp {item_id_3:"sunflower_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_sunflower
execute if data storage stardew:temp {item_id_3:"red_cabbage_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_red_cabbage
execute if data storage stardew:temp {item_id_3:"eggplant_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_eggplant
execute if data storage stardew:temp {item_id_3:"pumpkin_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_pumpkin
execute if data storage stardew:temp {item_id_3:"bok_choy_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_bok_choy
execute if data storage stardew:temp {item_id_3:"yam_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_yam
execute if data storage stardew:temp {item_id_3:"cranberry_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_cranberry
execute if data storage stardew:temp {item_id_3:"fairy_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_fairy_rose
execute if data storage stardew:temp {item_id_3:"amaranth_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_amaranth
execute if data storage stardew:temp {item_id_3:"grape_starter"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_grape
execute if data storage stardew:temp {item_id_3:"artichoke_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_artichoke
execute if data storage stardew:temp {item_id_3:"winter_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_winter_seeds
execute if data storage stardew:temp {item_id_3:"grass_starter"} run loot replace entity @s container.0 loot stardew:items/seeds/grass_starter

# 烹饪原料
execute if data storage stardew:temp {item_id_3:"sugar"} run loot replace entity @s container.0 loot stardew:items/cooking/sugar
execute if data storage stardew:temp {item_id_3:"wheat_flour"} run loot replace entity @s container.0 loot stardew:items/cooking/wheat_flour
execute if data storage stardew:temp {item_id_3:"rice"} run loot replace entity @s container.0 loot stardew:items/cooking/rice
execute if data storage stardew:temp {item_id_3:"oil"} run loot replace entity @s container.0 loot stardew:items/cooking/oil
execute if data storage stardew:temp {item_id_3:"vinegar"} run loot replace entity @s container.0 loot stardew:items/cooking/vinegar

# 肥料
execute if data storage stardew:temp {item_id_3:"basic_fertilizer"} run loot replace entity @s container.0 loot stardew:items/fertilizer/basic_fertilizer
execute if data storage stardew:temp {item_id_3:"quality_fertilizer"} run loot replace entity @s container.0 loot stardew:items/fertilizer/quality_fertilizer
execute if data storage stardew:temp {item_id_3:"basic_retaining_soil"} run loot replace entity @s container.0 loot stardew:items/fertilizer/basic_retaining_soil
execute if data storage stardew:temp {item_id_3:"quality_retaining_soil"} run loot replace entity @s container.0 loot stardew:items/fertilizer/quality_retaining_soil
execute if data storage stardew:temp {item_id_3:"speed_gro"} run loot replace entity @s container.0 loot stardew:items/fertilizer/speed_gro
execute if data storage stardew:temp {item_id_3:"deluxe_speed_gro"} run loot replace entity @s container.0 loot stardew:items/fertilizer/deluxe_speed_gro
