# data/stardew/functions/init.mcfunction
# 终极版初始化文件：包含所有系统、常量和状态的初始化与清理

# ==========================================
# 0. 状态效果系统初始化
# ==========================================
function stardew:status/init

# ==========================================
# 2. 常量预置 (Constants)
# ==========================================
scoreboard players set #2 sd_const 2
scoreboard players set #5 sd_const 5
scoreboard players set #7 sd_const 7
scoreboard players set #10 sd_const 10
scoreboard players set #20 sd_const 20
scoreboard players set #25 sd_const 25
scoreboard players set #33 sd_const 33
scoreboard players set #60 sd_const 60
scoreboard players set #67 sd_const 67
scoreboard players set #75 sd_const 75
scoreboard players set #90 sd_const 90
scoreboard players set #100 sd_const 100
scoreboard players set #240 sd_const 240
scoreboard players set #1440 sd_const 1440
scoreboard players set #1500 stardew.constant 1500
# 1. 计分板注册 (Objectives Registration)
# ==========================================

# 1.1 玩家基础 & 经济
scoreboard objectives add sd_gold dummy [{"text":"💰 ","color":"gold"},{"text":"金币","color":"yellow"}]
scoreboard objectives add sd_energy dummy "能量"
scoreboard objectives add sd_max_energy dummy "最大能量"
scoreboard objectives add sd_energy_warn dummy "能量警告冷却"
scoreboard objectives add sd_sleep trigger "点击睡觉"
scoreboard objectives add sd_right_click minecraft.used:minecraft.carrot_on_a_stick
scoreboard objectives add sd_sell_price dummy "出售价格"
scoreboard objectives add sd_const dummy "常量"
scoreboard objectives add sd_temp dummy "临时变量"
scoreboard objectives add sd_config dummy "游戏配置"

# 1.1.3.1 商店系统
scoreboard objectives add sd_in_shop dummy "商店状态"
scoreboard objectives add sd_shop_season dummy "商店季节"
scoreboard objectives add sd_shop_page dummy "商店页码"
scoreboard objectives add sd_shop_hover dummy "商店悬停状态"
scoreboard objectives add sd_shop_hover_prev dummy "商店悬停状态(上一tick)"
scoreboard objectives add sd_hover_slot dummy "悬停的槽位(1-3)"

# 1.1.4 筒仓系统
scoreboard objectives add sd_silo_count dummy "筒仓数量"
scoreboard objectives add sd_hay_stored dummy "已储存干草"
scoreboard objectives add sd_hay_capacity dummy "干草总容量"
scoreboard objectives add sd_grass_harvested dummy "收割草数量"
scoreboard objectives add sd_hay_chance dummy "干草获得概率"
scoreboard objectives add sd_random dummy "随机数"

# 1.1.5 生命值和能量值系统
scoreboard objectives add sd_health dummy "生命值"
scoreboard objectives add sd_max_health dummy "最大生命值"
scoreboard objectives add sd_last_health dummy "上次原版血量"
scoreboard objectives add sd_target_hearts dummy "目标黄心数"
scoreboard objectives add sd_food_timer dummy "食物同步计时器"

# 1.1.6 食物系统
scoreboard objectives add sd_use_item minecraft.used:minecraft.paper "使用物品"
scoreboard objectives add sd_food_cooldown dummy "食物冷却"

# 1.1.7 战斗系统
function stardew:combat/init

# 1.1.8 存储系统
function stardew:storage/init

# 1.1.9 博物馆鉴定系统
scoreboard objectives add sd_donated dummy "已捐赠物品标记"
scoreboard objectives add sd_offhand_check dummy "副手检测"

# 1.2 全局时间 & 环境
scoreboard objectives add sd_time dummy
scoreboard objectives add sd_day dummy
scoreboard objectives add sd_season dummy
scoreboard objectives add sd_year dummy
scoreboard objectives add sd_day_of_week dummy "星期几(0=周一,6=周日)"
scoreboard objectives add sd_tick_counter dummy
scoreboard objectives add sd_weather dummy "天气"
scoreboard objectives add sd_is_night dummy "是否夜晚"

