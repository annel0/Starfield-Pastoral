# 根据item_id给予对应的物品 (通过loot table)
# 由于mcfunction不支持动态loot路径,使用条件分支

# 读取item_id
data modify storage stardew:temp item_id set from storage stardew:temp purchase_item.item_id

# === 春季种子 ===
execute if data storage stardew:temp {item_id:"parsnip_seeds"} run loot give @s loot stardew:items/seeds/crop_parsnip
execute if data storage stardew:temp {item_id:"green_bean_starter"} run loot give @s loot stardew:items/seeds/crop_green_bean
execute if data storage stardew:temp {item_id:"cauliflower_seeds"} run loot give @s loot stardew:items/seeds/crop_cauliflower
execute if data storage stardew:temp {item_id:"potato_seeds"} run loot give @s loot stardew:items/seeds/crop_potato
execute if data storage stardew:temp {item_id:"tulip_bulb"} run loot give @s loot stardew:items/seeds/crop_tulip
execute if data storage stardew:temp {item_id:"kale_seeds"} run loot give @s loot stardew:items/seeds/crop_kale
execute if data storage stardew:temp {item_id:"jazz_seeds"} run loot give @s loot stardew:items/seeds/crop_blue_jazz
execute if data storage stardew:temp {item_id:"garlic_seeds"} run loot give @s loot stardew:items/seeds/crop_garlic
execute if data storage stardew:temp {item_id:"strawberry_seeds"} run loot give @s loot stardew:items/seeds/crop_strawberry

# === 夏季种子 ===
execute if data storage stardew:temp {item_id:"melon_seeds"} run loot give @s loot stardew:items/seeds/crop_melon
execute if data storage stardew:temp {item_id:"tomato_seeds"} run loot give @s loot stardew:items/seeds/crop_tomato
execute if data storage stardew:temp {item_id:"blueberry_seeds"} run loot give @s loot stardew:items/seeds/crop_blueberry
execute if data storage stardew:temp {item_id:"pepper_seeds"} run loot give @s loot stardew:items/seeds/crop_hot_pepper
execute if data storage stardew:temp {item_id:"wheat_seeds"} run loot give @s loot stardew:items/seeds/crop_wheat
execute if data storage stardew:temp {item_id:"radish_seeds"} run loot give @s loot stardew:items/seeds/crop_radish
execute if data storage stardew:temp {item_id:"poppy_seeds"} run loot give @s loot stardew:items/seeds/crop_poppy
execute if data storage stardew:temp {item_id:"spangle_seeds"} run loot give @s loot stardew:items/seeds/crop_spangle
execute if data storage stardew:temp {item_id:"hops_starter"} run loot give @s loot stardew:items/seeds/crop_hops
execute if data storage stardew:temp {item_id:"corn_seeds"} run loot give @s loot stardew:items/seeds/crop_corn
execute if data storage stardew:temp {item_id:"sunflower_seeds"} run loot give @s loot stardew:items/seeds/crop_sunflower
execute if data storage stardew:temp {item_id:"red_cabbage_seeds"} run loot give @s loot stardew:items/seeds/crop_red_cabbage

# === 秋季种子 ===
execute if data storage stardew:temp {item_id:"eggplant_seeds"} run loot give @s loot stardew:items/seeds/crop_eggplant
execute if data storage stardew:temp {item_id:"pumpkin_seeds"} run loot give @s loot stardew:items/seeds/crop_pumpkin
execute if data storage stardew:temp {item_id:"bok_choy_seeds"} run loot give @s loot stardew:items/seeds/crop_bok_choy
execute if data storage stardew:temp {item_id:"yam_seeds"} run loot give @s loot stardew:items/seeds/crop_yam
execute if data storage stardew:temp {item_id:"cranberry_seeds"} run loot give @s loot stardew:items/seeds/crop_cranberry
execute if data storage stardew:temp {item_id:"fairy_seeds"} run loot give @s loot stardew:items/seeds/crop_fairy_rose
execute if data storage stardew:temp {item_id:"amaranth_seeds"} run loot give @s loot stardew:items/seeds/crop_amaranth
execute if data storage stardew:temp {item_id:"grape_starter"} run loot give @s loot stardew:items/seeds/crop_grape
execute if data storage stardew:temp {item_id:"artichoke_seeds"} run loot give @s loot stardew:items/seeds/crop_artichoke

# === 冬季种子 ===
execute if data storage stardew:temp {item_id:"winter_seeds"} run loot give @s loot stardew:items/seeds/crop_winter_seeds

# === 全年商品 ===
execute if data storage stardew:temp {item_id:"grass_starter"} run loot give @s loot stardew:items/seeds/grass_starter

# === 烹饪原料 ===
execute if data storage stardew:temp {item_id:"sugar"} run loot give @s loot stardew:items/cooking/sugar
execute if data storage stardew:temp {item_id:"wheat_flour"} run loot give @s loot stardew:items/cooking/wheat_flour
execute if data storage stardew:temp {item_id:"rice"} run loot give @s loot stardew:items/cooking/rice
execute if data storage stardew:temp {item_id:"oil"} run loot give @s loot stardew:items/cooking/oil
execute if data storage stardew:temp {item_id:"vinegar"} run loot give @s loot stardew:items/cooking/vinegar

# === 肥料 ===
execute if data storage stardew:temp {item_id:"basic_fertilizer"} run loot give @s loot stardew:items/fertilizer/basic_fertilizer
execute if data storage stardew:temp {item_id:"quality_fertilizer"} run loot give @s loot stardew:items/fertilizer/quality_fertilizer
execute if data storage stardew:temp {item_id:"basic_retaining_soil"} run loot give @s loot stardew:items/fertilizer/basic_retaining_soil
execute if data storage stardew:temp {item_id:"quality_retaining_soil"} run loot give @s loot stardew:items/fertilizer/quality_retaining_soil
execute if data storage stardew:temp {item_id:"speed_gro"} run loot give @s loot stardew:items/fertilizer/speed_gro
execute if data storage stardew:temp {item_id:"deluxe_speed_gro"} run loot give @s loot stardew:items/fertilizer/deluxe_speed_gro
