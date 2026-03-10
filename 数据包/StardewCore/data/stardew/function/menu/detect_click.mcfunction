# data/stardew/function/menu/detect_click.mcfunction
# [执行者: interaction实体] 检测右键点击

# 0. 检查是否有交互(没有交互直接返回)
execute unless data entity @s interaction run return 0

# 0.5 检查玩家点击冷却（防止连续点击触发多次）
execute store result score #TempEntity sd_menu_ctrl run scoreboard players get @s sd_menu_entity_num
execute if score #TempEntity sd_menu_ctrl matches 1.. on target if score @s sd_menu_click_cd matches 1.. run data remove entity @s interaction
execute if score #TempEntity sd_menu_ctrl matches 1.. on target if score @s sd_menu_click_cd matches 1.. run return 0

# 1. 检查玩家编号是否匹配
scoreboard players set #TempClick sd_menu_ctrl 0
execute on target if score @s sd_menu_sequence = #TempEntity sd_menu_ctrl run scoreboard players set #TempClick sd_menu_ctrl 1

# 1.5 设置点击冷却 (10 ticks = 0.5秒)
execute if score #TempClick sd_menu_ctrl matches 1 on target run scoreboard players set @s sd_menu_click_cd 10

# 2. 如果是翻页按钮,执行翻页
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_page_next] on target run function stardew:menu/page_next
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_page_prev] on target run function stardew:menu/page_prev

# 2.5 如果是菜单按钮,执行相应功能

## 主菜单 (level=0)
# slot 2: 合成菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 0 run function stardew:menu/pages/crafting_menu
# slot 3: 装备菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 0 run function stardew:menu/pages/equipment_menu
# slot 4: 居民菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 0 run function stardew:menu/pages/npc_list
# slot 5: 作弊模式
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 0 unless score @s sd_menu_state matches 1.. run function stardew:menu/buttons/debug_mode
# slot 6: 设置菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 0 unless score @s sd_menu_state matches 1.. run function stardew:menu/pages/settings_menu
# slot 7: 存储菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 0 unless score @s sd_menu_state matches 1.. run function stardew:menu/storage/open_storage_menu
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 0 unless score @s sd_menu_state matches 1.. run function stardew:menu/toggle

## Debug主菜单 (level=1)
# slot 2: 季节菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 1 run function stardew:menu/pages/season_menu
# slot 3: 天气菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 1 run function stardew:menu/pages/weather_menu
# slot 4: 时间菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 1 run function stardew:menu/pages/time_menu
# slot 5: 物品菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 1 run function stardew:menu/buttons/items_menu
# slot 6: 等级菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 1 run function stardew:menu/pages/level_menu
# slot 7: 返回主菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 1 run function stardew:menu/pages/load_main
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 1 run function stardew:menu/toggle

## 居民菜单 (level=4)
# slot 1: 阿比盖尔 - 显示详细信息
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_1] on target if score @s sd_menu_level matches 4 run function stardew:menu/npc/show_abigail_info
# slot 2-7: 预留给其他NPC
# slot 8: 返回主菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 4 run function stardew:menu/pages/load_main
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 4 run function stardew:menu/toggle

## 背包列表菜单 (level=26)
# slot 1-7: 选择背包
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_1] on target if score @s sd_menu_level matches 26 run function stardew:menu/storage/buttons/select_bag_1
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 26 run function stardew:menu/storage/buttons/select_bag_2
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 26 run function stardew:menu/storage/buttons/select_bag_3
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 26 run function stardew:menu/storage/buttons/select_bag_4
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 26 run function stardew:menu/storage/buttons/select_bag_5
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 26 run function stardew:menu/storage/buttons/select_bag_6
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 26 run function stardew:menu/storage/buttons/select_bag_7
# slot 8: 返回主菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 26 run function stardew:menu/pages/load_main
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 26 run function stardew:menu/toggle

## 背包详细菜单 (level=261)
# slot 3: 打开背包
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 261 run function stardew:menu/storage/buttons/open_bag
# slot 4: 重命名
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 261 run function stardew:menu/storage/buttons/rename_bag
# slot 5: 换色
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 261 run function stardew:menu/storage/pages/color_menu
# slot 6: 返回背包列表
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 261 run function stardew:menu/storage/buttons/back_to_list
# slot 7: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 261 run function stardew:menu/toggle