# 1.2.1 每日事件标记（防止时间跳跃导致事件遗漏）
scoreboard objectives add sd_event_1800 dummy "18:00事件已触发"
scoreboard objectives add sd_event_2200 dummy "22:00事件已触发"
scoreboard objectives add sd_event_0000 dummy "00:00事件已触发"
scoreboard objectives add sd_event_0130 dummy "01:30事件已触发"

# 1.3 UI 显示
scoreboard objectives add sd_ui_hour dummy
scoreboard objectives add sd_ui_min dummy
scoreboard objectives add sd_ui_state dummy
scoreboard objectives add sd_time_min dummy
scoreboard objectives add sd_time_sec dummy
scoreboard objectives add sd_time_tmp dummy

# 1.3.5 菜单UI系统
scoreboard objectives add sd_menu_open trigger "打开菜单"
scoreboard objectives add sd_menu_sequence dummy "玩家UI编号"
scoreboard objectives add sd_menu_entity_num dummy "UI实体编号"
scoreboard objectives add sd_menu_page dummy "当前页码"
scoreboard objectives add sd_menu_slot dummy "按钮位置"
scoreboard objectives add sd_menu_targeted dummy "被瞄准"
scoreboard objectives add sd_menu_targeted_prev dummy "上一tick瞄准状态"
scoreboard objectives add sd_menu_ctrl dummy "控制变量"
scoreboard objectives add sd_menu_click_cd dummy "点击冷却"
scoreboard objectives add sd_regive_cd dummy "重新获取菜单书冷却"
scoreboard objectives add sd_show_gold dummy "金币显示开关"
scoreboard objectives add sd_menu_level dummy "菜单层级"
scoreboard objectives add sd_menu_init_fix dummy "菜单初始化修复标记"
scoreboard objectives add sd_menu_state dummy "菜单状态"

# 1.3.5.1 存储系统
function stardew:menu/storage/init

# 1.3.6 合成系统
# 配方解锁状态记分板 (通过宏动态创建 stardew.recipe.{id})
# 配方ID范围: 工具101-199, 设备201-299, 建筑301-399, 消耗品401-499, 家具501-599
scoreboard objectives add stardew.recipe.201 dummy "配方:熔炉"
scoreboard objectives add stardew.recipe.202 dummy "配方:箱子"
scoreboard objectives add stardew.recipe.203 dummy "配方:小桶"

# 1.4 耕种系统 (Farming)
scoreboard objectives add sd_crop_age dummy "作物年龄"
scoreboard objectives add sd_max_crop_age dummy "作物最大年龄"
scoreboard objectives add sd_original_max_age dummy "原始最大年龄"
scoreboard objectives add sd_rng dummy "随机数"
scoreboard objectives add sd_ray_steps dummy "射线步数"
scoreboard objectives add sd_watered dummy "是否浇水"
# [核心] 再生系统变量
scoreboard objectives add sd_regrow_stage dummy "再生阶段"
scoreboard objectives add sd_temp_regrow dummy "再生临时变量"
# [肥料系统] 肥料记分板
scoreboard objectives add sd_temp_fert_type dummy "临时肥料类型"
scoreboard objectives add sd_temp_fert_level dummy "临时肥料等级"
scoreboard objectives add sd_fertilizer_type dummy "肥料类型"
scoreboard objectives add sd_fertilizer_level dummy "肥料等级"
scoreboard objectives add sd_raycast dummy "射线距离"
# [品质系统] 品质计算临时变量
scoreboard objectives add sd_temp_quality dummy "品质计算:等级+肥料加成"
scoreboard objectives add sd_temp_silver dummy "品质计算:银星阈值"
scoreboard objectives add sd_temp_gold dummy "品质计算:金星阈值"
scoreboard objectives add sd_temp_diamond dummy "品质计算:钻石星阈值"
# [新增] 耕种技能
scoreboard objectives add sd_farming_xp dummy "耕种经验"
scoreboard objectives add sd_farming_lvl dummy "🌾 耕种等级"

