# data/stardew/function/menu/storage/pages/set_bag_slot_macro.mcfunction
# 宏函数：设置背包槽位物品（使用原版colored bundle）
# 调用参数: bag_id
# 执行者: item_display实体（通过 as @e[...] 调用）
# 前提: #CurrentPlayer sd_menu_ctrl 和 #PlayerBagCount sd_storage_temp 已设置

# 设置检查的背包ID
$scoreboard players set #CheckingBagID sd_storage_temp $(bag_id)

# 如果背包ID >= 玩家背包数量，显示未解锁 (纸 CMD:16)
execute if score #CheckingBagID sd_storage_temp >= #PlayerBagCount sd_storage_temp run data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":16}}}

# 如果背包ID < 玩家背包数量，显示对应颜色的bundle
# 先获取背包颜色（默认为0）
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp run scoreboard players set #BagColor sd_bag_color 0
$execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp store result score #BagColor sd_bag_color run data get storage stardew:storage bags[$(bag_id)].color

# 根据颜色设置对应的colored bundle
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 0 run data merge entity @s {item:{id:"minecraft:bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 1 run data merge entity @s {item:{id:"minecraft:white_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 2 run data merge entity @s {item:{id:"minecraft:light_gray_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 3 run data merge entity @s {item:{id:"minecraft:gray_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 4 run data merge entity @s {item:{id:"minecraft:black_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 5 run data merge entity @s {item:{id:"minecraft:brown_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 6 run data merge entity @s {item:{id:"minecraft:red_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 7 run data merge entity @s {item:{id:"minecraft:orange_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 8 run data merge entity @s {item:{id:"minecraft:yellow_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 9 run data merge entity @s {item:{id:"minecraft:lime_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 10 run data merge entity @s {item:{id:"minecraft:green_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 11 run data merge entity @s {item:{id:"minecraft:cyan_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 12 run data merge entity @s {item:{id:"minecraft:light_blue_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 13 run data merge entity @s {item:{id:"minecraft:blue_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 14 run data merge entity @s {item:{id:"minecraft:purple_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 15 run data merge entity @s {item:{id:"minecraft:magenta_bundle",count:1}}
execute if score #CheckingBagID sd_storage_temp < #PlayerBagCount sd_storage_temp if score #BagColor sd_bag_color matches 16 run data merge entity @s {item:{id:"minecraft:pink_bundle",count:1}}
