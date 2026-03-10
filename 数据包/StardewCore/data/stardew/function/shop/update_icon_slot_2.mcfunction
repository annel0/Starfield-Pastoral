# 更新槽位2的图标 - 通过item_id映射到loot table
# [执行者: item_display]

# 使用execute if data检测item_id并替换对应的loot
execute if data storage stardew:temp {item_id_2:"parsnip_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_parsnip
execute if data storage stardew:temp {item_id_2:"green_bean_starter"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_green_bean
execute if data storage stardew:temp {item_id_2:"cauliflower_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_cauliflower
execute if data storage stardew:temp {item_id_2:"potato_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_potato
execute if data storage stardew:temp {item_id_2:"tulip_bulb"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_tulip
execute if data storage stardew:temp {item_id_2:"kale_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_kale
execute if data storage stardew:temp {item_id_2:"jazz_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_blue_jazz
execute if data storage stardew:temp {item_id_2:"garlic_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_garlic
execute if data storage stardew:temp {item_id_2:"strawberry_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_strawberry
execute if data storage stardew:temp {item_id_2:"melon_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_melon
execute if data storage stardew:temp {item_id_2:"tomato_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_tomato
execute if data storage stardew:temp {item_id_2:"blueberry_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_blueberry
execute if data storage stardew:temp {item_id_2:"pepper_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_hot_pepper
execute if data storage stardew:temp {item_id_2:"wheat_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_wheat
execute if data storage stardew:temp {item_id_2:"radish_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_radish
execute if data storage stardew:temp {item_id_2:"poppy_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_poppy
execute if data storage stardew:temp {item_id_2:"spangle_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_spangle
execute if data storage stardew:temp {item_id_2:"hops_starter"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_hops
execute if data storage stardew:temp {item_id_2:"corn_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_corn
execute if data storage stardew:temp {item_id_2:"sunflower_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_sunflower
execute if data storage stardew:temp {item_id_2:"red_cabbage_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_red_cabbage
execute if data storage stardew:temp {item_id_2:"eggplant_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_eggplant
execute if data storage stardew:temp {item_id_2:"pumpkin_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_pumpkin
execute if data storage stardew:temp {item_id_2:"bok_choy_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_bok_choy
execute if data storage stardew:temp {item_id_2:"yam_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_yam
execute if data storage stardew:temp {item_id_2:"cranberry_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_cranberry
execute if data storage stardew:temp {item_id_2:"fairy_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_fairy_rose
execute if data storage stardew:temp {item_id_2:"amaranth_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_amaranth
execute if data storage stardew:temp {item_id_2:"grape_starter"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_grape
execute if data storage stardew:temp {item_id_2:"artichoke_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_artichoke
execute if data storage stardew:temp {item_id_2:"winter_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_winter_seeds
execute if data storage stardew:temp {item_id_2:"grass_starter"} run loot replace entity @s container.0 loot stardew:items/seeds/grass_starter

# 烹饪原料
execute if data storage stardew:temp {item_id_2:"sugar"} run loot replace entity @s container.0 loot stardew:items/cooking/sugar
execute if data storage stardew:temp {item_id_2:"wheat_flour"} run loot replace entity @s container.0 loot stardew:items/cooking/wheat_flour
execute if data storage stardew:temp {item_id_2:"rice"} run loot replace entity @s container.0 loot stardew:items/cooking/rice
execute if data storage stardew:temp {item_id_2:"oil"} run loot replace entity @s container.0 loot stardew:items/cooking/oil
execute if data storage stardew:temp {item_id_2:"vinegar"} run loot replace entity @s container.0 loot stardew:items/cooking/vinegar

# 肥料
execute if data storage stardew:temp {item_id_2:"basic_fertilizer"} run loot replace entity @s container.0 loot stardew:items/fertilizer/basic_fertilizer
execute if data storage stardew:temp {item_id_2:"quality_fertilizer"} run loot replace entity @s container.0 loot stardew:items/fertilizer/quality_fertilizer
execute if data storage stardew:temp {item_id_2:"basic_retaining_soil"} run loot replace entity @s container.0 loot stardew:items/fertilizer/basic_retaining_soil
execute if data storage stardew:temp {item_id_2:"quality_retaining_soil"} run loot replace entity @s container.0 loot stardew:items/fertilizer/quality_retaining_soil
execute if data storage stardew:temp {item_id_2:"speed_gro"} run loot replace entity @s container.0 loot stardew:items/fertilizer/speed_gro
execute if data storage stardew:temp {item_id_2:"deluxe_speed_gro"} run loot replace entity @s container.0 loot stardew:items/fertilizer/deluxe_speed_gro