# 1.5 树木系统 (Trees)
scoreboard objectives add sd_tree_hp dummy "树木血量"
scoreboard objectives add sd_tree_type dummy "树木种类"
scoreboard objectives add sd_shaked dummy "是否摇过"

# 1.5.5 怪物系统 (Monsters)
scoreboard objectives add sd_monster_kill minecraft.custom:minecraft.mob_kills "怪物击杀数"
scoreboard objectives add sd_combat_xp dummy "战斗经验"
scoreboard objectives add sd_combat_level dummy "⚔️ 战斗等级"
scoreboard objectives add sd_axe_dmg dummy "斧头伤害"
scoreboard objectives add sd_axe_cd dummy "斧头冷却"

# 1.6.1 工具蓄力系统 (Tool Charging)
scoreboard objectives add sd_charge_time dummy "蓄力时间"
scoreboard objectives add sd_charge_ready dummy "蓄力完成标记"

# 1.6.2 特殊进度条系统 (Special Progress Bar for Actionbar)
scoreboard objectives add sd_special_value dummy "特殊进度当前值"
scoreboard objectives add sd_special_max dummy "特殊进度最大值"
scoreboard objectives add sd_special_type dummy "特殊进度类型(0=无,1=蓄力,2=钓鱼)"

# 1.6.3 工具冷却系统 (Tool Cooldown)
scoreboard objectives add sd_tool_cd dummy "工具冷却时间"
scoreboard objectives add sd_hoe_cd dummy "锄头冷却"
scoreboard objectives add sd_water_cd dummy "水壶冷却"
scoreboard objectives add sd_scythe_cd dummy "镰刀冷却"

# 1.6 钓鱼系统 (Fishing) - 基础
scoreboard objectives add sd_fishing_lvl dummy "🎣 钓鱼等级"
scoreboard objectives add sd_fishing_xp dummy "钓鱼经验"
scoreboard objectives add sd_level_xp_req dummy "等级经验需求"
scoreboard objectives add sd_fishing_tick dummy "钓鱼计时"
scoreboard objectives add sd_rod_use minecraft.used:minecraft.fishing_rod
scoreboard objectives add sd_fish_power_player dummy "玩家力量"
scoreboard objectives add sd_final_difficulty dummy "最终难度"
scoreboard objectives add sd_age dummy "通用年龄" 
scoreboard objectives add sd_hook_safe dummy "钓钩安全锁"

# 1.7 挖矿系统 (Mining)
scoreboard objectives add sd_mining_lvl dummy "⛏ 挖矿等级"
scoreboard objectives add sd_mining_xp dummy "挖矿经验"
scoreboard objectives add sd_mining_xp_req dummy "挖矿升级经验需求"
scoreboard objectives add sd_mine_level dummy "当前矿洞层数"
scoreboard objectives add sd_stone_hp dummy "石头当前血量"
scoreboard objectives add sd_stone_max_hp dummy "石头最大血量"
scoreboard objectives add sd_stone_type dummy "石头类型"

# 1.8 战斗系统 (Combat)
scoreboard objectives add sd_combat_lvl dummy "⚔ 战斗等级"
scoreboard objectives add sd_combat_xp dummy "战斗经验"
scoreboard objectives add sd_combat_xp_req dummy "战斗升级经验需求"

