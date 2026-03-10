# data/stardew/function/utility/keg/interact_handler2.mcfunction
# 处理玩家与小桶的右键交互 - 执行者: interaction 实体 (@s)

tag @s add sd_interacting_keg

# 1. 如果小桶已完成(状态为2),收取产物
execute if score @s sd_keg_state matches 2 on target run function stardew:utility/keg/collect_product
execute if score @s sd_keg_state matches 2 run tag @s remove sd_interacting_keg
execute if score @s sd_keg_state matches 2 run return 1

# 2. 如果小桶空闲,检查玩家手持物品 - 28种作物x4品质
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1304] run function stardew:utility/keg/process_strawberry
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1305] run function stardew:utility/keg/process_strawberry
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1306] run function stardew:utility/keg/process_strawberry
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1307] run function stardew:utility/keg/process_strawberry
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2804] run function stardew:utility/keg/process_blueberry
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2805] run function stardew:utility/keg/process_blueberry
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2806] run function stardew:utility/keg/process_blueberry
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2807] run function stardew:utility/keg/process_blueberry
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3904] run function stardew:utility/keg/process_cranberry
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3905] run function stardew:utility/keg/process_cranberry
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3906] run function stardew:utility/keg/process_cranberry
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3907] run function stardew:utility/keg/process_cranberry
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4004] run function stardew:utility/keg/process_grape
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4005] run function stardew:utility/keg/process_grape
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4006] run function stardew:utility/keg/process_grape
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4007] run function stardew:utility/keg/process_grape
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4604] run function stardew:utility/keg/process_ancient_fruit
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4605] run function stardew:utility/keg/process_ancient_fruit
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4606] run function stardew:utility/keg/process_ancient_fruit
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4607] run function stardew:utility/keg/process_ancient_fruit
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2704] run function stardew:utility/keg/process_hot_pepper
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2705] run function stardew:utility/keg/process_hot_pepper
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2706] run function stardew:utility/keg/process_hot_pepper
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2707] run function stardew:utility/keg/process_hot_pepper
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3204] run function stardew:utility/keg/process_starfruit
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3205] run function stardew:utility/keg/process_starfruit
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3206] run function stardew:utility/keg/process_starfruit
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3207] run function stardew:utility/keg/process_starfruit
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1124] run function stardew:utility/keg/process_rhubarb
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1125] run function stardew:utility/keg/process_rhubarb
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1126] run function stardew:utility/keg/process_rhubarb
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1127] run function stardew:utility/keg/process_rhubarb
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3104] run function stardew:utility/keg/process_melon
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3105] run function stardew:utility/keg/process_melon
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3106] run function stardew:utility/keg/process_melon
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3107] run function stardew:utility/keg/process_melon
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4204] run function stardew:utility/keg/process_pumpkin
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4205] run function stardew:utility/keg/process_pumpkin
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4206] run function stardew:utility/keg/process_pumpkin
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4207] run function stardew:utility/keg/process_pumpkin
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1104] run function stardew:utility/keg/process_wheat
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1105] run function stardew:utility/keg/process_wheat
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1106] run function stardew:utility/keg/process_wheat
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1107] run function stardew:utility/keg/process_wheat
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3004] run function stardew:utility/keg/process_hops
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3005] run function stardew:utility/keg/process_hops
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3006] run function stardew:utility/keg/process_hops
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3007] run function stardew:utility/keg/process_hops
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2104] run function stardew:utility/keg/process_coffee_bean
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2105] run function stardew:utility/keg/process_coffee_bean
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2106] run function stardew:utility/keg/process_coffee_bean
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2107] run function stardew:utility/keg/process_coffee_bean
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1204] run function stardew:utility/keg/process_tomato
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1205] run function stardew:utility/keg/process_tomato
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1206] run function stardew:utility/keg/process_tomato
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1207] run function stardew:utility/keg/process_tomato
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2904] run function stardew:utility/keg/process_corn
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2905] run function stardew:utility/keg/process_corn
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2906] run function stardew:utility/keg/process_corn
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2907] run function stardew:utility/keg/process_corn
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1604] run function stardew:utility/keg/process_potato
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1605] run function stardew:utility/keg/process_potato
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1606] run function stardew:utility/keg/process_potato
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1607] run function stardew:utility/keg/process_potato
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2204] run function stardew:utility/keg/process_cauliflower
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2205] run function stardew:utility/keg/process_cauliflower
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2206] run function stardew:utility/keg/process_cauliflower
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2207] run function stardew:utility/keg/process_cauliflower
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1504] run function stardew:utility/keg/process_parsnip
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1505] run function stardew:utility/keg/process_parsnip
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1506] run function stardew:utility/keg/process_parsnip
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1507] run function stardew:utility/keg/process_parsnip
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2004] run function stardew:utility/keg/process_green_bean
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2005] run function stardew:utility/keg/process_green_bean
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2006] run function stardew:utility/keg/process_green_bean
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2007] run function stardew:utility/keg/process_green_bean
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1804] run function stardew:utility/keg/process_kale
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1805] run function stardew:utility/keg/process_kale
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1806] run function stardew:utility/keg/process_kale
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1807] run function stardew:utility/keg/process_kale
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1404] run function stardew:utility/keg/process_garlic
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1405] run function stardew:utility/keg/process_garlic
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1406] run function stardew:utility/keg/process_garlic
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=1407] run function stardew:utility/keg/process_garlic
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2404] run function stardew:utility/keg/process_red_cabbage
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2405] run function stardew:utility/keg/process_red_cabbage
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2406] run function stardew:utility/keg/process_red_cabbage
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2407] run function stardew:utility/keg/process_red_cabbage
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3604] run function stardew:utility/keg/process_amaranth
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3605] run function stardew:utility/keg/process_amaranth
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3606] run function stardew:utility/keg/process_amaranth
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3607] run function stardew:utility/keg/process_amaranth
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3304] run function stardew:utility/keg/process_bok_choy
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3305] run function stardew:utility/keg/process_bok_choy
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3306] run function stardew:utility/keg/process_bok_choy
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3307] run function stardew:utility/keg/process_bok_choy
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2304] run function stardew:utility/keg/process_radish
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2305] run function stardew:utility/keg/process_radish
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2306] run function stardew:utility/keg/process_radish
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2307] run function stardew:utility/keg/process_radish
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3504] run function stardew:utility/keg/process_yam
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3505] run function stardew:utility/keg/process_yam
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3506] run function stardew:utility/keg/process_yam
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3507] run function stardew:utility/keg/process_yam
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4104] run function stardew:utility/keg/process_artichoke
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4105] run function stardew:utility/keg/process_artichoke
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4106] run function stardew:utility/keg/process_artichoke
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=4107] run function stardew:utility/keg/process_artichoke
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3404] run function stardew:utility/keg/process_eggplant
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3405] run function stardew:utility/keg/process_eggplant
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3406] run function stardew:utility/keg/process_eggplant
execute if score @s sd_keg_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=3407] run function stardew:utility/keg/process_eggplant

tag @s remove sd_interacting_keg
