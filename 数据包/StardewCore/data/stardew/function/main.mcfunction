# data/stardew/functions/main.mcfunction



# =================================================
# 0. 状态效果系统 tick
# =================================================
function stardew:status/tick

# [DEBUG] Main测试计数器
execute if score #main_test sd_test_main matches 0.. run scoreboard players add #main_test sd_test_main 1

# =================================================
# 0. 新玩家初始化检测
# =================================================
# 检测新加入的玩家并初始化他们的属性
execute as @a unless score @s sd_max_health matches 1.. run scoreboard players set @s sd_max_health 400
execute as @a unless score @s sd_health matches 0.. run scoreboard players set @s sd_health 400
execute as @a unless score @s sd_max_energy matches 1.. run scoreboard players set @s sd_max_energy 400
execute as @a unless score @s sd_energy matches 0.. run scoreboard players set @s sd_energy 400
execute as @a unless score @s sd_gold matches 0.. run scoreboard players set @s sd_gold 0
execute as @a unless score @s sd_fishing_lvl matches 0.. run scoreboard players set @s sd_fishing_lvl 0
execute as @a unless score @s sd_fishing_xp matches 0.. run scoreboard players set @s sd_fishing_xp 0
execute as @a unless score @s sd_farming_lvl matches 0.. run scoreboard players set @s sd_farming_lvl 0
execute as @a unless score @s sd_farming_xp matches 0.. run scoreboard players set @s sd_farming_xp 0
execute as @a unless score @s sd_mining_lvl matches 0.. run scoreboard players set @s sd_mining_lvl 0
execute as @a unless score @s sd_mining_xp matches 0.. run scoreboard players set @s sd_mining_xp 0
execute as @a unless score @s sd_show_gold matches 0.. run scoreboard players set @s sd_show_gold 1

# 强制玩家经验等级和经验条保持为0
execute as @a run xp set @s 0 levels
execute as @a run xp set @s 0 points

# =================================================
# 1. 全局计时器
# =================================================
scoreboard players add Global sd_tick_counter 1
execute if score Global sd_tick_counter matches 17.. run function stardew:time/calc

# =================================================
# 1.5 战斗系统
# =================================================
function stardew:combat/tick

# =================================================
# 1.6 全局DPS统计
# =================================================
execute as @a run function stardew:debug/dps/tick

# =================================================
# 1.7 装备系统
# =================================================
execute as @a run function stardew:equipment/tick

# =================================================
# 2. UI 计算逻辑 (含 10分钟取整算法)
# =================================================

scoreboard objectives add sd_ui_hour dummy
scoreboard objectives add sd_ui_min dummy
scoreboard objectives add sd_const dummy
scoreboard players set #60 sd_const 60
scoreboard players set #10 sd_const 10

execute unless score Global sd_ui_hour matches 0..24 run scoreboard players set Global sd_ui_hour 6
execute unless score Global sd_ui_min matches 0..60 run scoreboard players set Global sd_ui_min 0

execute if score Global sd_time matches 0.. run scoreboard players operation Global sd_ui_hour = Global sd_time
execute if score Global sd_ui_hour matches 0.. run scoreboard players operation Global sd_ui_hour /= #60 sd_const

execute if score Global sd_time matches 0.. run scoreboard players operation Global sd_ui_min = Global sd_time
execute if score Global sd_ui_min matches 0.. run scoreboard players operation Global sd_ui_min %= #60 sd_const

execute if score Global sd_ui_min matches 0.. run scoreboard players operation Global sd_ui_min /= #10 sd_const
execute if score Global sd_ui_min matches 0.. run scoreboard players operation Global sd_ui_min *= #10 sd_const

execute if score Global sd_ui_hour matches 24.. run scoreboard players remove Global sd_ui_hour 24

# UI 更新
# 每个玩家更新一次自己的 UI 逻辑
# (注：如果你发现侧边栏闪烁，说明多人模式下大家都想改侧边栏，那时我们再优化)
execute as @a run function stardew:ui/tick

# Actionbar 状态栏
execute as @a run function stardew:ui/actionbar

# 能量系统管理
execute as @a run function stardew:energy/manage

# =================================================
# 4. 交互与系统
# =================================================
execute as @a at @s run function stardew:interact

# =================================================
# 4.3 博物馆鉴定系统
# =================================================
execute as @a at @s run function stardew:museum/detect_offhand

# =================================================
# 4.5 食物系统（在消费前存储数据）
# =================================================
function stardew:food/tick

# =================================================
# 5. 钓鱼系统
# =================================================

function stardew:fishing/init_bobber
execute as @a at @s run function stardew:fishing/fight_check
execute as @e[type=fishing_bobber] run function stardew:fishing/hook_check
execute as @a[tag=is_fighting_fish] at @s run function stardew:fishing/pull_logic
execute as @a at @s run function stardew:fishing/level_up

# =================================================
# 5.5. 农耕系统等级检测
# =================================================
execute as @a at @s run function stardew:farming/level_up

# =================================================
# 5.6. 战斗系统等级检测
# =================================================
execute as @a at @s run function stardew:combat/level_up

# =================================================
# 6. 挖矿系统
# =================================================
function stardew:mining/tick