# 1.9 采集系统 (Foraging)
scoreboard objectives add sd_foraging_lvl dummy "🌲 采集等级"
scoreboard objectives add sd_foraging_xp dummy "采集经验"
scoreboard objectives add sd_foraging_xp_req dummy "采集升级经验需求"
scoreboard objectives add sd_pickaxe_tier dummy "镐子等级"
scoreboard objectives add sd_pickaxe_dmg dummy "镐子伤害"
scoreboard objectives add sd_pickaxe_cd dummy "镐子冷却"
scoreboard objectives add sd_temp_pickaxe_tier dummy "临时镐子等级"
scoreboard objectives add sd_temp_pickaxe_dmg dummy "临时镐子伤害"
scoreboard objectives add sd_temp_val dummy "临时数值"
scoreboard objectives add sd_mine_count dummy "挖掘计数"
scoreboard objectives add sd_ladder_rng dummy "梯子随机数"
scoreboard objectives add sd_mine_theme dummy "矿洞主题"
scoreboard objectives add sd_mining_targeted dummy "矿石被瞄准"
scoreboard objectives add sd_mining_targeted_prev dummy "矿石上tick瞄准状态"

# 电梯UI系统
scoreboard objectives add sd_elevator_select dummy "电梯选中楼层"
scoreboard objectives add sd_elevator_page dummy "电梯菜单页码"
scoreboard objectives add sd_interaction_time dummy "交互时间戳"
scoreboard objectives add sd_bite_anim dummy "动画状态"
scoreboard objectives add sd_fish_region dummy "钓鱼区域"
scoreboard objectives add sd_time_slot dummy "时间段(0-7)"

# 矿井系统 - 楼层记录
scoreboard objectives add sd_mine_max_floor dummy "已到达最深层"

# 1.7 钓鱼系统 - 状态机
scoreboard objectives add sd_fish_safe dummy "安全时间"
scoreboard objectives add sd_bite_time dummy "预定咬钩时间"
scoreboard objectives add sd_fish_type dummy "鱼种ID"
scoreboard objectives add sd_fish_shake dummy "挣扎计数"
scoreboard objectives add sd_fish_power_fish dummy "鱼的力量"
scoreboard objectives add sd_fish_progress dummy "进度条"
scoreboard objectives add sd_fish_phase dummy "战斗阶段"
scoreboard objectives add sd_fish_pull_time dummy "拉线剩余"
scoreboard objectives add sd_fish_bite_state dummy "咬钩状态"
scoreboard objectives add sd_bite_react dummy "反应倒计时"
scoreboard objectives add sd_fish_ready dummy "准备咬钩"
scoreboard objectives add sd_bite_window dummy "咬钩窗口"
scoreboard objectives add sd_sneak_time minecraft.custom:minecraft.sneak_time
scoreboard objectives add sd_sneak_last dummy
scoreboard objectives add sd_is_sneaking dummy
scoreboard objectives add sd_fish_hint_cd dummy
scoreboard objectives add sd_pull_penalty dummy "平静期惩罚"

# 1.8 菜单系统 - 多级菜单
scoreboard objectives add sd_menu_level dummy "菜单层级"

# 1.9 渔具系统 (Tackle)
scoreboard objectives add sd_tackle_id dummy "当前渔具ID"
scoreboard objectives add sd_treasure_bonus dummy "宝藏加成"
scoreboard objectives add sd_pull_power dummy "拉力"
scoreboard objectives add sd_new_tackle dummy "新渔具暂存"
scoreboard objectives add sd_detach_id dummy "卸载ID"
scoreboard objectives add sd_rod_type dummy "鱼竿类型"
scoreboard objectives add sd_tackle1 dummy "槽位1"
scoreboard objectives add sd_tackle2 dummy "槽位2"
scoreboard objectives add sd_count dummy "通用计数"

# 1.10 实用设施时间系统 (Utility Time System)
# [通用架构] 所有基于时间的 utility 都使用相同的记分板结构
# 格式: sd_[utility]_state, sd_[utility]_type, sd_[utility]_timer, sd_[utility]_max_time, sd_[utility]_id
# 
# 当前支持的设施:
# - 熔炉 (Furnace): sd_furnace_*
# 
# 未来可扩展:
# - 酿酒桶 (Keg): sd_keg_*
# - 保存罐 (Jar): sd_jar_*
# - 奶酪机 (Cheese Press): sd_cheese_*
# - 织布机 (Loom): sd_loom_*
# - 油料机 (Oil Maker): sd_oil_*
# 等等...

