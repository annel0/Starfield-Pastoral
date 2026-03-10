# 更新槽位1的图标 - 通过item_id映射到loot table
# [执行者: item_display]

# 使用execute if data检测item_id并替换对应的loot
execute if data storage stardew:temp {item_id_1:"parsnip_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_parsnip
execute if data storage stardew:temp {item_id_1:"green_bean_starter"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_green_bean
execute if data storage stardew:temp {item_id_1:"cauliflower_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_cauliflower
execute if data storage stardew:temp {item_id_1:"potato_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_potato
execute if data storage stardew:temp {item_id_1:"tulip_bulb"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_tulip
execute if data storage stardew:temp {item_id_1:"kale_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_kale
execute if data storage stardew:temp {item_id_1:"jazz_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_blue_jazz
execute if data storage stardew:temp {item_id_1:"garlic_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_garlic
execute if data storage stardew:temp {item_id_1:"strawberry_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_strawberry
execute if data storage stardew:temp {item_id_1:"melon_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_melon
execute if data storage stardew:temp {item_id_1:"tomato_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_tomato
execute if data storage stardew:temp {item_id_1:"blueberry_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_blueberry
execute if data storage stardew:temp {item_id_1:"pepper_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_hot_pepper
execute if data storage stardew:temp {item_id_1:"wheat_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_wheat
execute if data storage stardew:temp {item_id_1:"radish_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_radish
execute if data storage stardew:temp {item_id_1:"poppy_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_poppy
execute if data storage stardew:temp {item_id_1:"spangle_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_spangle
execute if data storage stardew:temp {item_id_1:"hops_starter"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_hops
execute if data storage stardew:temp {item_id_1:"corn_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_corn
execute if data storage stardew:temp {item_id_1:"sunflower_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_sunflower
execute if data storage stardew:temp {item_id_1:"red_cabbage_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_red_cabbage
execute if data storage stardew:temp {item_id_1:"eggplant_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_eggplant
execute if data storage stardew:temp {item_id_1:"pumpkin_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_pumpkin
execute if data storage stardew:temp {item_id_1:"bok_choy_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_bok_choy
execute if data storage stardew:temp {item_id_1:"yam_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_yam
execute if data storage stardew:temp {item_id_1:"cranberry_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_cranberry
execute if data storage stardew:temp {item_id_1:"fairy_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_fairy_rose
execute if data storage stardew:temp {item_id_1:"amaranth_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_amaranth
execute if data storage stardew:temp {item_id_1:"grape_starter"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_grape
execute if data storage stardew:temp {item_id_1:"artichoke_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_artichoke
execute if data storage stardew:temp {item_id_1:"winter_seeds"} run loot replace entity @s container.0 loot stardew:items/seeds/crop_winter_seeds
execute if data storage stardew:temp {item_id_1:"grass_starter"} run loot replace entity @s container.0 loot stardew:items/seeds/grass_starter

# 烹饪原料
execute if data storage stardew:temp {item_id_1:"sugar"} run loot replace entity @s container.0 loot stardew:items/cooking/sugar
execute if data storage stardew:temp {item_id_1:"wheat_flour"} run loot replace entity @s container.0 loot stardew:items/cooking/wheat_flour
execute if data storage stardew:temp {item_id_1:"rice"} run loot replace entity @s container.0 loot stardew:items/cooking/rice
execute if data storage stardew:temp {item_id_1:"oil"} run loot replace entity @s container.0 loot stardew:items/cooking/oil
execute if data storage stardew:temp {item_id_1:"vinegar"} run loot replace entity @s container.0 loot stardew:items/cooking/vinegar

# 肥料
execute if data storage stardew:temp {item_id_1:"basic_fertilizer"} run loot replace entity @s container.0 loot stardew:items/fertilizer/basic_fertilizer
execute if data storage stardew:temp {item_id_1:"quality_fertilizer"} run loot replace entity @s container.0 loot stardew:items/fertilizer/quality_fertilizer
execute if data storage stardew:temp {item_id_1:"basic_retaining_soil"} run loot replace entity @s container.0 loot stardew:items/fertilizer/basic_retaining_soil
execute if data storage stardew:temp {item_id_1:"quality_retaining_soil"} run loot replace entity @s container.0 loot stardew:items/fertilizer/quality_retaining_soil
execute if data storage stardew:temp {item_id_1:"speed_gro"} run loot replace entity @s container.0 loot stardew:items/fertilizer/speed_gro
execute if data storage stardew:temp {item_id_1:"deluxe_speed_gro"} run loot replace entity @s container.0 loot stardew:items/fertilizer/deluxe_speed_gro
