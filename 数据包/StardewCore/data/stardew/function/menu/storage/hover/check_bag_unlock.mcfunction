# data/stardew/function/menu/storage/hover/check_bag_unlock.mcfunction
# 检查背包槽位是否已解锁，显示对应的hover文本
# [执行者: item_display按钮]

# 获取按钮的entity_num作为#Temp
execute store result score #Temp sd_menu_ctrl run scoreboard players get @s sd_menu_entity_num

# 获取玩家背包数量和当前页码
execute store result score #PlayerBagCount sd_storage_temp run scoreboard players get @a[scores={sd_menu_sequence=1..},limit=1] sd_bag_count
execute store result score #CurrentPage sd_storage_temp run scoreboard players get @a[scores={sd_menu_sequence=1..},limit=1] sd_menu_page

# 计算当前槽位对应的背包ID（槽位1-7对应背包0-6，加上页码*7）
scoreboard players operation #SlotBagID sd_storage_temp = #CurrentPage sd_storage_temp
scoreboard players operation #SlotBagID sd_storage_temp *= #7 sd_const
scoreboard players operation #SlotBagID sd_storage_temp += @s sd_menu_slot
scoreboard players remove #SlotBagID sd_storage_temp 1

# 如果背包ID >= 玩家背包数量，显示"未解锁"
execute if score #SlotBagID sd_storage_temp >= #PlayerBagCount sd_storage_temp as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run data modify entity @s text set value '{"text":"未解锁","color":"gray","bold":true}'

# 如果背包ID < 玩家背包数量，显示背包名称
execute if score #SlotBagID sd_storage_temp < #PlayerBagCount sd_storage_temp store result storage stardew:temp macro.bag_id int 1 run scoreboard players get #SlotBagID sd_storage_temp
scoreboard players add #SlotBagID sd_storage_temp 1
execute store result storage stardew:temp macro.display_num int 1 run scoreboard players get #SlotBagID sd_storage_temp
scoreboard players remove #SlotBagID sd_storage_temp 1
execute if score #SlotBagID sd_storage_temp < #PlayerBagCount sd_storage_temp run function stardew:menu/storage/hover/display_bag_name_macro with storage stardew:temp macro