# 熔炉系统 (Furnace)
scoreboard objectives add sd_furnace_state dummy "熔炉状态"
scoreboard objectives add sd_furnace_type dummy "熔炉物品类型"
scoreboard objectives add sd_furnace_timer dummy "熔炉已工作时间"
scoreboard objectives add sd_furnace_max_time dummy "熔炉需要总时间"
scoreboard objectives add sd_furnace_id dummy "熔炉唯一ID"
scoreboard objectives add sd_furnace_last_time dummy "熔炉上次更新时间"

# 小桶系统 (Keg)
scoreboard objectives add sd_keg_state dummy "小桶状态"
scoreboard objectives add sd_keg_type dummy "小桶物品类型"
scoreboard objectives add sd_keg_timer dummy "小桶已工作时间"
scoreboard objectives add sd_keg_max_time dummy "小桶需要总时间"
scoreboard objectives add sd_keg_id dummy "小桶唯一ID"
scoreboard objectives add sd_keg_last_time dummy "小桶上次更新时间"

# 树液提取器系统 (Tapper)
scoreboard objectives add sd_tapper_state dummy "提取器状态"
scoreboard objectives add sd_tapper_type dummy "提取器产物类型"
scoreboard objectives add sd_tapper_timer dummy "提取器已工作时间"
scoreboard objectives add sd_tapper_max_time dummy "提取器需要总时间"
scoreboard objectives add sd_tapper_id dummy "提取器唯一ID"
scoreboard objectives add sd_tapper_last_time dummy "提取器上次更新时间"

# 洒水器系统 (Sprinkler)
scoreboard objectives add sd_sprinkler_type dummy "洒水器类型"
scoreboard objectives add sd_sprinkler_calls dummy "洒水器调用次数"

# 通用实用设施奖励与活跃标记(用于 new_day 的统一奖励)
scoreboard objectives add sd_utility_bonus dummy "实用设施时间奖励"
scoreboard objectives add sd_utility_active dummy "实用设施活跃标记"
scoreboard objectives add sd_rotation dummy "实用设施旋转角度"

# 实用设施高亮系统
scoreboard objectives add sd_utility_targeted dummy "当前帧是否被瞄准"
scoreboard objectives add sd_utility_targeted_prev dummy "上一帧是否被瞄准"

# 实用设施动画系统 (Utility Animation System)
scoreboard objectives add sd_anim_tick dummy "动画时钟"
scoreboard objectives add sd_anim_phase dummy "动画阶段"


# ==========================================
# 2. 常量预置 (Constants)
# ==========================================
scoreboard players set #-1 sd_const -1
scoreboard players set #3 sd_const 3
scoreboard players set #4 sd_const 4
scoreboard players set #5 sd_const 5
scoreboard players set #10 sd_const 10
scoreboard players set #20 sd_const 20
scoreboard players set #60 sd_const 60
scoreboard players set #100 sd_const 100
scoreboard players set #1440 sd_const 1440
scoreboard players set Global sd_const 1


# ==========================================
# 3. 玩家初始状态 (Initial Player State)
# ==========================================
# 确保所有玩家都有初始值
execute as @a unless score @s sd_gold matches 0.. run scoreboard players set @s sd_gold 0
execute as @a unless score @s sd_energy matches 0.. run scoreboard players set @s sd_energy 400
execute as @a unless score @s sd_max_energy matches 1.. run scoreboard players set @s sd_max_energy 400
execute as @a unless score @s sd_health matches 0.. run scoreboard players set @s sd_health 400
execute as @a unless score @s sd_max_health matches 1.. run scoreboard players set @s sd_max_health 400
execute as @a unless score @s sd_fishing_lvl matches 0.. run scoreboard players set @s sd_fishing_lvl 0
execute as @a unless score @s sd_fishing_xp matches 0.. run scoreboard players set @s sd_fishing_xp 0
execute as @a unless score @s sd_show_gold matches 0.. run scoreboard players set @s sd_show_gold 1
# [新增] 耕种初始值
execute as @a unless score @s sd_farming_lvl matches 0.. run scoreboard players set @s sd_farming_lvl 0
execute as @a unless score @s sd_farming_xp matches 0.. run scoreboard players set @s sd_farming_xp 0
# [新增] 挖矿初始值
execute as @a unless score @s sd_mining_lvl matches 0.. run scoreboard players set @s sd_mining_lvl 0
execute as @a unless score @s sd_mining_xp matches 0.. run scoreboard players set @s sd_mining_xp 0
execute as @a unless score @s sd_mine_level matches 0.. run scoreboard players set @s sd_mine_level 0
execute as @a unless score @s sd_mine_count matches 0.. run scoreboard players set @s sd_mine_count 0
execute as @a unless score @s sd_mine_max_floor matches 0.. run scoreboard players set @s sd_mine_max_floor 0