# =================================================
# 6.1 矿洞系统
# =================================================
function stardew:mine/tick

# =================================================
# 6.2 怪物系统
# =================================================
function stardew:monsters/tick

# =================================================
# 6.5 实用设施系统
# =================================================
function stardew:utility/tick

# =================================================
# 6.6 菜单UI系统
# =================================================
function stardew:menu/trigger_check
function stardew:menu/regive_check

# =================================================
# 6.7 存储系统 - 新版本
# =================================================
function stardew:menu/storage/tick_cart
function stardew:menu/storage/check_rename

# =================================================
# 7. NPC系统
# =================================================
function stardew:npc/tick

# 7.5 检测玩家维度变化并同步NPC
function stardew:npc/detect_dimension_change

# 冷却减少
execute as @a if score @s sd_regive_cd matches 1.. run scoreboard players remove @s sd_regive_cd 1

# =================================================
# 6.7 畜牧业系统
# =================================================
function stardew:animal/core/tick

# =================================================
# 6.8 对话系统
# =================================================
execute as @a[tag=in_dialogue] at @s run function stardew:dialogue/player_tick
function stardew:dialogue/interact_menu

# =================================================
# 6.9 建筑进入/出口系统
# =================================================
# 检测玩家右键门进入建筑
function stardew:building/door_interact
# 检测玩家右键出口离开建筑
function stardew:building/exit/check_exit

# =================================================
# 7. 其他逻辑
# =================================================
execute as @e[type=interaction,tag=sd_shipping_bin] run function stardew:economy/shipping_bin_handler
execute as @a run function stardew:control/sneak_update
execute as @e[type=interaction,tag=sd_tree] at @s run function stardew:tree/main_handler
execute as @e[type=interaction,tag=weed_hitbox] at @s run function stardew:weeds/check_damage
execute as @e[type=interaction,tag=grass_hitbox] at @s run function stardew:grass/check_damage

execute as @a run function stardew:tools/charge_system
function stardew:tools/cooldown/tick

execute as @a[tag=sd_debug_mode] run function stardew:tools/debug_hud



# =================================================
# 菜单tick(放在最后,因为raycast会阻塞后续代码)
# =================================================
function stardew:menu/tick



# =================================================
# 商店系统 tick
# =================================================
# 检测商店入口interaction被触发
execute as @e[type=interaction,tag=pierre_shop,nbt={interaction:{}}] at @s run function stardew:shop/trigger_pierre
execute as @e[type=interaction,tag=pierre_shop,nbt={attack:{}}] at @s run function stardew:shop/trigger_pierre

# 商店内交互检测 (必须在重置NBT之前执行)
execute in stardew:interiors as @e[type=interaction,tag=button_close,nbt={interaction:{}}] at @s run function stardew:shop/close_shop
execute in stardew:interiors as @e[type=interaction,tag=button_close,nbt={attack:{}}] at @s run function stardew:shop/close_shop
execute in stardew:interiors as @e[type=interaction,tag=button_page_up,nbt={interaction:{}}] at @s run function stardew:shop/page_up
execute in stardew:interiors as @e[type=interaction,tag=button_page_up,nbt={attack:{}}] at @s run function stardew:shop/page_up
execute in stardew:interiors as @e[type=interaction,tag=button_page_down,nbt={interaction:{}}] at @s run function stardew:shop/page_down
execute in stardew:interiors as @e[type=interaction,tag=button_page_down,nbt={attack:{}}] at @s run function stardew:shop/page_down
execute in stardew:interiors as @e[type=interaction,tag=slot_1,nbt={interaction:{}}] at @s run function stardew:shop/purchase_slot_1
execute in stardew:interiors as @e[type=interaction,tag=slot_1,nbt={attack:{}}] at @s run function stardew:shop/purchase_slot_1
execute in stardew:interiors as @e[type=interaction,tag=slot_2,nbt={interaction:{}}] at @s run function stardew:shop/purchase_slot_2
execute in stardew:interiors as @e[type=interaction,tag=slot_2,nbt={attack:{}}] at @s run function stardew:shop/purchase_slot_2
execute in stardew:interiors as @e[type=interaction,tag=slot_3,nbt={interaction:{}}] at @s run function stardew:shop/purchase_slot_3
execute in stardew:interiors as @e[type=interaction,tag=slot_3,nbt={attack:{}}] at @s run function stardew:shop/purchase_slot_3

# 重置所有商店interaction的NBT (在检测之后)
execute in stardew:interiors as @e[type=interaction,tag=shop_interaction] run data remove entity @s interaction
execute in stardew:interiors as @e[type=interaction,tag=shop_interaction] run data remove entity @s attack

# 实时更新金币显示 (每tick)
execute if entity @a[scores={sd_in_shop=1..}] in stardew:interiors as @e[type=text_display,tag=shop_ui,tag=money_text,limit=1] run data modify entity @s text set value '{"score":{"name":"@p","objective":"sd_gold"},"color":"#853605","bold":true}'

# 商店悬停检测系统 (每tick) - 统一射线检测
execute if entity @a[scores={sd_in_shop=1..}] run function stardew:shop/hover_tick