## 颜色选择菜单 (level=262)
# slot 1-7: 选择颜色（根据sd_menu_page判断）
# 第0页: 颜色0-6
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_1] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 0 run function stardew:menu/storage/buttons/select_color_0
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 0 run function stardew:menu/storage/buttons/select_color_1
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 0 run function stardew:menu/storage/buttons/select_color_2
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 0 run function stardew:menu/storage/buttons/select_color_3
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 0 run function stardew:menu/storage/buttons/select_color_4
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 0 run function stardew:menu/storage/buttons/select_color_5
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 0 run function stardew:menu/storage/buttons/select_color_6
# 第1页: 颜色7-13
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_1] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 1 run function stardew:menu/storage/buttons/select_color_7
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 1 run function stardew:menu/storage/buttons/select_color_8
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 1 run function stardew:menu/storage/buttons/select_color_9
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 1 run function stardew:menu/storage/buttons/select_color_10
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 1 run function stardew:menu/storage/buttons/select_color_11
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 1 run function stardew:menu/storage/buttons/select_color_12
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 1 run function stardew:menu/storage/buttons/select_color_13
# 第2页: 颜色14-16
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_1] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 2 run function stardew:menu/storage/buttons/select_color_14
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 2 run function stardew:menu/storage/buttons/select_color_15
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 262 if score @s sd_menu_page matches 2 run function stardew:menu/storage/buttons/select_color_16
# slot 8: 返回背包详情
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 262 run function stardew:menu/storage/pages/bag_detail
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 262 run function stardew:menu/toggle

## 装备菜单 (level=2)
# slot 1-5: 检测Shift+右键卸下装备
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_1] on target if score @s sd_menu_level matches 2 if predicate stardew:is_sneaking run function stardew:equipment/interact/unequip_boots
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 2 if predicate stardew:is_sneaking run function stardew:equipment/interact/unequip_ring1
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 2 if predicate stardew:is_sneaking run function stardew:equipment/interact/unequip_ring2
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 2 if predicate stardew:is_sneaking run function stardew:equipment/interact/unequip_ring3
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 2 if predicate stardew:is_sneaking run function stardew:equipment/interact/unequip_ring4
# slot 8: 返回主菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 2 run function stardew:menu/pages/load_main
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 2 run function stardew:menu/toggle

## 季节子菜单 (level=21)
# slot 3: 切换春季
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 21 run function stardew:menu/buttons/season_spring
# slot 4: 切换夏季
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 21 run function stardew:menu/buttons/season_summer
# slot 5: 切换秋季
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 21 run function stardew:menu/buttons/season_fall
# slot 6: 切换冬季
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 21 run function stardew:menu/buttons/season_winter
# slot 7: 返回Debug菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 21 run function stardew:menu/pages/load_debug
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 21 run function stardew:menu/toggle

## 天气子菜单 (level=22)
# slot 3: 切换晴天
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 22 run function stardew:menu/buttons/weather_sunny
# slot 4: 切换雨天
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 22 run function stardew:menu/buttons/weather_rain
# slot 5: 切换雷雨
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 22 run function stardew:menu/buttons/weather_thunder
# slot 6: 切换下雪
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 22 run function stardew:menu/buttons/weather_snow
# slot 7: 返回Debug菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 22 run function stardew:menu/pages/load_debug
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 22 run function stardew:menu/toggle

## 时间调整菜单 (level=24)
# slot 2: 快进一天
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 24 run function stardew:menu/buttons/time_forward_day
# slot 3: 倒退一天
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 24 run function stardew:menu/buttons/time_backward_day
# slot 4: 快进一季
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 24 run function stardew:menu/buttons/time_forward_season
# slot 5: 倒退一季
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 24 run function stardew:menu/buttons/time_backward_season
# slot 6: 快进一小时
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 24 run function stardew:menu/buttons/time_forward_hour
# slot 7: 倒退一小时
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 24 run function stardew:menu/buttons/time_backward_hour
# slot 8: 返回Debug菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 24 run function stardew:menu/pages/load_debug
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 24 run function stardew:menu/toggle

## 设置菜单 (level=25)
# slot 1: 金币显示开关
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_1] on target if score @s sd_menu_level matches 25 run function stardew:menu/buttons/toggle_gold_display
# slot 8: 返回主菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 25 run function stardew:menu/pages/load_main
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 25 run function stardew:menu/toggle

## 等级选择菜单 (level=23)
# slot 2: 耕种等级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 23 run function stardew:menu/pages/level_farming
# slot 3: 战斗等级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 23 run function stardew:menu/pages/level_combat
# slot 4: 钓鱼等级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 23 run function stardew:menu/pages/level_fishing
# slot 5: 采集等级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 23 run function stardew:menu/pages/level_foraging
# slot 6: 挖矿等级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 23 run function stardew:menu/pages/level_mining
# slot 7: 返回Debug菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 23 run function stardew:menu/pages/load_debug
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 23 run function stardew:menu/toggle