# 清理所有玩家的旧钓鱼状态
execute as @a run function stardew:fishing/reset_state

# 初始化冷却系统
function stardew:tools/cooldown/init

# 初始化畜牧业系统
function stardew:animal/core/init

# 菜单UI全局序列号初始化（从1开始，避免0值导致的选择器问题）
execute unless score #HaveUsed sd_menu_sequence matches 1.. run scoreboard players set #HaveUsed sd_menu_sequence 1

# 清理可能存在的孤立菜单实体（修复幽灵菜单bug）
execute unless score #MenuCleanup sd_menu_init_fix matches 1 run function stardew:menu/cleanup_orphaned
execute unless score #MenuCleanup sd_menu_init_fix matches 1 run scoreboard players set #MenuCleanup sd_menu_init_fix 1

# ==========================================
# 4. 全局时间状态 (Global State)
# ==========================================
execute unless score Global sd_time matches 0.. run scoreboard players set Global sd_time 360
execute unless score Global sd_year matches 1.. run scoreboard players set Global sd_year 1
execute unless score Global sd_season matches 1.. run scoreboard players set Global sd_season 1
execute unless score Global sd_day matches 1.. run scoreboard players set Global sd_day 1
execute unless score Global sd_weather matches 0..3 run scoreboard players set Global sd_weather 0

# 计算并初始化星期几（sd_day % 7）
execute unless score Global sd_day_of_week matches 0..6 run scoreboard players operation Global sd_day_of_week = Global sd_day
execute unless score Global sd_day_of_week matches 0..6 run scoreboard players operation Global sd_day_of_week %= #7 stardew.const

# UI/临时分数清零
scoreboard players set Global sd_ui_hour 0
scoreboard players set Global sd_ui_min 0
scoreboard players set Global sd_tick_counter 0
scoreboard players set Global sd_rng 0



# ==========================================
# 6. 配置加载与规则设置
# ==========================================

# 加载配置 (价格、经验表等)
function stardew:config/settings

# 加载 UI 系统
function stardew:ui/load

# ==========================================
# 7. Bossbar 初始化
# ==========================================
# 7. Bossbar 初始化
# ==========================================

# 钓鱼 bossbar
bossbar add stardew:fishing {"text":"🎣 钓鱼中...","color":"aqua"}
bossbar set stardew:fishing color blue
bossbar set stardew:fishing style progress
bossbar set stardew:fishing visible false
bossbar set stardew:fishing max 1000

# ==========================================
# 8. 矿洞系统初始化
# ==========================================
function stardew:mine/init

# 规则
gamerule doDaylightCycle false
gamerule doWeatherCycle false
gamerule doTileDrops false
gamerule randomTickSpeed 0
gamerule doMobLoot true

# ==========================================
# 对话系统初始化
# ==========================================
function stardew:dialogue/init

# ==========================================
# NPC系统初始化
# ==========================================
function stardew:npc/system/init

tellraw @a {"text":"[系统] 星露谷Core v0.10.1 (挖矿初版 + 对话系统原型) 已加载","color":"green"}