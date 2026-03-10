# data/stardew/functions/farming/visual_update_router.mcfunction
# [执行者: 作物实体]
# 作用：仅刷新模型，不增加年龄

# 春季
execute if entity @s[tag=crop_parsnip] run function stardew:crops/visual/parsnip/visual
execute if entity @s[tag=crop_garlic] run function stardew:crops/visual/garlic/visual
execute if entity @s[tag=crop_potato] run function stardew:crops/visual/potato/visual
execute if entity @s[tag=crop_tulip] run function stardew:crops/visual/tulip/visual
execute if entity @s[tag=crop_kale] run function stardew:crops/visual/kale/visual
execute if entity @s[tag=crop_blue_jazz] run function stardew:crops/visual/blue_jazz/visual
execute if entity @s[tag=crop_green_bean] run function stardew:crops/visual/green_bean/visual
execute if entity @s[tag=crop_strawberry] run function stardew:crops/visual/strawberry/visual
execute if entity @s[tag=crop_coffee_bean] run function stardew:crops/visual/coffee_bean/visual
execute if entity @s[tag=crop_cauliflower] run function stardew:crops/visual/cauliflower/visual
execute if entity @s[tag=crop_carrot] run function stardew:crops/visual/carrot/visual
execute if entity @s[tag=crop_rhubarb] run function stardew:crops/visual/rhubarb/visual

# 夏季
execute if entity @s[tag=crop_wheat] run function stardew:crops/visual/wheat/visual
execute if entity @s[tag=crop_radish] run function stardew:crops/visual/radish/visual
execute if entity @s[tag=crop_red_cabbage] run function stardew:crops/visual/red_cabbage/visual
execute if entity @s[tag=crop_poppy] run function stardew:crops/visual/poppy/visual
execute if entity @s[tag=crop_summer_spangle] run function stardew:crops/visual/summer_spangle/visual
execute if entity @s[tag=crop_tomato] run function stardew:crops/visual/tomato/visual
execute if entity @s[tag=crop_hot_pepper] run function stardew:crops/visual/hot_pepper/visual
execute if entity @s[tag=crop_blueberry] run function stardew:crops/visual/blueberry/visual
execute if entity @s[tag=crop_corn] run function stardew:crops/visual/corn/visual
execute if entity @s[tag=crop_hops] run function stardew:crops/visual/hops/visual
execute if entity @s[tag=crop_melon] run function stardew:crops/visual/melon/visual
execute if entity @s[tag=crop_starfruit] run function stardew:crops/visual/starfruit/visual
execute if entity @s[tag=crop_summer_squash] run function stardew:crops/visual/summer_squash/visual

# 秋季
execute if entity @s[tag=crop_bok_choy] run function stardew:crops/visual/bok_choy/visual
execute if entity @s[tag=crop_eggplant] run function stardew:crops/visual/eggplant/visual
execute if entity @s[tag=crop_yam] run function stardew:crops/visual/yam/visual
execute if entity @s[tag=crop_amaranth] run function stardew:crops/visual/amaranth/visual
execute if entity @s[tag=crop_sunflower] run function stardew:crops/visual/sunflower/visual
execute if entity @s[tag=crop_fairy_rose] run function stardew:crops/visual/fairy_rose/visual
execute if entity @s[tag=crop_cranberry] run function stardew:crops/visual/cranberry/visual
execute if entity @s[tag=crop_grape] run function stardew:crops/visual/grape/visual
execute if entity @s[tag=crop_artichoke] run function stardew:crops/visual/artichoke/visual
execute if entity @s[tag=crop_pumpkin] run function stardew:crops/visual/pumpkin/visual
execute if entity @s[tag=crop_broccoli] run function stardew:crops/visual/broccoli/visual

# 冬季/特殊
execute if entity @s[tag=crop_winter_root] run function stardew:crops/visual/winter_root/visual
execute if entity @s[tag=crop_snow_yam] run function stardew:crops/visual/snow_yam/visual
execute if entity @s[tag=crop_crystal_fruit] run function stardew:crops/visual/crystal_fruit/visual
execute if entity @s[tag=crop_powder_melon] run function stardew:crops/visual/powder_melon/visual
execute if entity @s[tag=crop_ancient_fruit] run function stardew:crops/visual/ancient_fruit/visual
execute if entity @s[tag=crop_ancient_fruit] run function stardew:crops/visual/ancient_fruit/visual