## 耕种等级调整菜单 (level=231)
# slot 4: 升级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 231 run function stardew:menu/buttons/level_up_farming
# slot 5: 降级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 231 run function stardew:menu/buttons/level_down_farming
# slot 6: 返回等级菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 231 run function stardew:menu/pages/level_menu
# slot 7: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 231 run function stardew:menu/toggle

## 战斗等级调整菜单 (level=232)
# slot 4: 升级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 232 run function stardew:menu/buttons/level_up_combat
# slot 5: 降级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 232 run function stardew:menu/buttons/level_down_combat
# slot 6: 返回等级菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 232 run function stardew:menu/pages/level_menu
# slot 7: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 232 run function stardew:menu/toggle

## 钓鱼等级调整菜单 (level=233)
# slot 4: 升级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 233 run function stardew:menu/buttons/level_up_fishing
# slot 5: 降级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 233 run function stardew:menu/buttons/level_down_fishing
# slot 6: 返回等级菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 233 run function stardew:menu/pages/level_menu
# slot 7: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 233 run function stardew:menu/toggle

## 采集等级调整菜单 (level=234)
# slot 4: 升级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 234 run function stardew:menu/buttons/level_up_foraging
# slot 5: 降级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 234 run function stardew:menu/buttons/level_down_foraging
# slot 6: 返回等级菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 234 run function stardew:menu/pages/level_menu
# slot 7: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 234 run function stardew:menu/toggle

## 挖矿等级调整菜单 (level=235)
# slot 4: 升级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 235 run function stardew:menu/buttons/level_up_mining
# slot 5: 降级
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 235 run function stardew:menu/buttons/level_down_mining
# slot 6: 返回等级菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 235 run function stardew:menu/pages/level_menu
# slot 7: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 235 run function stardew:menu/toggle

## 物品主菜单 (level=10)
# 第0页
# slot 1: 作物子菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_1] on target if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 0 run function stardew:menu/buttons/items_crops
# slot 2: 鱼类子菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 0 run function stardew:menu/buttons/items_fish
# slot 3: 种子子菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 0 run function stardew:menu/buttons/items_seeds
# slot 4: 工具子菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 0 run function stardew:menu/buttons/items_tools
# slot 5: 宝石 - 直接获取
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 0 run function stardew:menu/buttons/items_gems
# slot 6: 渔具 - 直接获取
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 0 run function stardew:menu/buttons/items_fishing
# slot 7: 资源 - 直接获取
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 0 run function stardew:menu/buttons/items_resource
# slot 8: 返回Debug菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 0 run function stardew:menu/pages/load_debug
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 0 run function stardew:menu/toggle

# 第1页
# slot 1: Debug工具 - 直接获取
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_1] on target if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 1 run function stardew:menu/buttons/items_debug
# slot 2: 矿物生成 - 直接获取
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 1 run function stardew:menu/buttons/items_debug_mining
# slot 8: 返回Debug菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 1 run function stardew:menu/pages/load_debug
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 10 if score @s sd_menu_page matches 1 run function stardew:menu/toggle

## 作物子菜单 (level=11)
# slot 3: 春季作物
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 11 run function stardew:menu/pages/items_crops_spring
# slot 4: 夏季作物
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 11 run function stardew:menu/pages/items_crops_summer
# slot 5: 秋季作物
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 11 run function stardew:menu/pages/items_crops_fall
# slot 6: 冬季作物
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 11 run function stardew:menu/pages/items_crops_winter
# slot 7: 返回物品主菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 11 run function stardew:menu/buttons/items_main
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 11 run function stardew:menu/toggle

## 鱼类子菜单 (level=12)
# slot 1: 春季鱼类
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_1] on target if score @s sd_menu_level matches 12 run function stardew:menu/buttons/items_fish_spring
# slot 2: 夏季鱼类
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 12 run function stardew:menu/buttons/items_fish_summer
# slot 3: 秋季鱼类
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 12 run function stardew:menu/buttons/items_fish_fall
# slot 4: 冬季鱼类
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 12 run function stardew:menu/buttons/items_fish_winter
# slot 5: 特殊鱼类
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 12 run function stardew:menu/buttons/items_fish_special
# slot 6: 垃圾物品
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 12 run function stardew:menu/buttons/get_fish_trash
# slot 7: 资源物品
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 12 run function stardew:menu/buttons/get_fish_resource
# slot 8: 返回物品主菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 12 run function stardew:menu/buttons/items_main
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 12 run function stardew:menu/toggle

## 种子子菜单 (level=13)
# slot 2: 春季种子
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 13 run function stardew:menu/buttons/get_seeds_spring
# slot 3: 夏季种子
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 13 run function stardew:menu/buttons/get_seeds_summer
# slot 4: 秋季种子
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 13 run function stardew:menu/buttons/get_seeds_fall
# slot 5: 冬季种子
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 13 run function stardew:menu/buttons/get_seeds_winter
# slot 6: 树木种子
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 13 run function stardew:menu/buttons/get_seeds_tree
# slot 7: 返回物品主菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 13 run function stardew:menu/buttons/items_main
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 13 run function stardew:menu/toggle

## 工具子菜单 (level=14)
# slot 1: 斧子
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_1] on target if score @s sd_menu_level matches 14 run function stardew:menu/buttons/get_tools_axe
# slot 2: 锄头
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 14 run function stardew:menu/buttons/get_tools_hoe
# slot 3: 镐子
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 14 run function stardew:menu/buttons/get_tools_pickaxe
# slot 4: 镰刀
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 14 run function stardew:menu/buttons/get_tools_scythe
# slot 5: 水壶
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 14 run function stardew:menu/buttons/get_tools_watering_can
# slot 6: 武器 (占位，暂无功能)
# slot 7: 防具 (占位，暂无功能)
# slot 8: 返回物品主菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 14 run function stardew:menu/buttons/items_main
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 14 run function stardew:menu/toggle

## 春季作物品质选择 (level=111)
# slot 3: 普通品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 111 run function stardew:menu/buttons/get_crops_spring_base
# slot 4: 银星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 111 run function stardew:menu/buttons/get_crops_spring_silver
# slot 5: 金星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 111 run function stardew:menu/buttons/get_crops_spring_gold
# slot 6: 钻石星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 111 run function stardew:menu/buttons/get_crops_spring_diamond
# slot 7: 返回作物菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 111 run function stardew:menu/buttons/items_crops
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 111 run function stardew:menu/toggle

## 夏季作物品质选择 (level=112)
# slot 3: 普通品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 112 run function stardew:menu/buttons/get_crops_summer_base
# slot 4: 银星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 112 run function stardew:menu/buttons/get_crops_summer_silver
# slot 5: 金星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 112 run function stardew:menu/buttons/get_crops_summer_gold
# slot 6: 钻石星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 112 run function stardew:menu/buttons/get_crops_summer_diamond
# slot 7: 返回作物菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 112 run function stardew:menu/buttons/items_crops
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 112 run function stardew:menu/toggle

## 秋季作物品质选择 (level=113)
# slot 3: 普通品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 113 run function stardew:menu/buttons/get_crops_fall_base
# slot 4: 银星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 113 run function stardew:menu/buttons/get_crops_fall_silver
# slot 5: 金星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 113 run function stardew:menu/buttons/get_crops_fall_gold
# slot 6: 钻石星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 113 run function stardew:menu/buttons/get_crops_fall_diamond
# slot 7: 返回作物菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 113 run function stardew:menu/buttons/items_crops
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 113 run function stardew:menu/toggle

## 冬季作物品质选择 (level=114)
# slot 3: 普通品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 114 run function stardew:menu/buttons/get_crops_winter_base
# slot 4: 银星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 114 run function stardew:menu/buttons/get_crops_winter_silver
# slot 5: 金星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 114 run function stardew:menu/buttons/get_crops_winter_gold
# slot 6: 钻石星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 114 run function stardew:menu/buttons/get_crops_winter_diamond
# slot 7: 返回作物菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 114 run function stardew:menu/buttons/items_crops
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 114 run function stardew:menu/toggle

## 春季鱼类品质选择 (level=121)
# slot 3: 普通品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 121 run function stardew:menu/buttons/get_fish_spring_base
# slot 4: 银星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 121 run function stardew:menu/buttons/get_fish_spring_silver
# slot 5: 金星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 121 run function stardew:menu/buttons/get_fish_spring_gold
# slot 6: 钻石星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 121 run function stardew:menu/buttons/get_fish_spring_diamond
# slot 7: 返回鱼类菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 121 run function stardew:menu/buttons/items_fish
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 121 run function stardew:menu/toggle

## 夏季鱼类品质选择 (level=122)
# slot 3: 普通品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 122 run function stardew:menu/buttons/get_fish_summer_base
# slot 4: 银星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 122 run function stardew:menu/buttons/get_fish_summer_silver
# slot 5: 金星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 122 run function stardew:menu/buttons/get_fish_summer_gold
# slot 6: 钻石星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 122 run function stardew:menu/buttons/get_fish_summer_diamond
# slot 7: 返回鱼类菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 122 run function stardew:menu/buttons/items_fish
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 122 run function stardew:menu/toggle

## 秋季鱼类品质选择 (level=123)
# slot 3: 普通品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 123 run function stardew:menu/buttons/get_fish_fall_base
# slot 4: 银星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 123 run function stardew:menu/buttons/get_fish_fall_silver
# slot 5: 金星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 123 run function stardew:menu/buttons/get_fish_fall_gold
# slot 6: 钻石星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 123 run function stardew:menu/buttons/get_fish_fall_diamond
# slot 7: 返回鱼类菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 123 run function stardew:menu/buttons/items_fish
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 123 run function stardew:menu/toggle

## 冬季鱼类品质选择 (level=124)
# slot 3: 普通品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 124 run function stardew:menu/buttons/get_fish_winter_base
# slot 4: 银星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 124 run function stardew:menu/buttons/get_fish_winter_silver
# slot 5: 金星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 124 run function stardew:menu/buttons/get_fish_winter_gold
# slot 6: 钻石星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 124 run function stardew:menu/buttons/get_fish_winter_diamond
# slot 7: 返回鱼类菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 124 run function stardew:menu/buttons/items_fish
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 124 run function stardew:menu/toggle

## 特殊鱼类品质选择 (level=125)
# slot 3: 普通品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 125 run function stardew:menu/buttons/get_fish_special_base
# slot 4: 银星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 125 run function stardew:menu/buttons/get_fish_special_silver
# slot 5: 金星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 125 run function stardew:menu/buttons/get_fish_special_gold
# slot 6: 钻石星品质
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_6] on target if score @s sd_menu_level matches 125 run function stardew:menu/buttons/get_fish_special_diamond
# slot 7: 返回鱼类菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_7] on target if score @s sd_menu_level matches 125 run function stardew:menu/buttons/items_fish
# slot 8: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 125 run function stardew:menu/toggle

## 合成菜单 (level=30)
# slot 1: 工具分类
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_1] on target if score @s sd_menu_level matches 30 run function stardew:menu/pages/tools_recipes
# slot 2: 设备分类
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 30 run function stardew:menu/pages/equipment_recipes
# slot 3: 建筑分类
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 30 run function stardew:menu/pages/building_recipes
# slot 4: 消耗品分类
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_4] on target if score @s sd_menu_level matches 30 run function stardew:menu/pages/consumable_recipes
# slot 5: 家具分类
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_5] on target if score @s sd_menu_level matches 30 run function stardew:menu/pages/furniture_recipes
# slot 8: 返回主菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 30 run function stardew:menu/pages/load_main
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 30 run function stardew:menu/toggle

## 设备配方子菜单 (level=302)
# slot 1: 合成熔炉
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_1] on target if score @s sd_menu_level matches 302 run function stardew:crafting/craft_furnace
# slot 2: 合成箱子
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_2] on target if score @s sd_menu_level matches 302 run function stardew:crafting/craft_chest
# slot 3: 合成小桶
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_3] on target if score @s sd_menu_level matches 302 run function stardew:crafting/craft_keg
# slot 8: 返回合成菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 302 run function stardew:menu/pages/crafting_menu
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 302 run function stardew:menu/toggle

## 工具配方子菜单 (level=301)
# slot 8: 返回合成菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 301 run function stardew:menu/pages/crafting_menu
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 301 run function stardew:menu/toggle

## 建筑配方子菜单 (level=303)
# slot 8: 返回合成菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 303 run function stardew:menu/pages/crafting_menu
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 303 run function stardew:menu/toggle

## 消耗品配方子菜单 (level=304)
# slot 8: 返回合成菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 304 run function stardew:menu/pages/crafting_menu
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 304 run function stardew:menu/toggle

## 家具配方子菜单 (level=305)
# slot 8: 返回合成菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_8] on target if score @s sd_menu_level matches 305 run function stardew:menu/pages/crafting_menu
# slot 9: 关闭菜单
execute if score #TempClick sd_menu_ctrl matches 1 if entity @s[tag=sd_menu_btn_click,tag=sd_btn_slot_9] on target if score @s sd_menu_level matches 305 run function stardew:menu/toggle

# 3. 播放点击音效
execute if score #TempClick sd_menu_ctrl matches 1 on target at @s run playsound ui.button.click player @s ~ ~ ~ 1 1

# 4. 重置交互时间戳(防止疯狂触发)
data remove entity @s interaction
