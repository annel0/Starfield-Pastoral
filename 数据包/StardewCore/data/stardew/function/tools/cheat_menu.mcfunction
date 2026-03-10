# 简化版全能工具台（UTF-8，无表情符号）
tellraw @s ["",{"text":"======== Stardew 全能工具台 ========","color":"yellow"}]

tellraw @s ["",{"text":"\n[ 功能 ]","color":"#FF6600","bold":true}]
tellraw @s ["",{"text":"[ 睡觉 ]","color":"#00BFFF","clickEvent":{"action":"run_command","value":"/trigger sd_sleep set 1"}},{"text":" (跳过一天)","color":"gray"}]
tellraw @s ["",{"text":"[ 春季 ]","color":"#32CD32","clickEvent":{"action":"run_command","value":"/scoreboard players set Global sd_season 1"}},{"text":" | "},{"text":"[ 夏季 ]","color":"#FFD700","clickEvent":{"action":"run_command","value":"/scoreboard players set Global sd_season 2"}},{"text":" | "},{"text":"[ 秋季 ]","color":"#FF8C00","clickEvent":{"action":"run_command","value":"/scoreboard players set Global sd_season 3"}},{"text":" | "},{"text":"[ 冬季 ]","color":"#B0E0E6","clickEvent":{"action":"run_command","value":"/scoreboard players set Global sd_season 4"}}]

# ==============================================================================
# 农具 (Tools)
# ==============================================================================
tellraw @s ["",{"text":"\n[ 农具 ]","color":"gold","bold":true}]

# --- 斧头 ---
tellraw @s ["",{"text":"斧头: ","color":"gray"}]
tellraw @s [{"text":"[铜] ","color":"#CD7F32","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/axe_copper\"}"}}]
tellraw @s [{"text":"[铁] ","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/axe_iron\"}"}}]
tellraw @s [{"text":"[金] ","color":"yellow","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/axe_gold\"}"}}]
tellraw @s [{"text":"[钻] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/axe_diamond\"}"}}]

# --- 镰刀 ---
tellraw @s ["",{"text":"\n镰刀: ","color":"gray"}]
tellraw @s [{"text":"[铜] ","color":"#CD7F32","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/scythe_copper\"}"}}]
tellraw @s [{"text":"[铁] ","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/scythe_iron\"}"}}]
tellraw @s [{"text":"[金] ","color":"yellow","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/scythe_gold\"}"}}]
tellraw @s [{"text":"[钻] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/scythe_diamond\"}"}}]

# --- 锄头 ---
tellraw @s ["",{"text":"\n锄头: ","color":"gray"}]
tellraw @s [{"text":"[铜] ","color":"#CD7F32","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/hoe_copper\"}"}}]
tellraw @s [{"text":"[铁] ","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/hoe_iron\"}"}}]
tellraw @s [{"text":"[金] ","color":"yellow","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/hoe_gold\"}"}}]
tellraw @s [{"text":"[钻] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/hoe_diamond\"}"}}]

# --- 水壶 ---
tellraw @s ["",{"text":"\n水壶: ","color":"gray"}]
tellraw @s [{"text":"[铜] ","color":"#CD7F32","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/watering_can_copper\"}"}}]
tellraw @s [{"text":"[铁] ","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/watering_can_iron\"}"}}]
tellraw @s [{"text":"[金] ","color":"yellow","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/watering_can_gold\"}"}}]
tellraw @s [{"text":"[钻] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/tools/watering_can_diamond\"}"}}]

# ==============================================================================
# 作物种子 (Crop Seeds)
# ==============================================================================
tellraw @s ["",{"text":"\n[ 作物种子 ]","color":"dark_green","bold":true}]
tellraw @s ["",{"text":"[小麦] ","color":"yellow","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/seeds/crop_wheat\"}"}},{"text":"[番茄] ","color":"red","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/seeds/crop_tomato\"}"}},{"text":"[草莓] ","color":"light_purple","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/seeds/crop_strawberry\"}"}},{"text":"[大蒜] ","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/seeds/crop_garlic\"}"}}]

# ==============================================================================
# 树木种子 (Tree Seeds)
# ==============================================================================
tellraw @s ["",{"text":"\n[ 树木种子 ]","color":"dark_green","bold":true}]
tellraw @s ["",{"text":"[橡果] ","color":"green","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/seeds/tree_oak\"}"}},{"text":"[枫树] ","color":"gold","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/seeds/tree_maple\"}"}},{"text":"[松果] ","color":"dark_green","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/seeds/tree_pine\"}"}},{"text":"[桃花心木] ","color":"red","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/seeds/tree_mahogany\"}"}}]

# ==============================================================================
# 调试工具 (Debug)
# ==============================================================================
tellraw @s ["",{"text":"\n[ 调试工具 ]","color":"gray","bold":true}]
tellraw @s ["",{"text":"[ 生长激素 ] ","color":"light_purple","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/debug/grow_hormone\"}"}}]
tellraw @s ["",{"text":"[ 时间调试棒 ] ","color":"gold","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/debug/time_wand\"}"}}]
tellraw @s ["",{"text":"[ 钓鱼听诊器 ] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/debug/fish_doctor\"}"}}]
tellraw @s ["",{"text":"[ 天气控制棒 ] ","color":"blue","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/debug/weather_wand\"}"}}]
tellraw @s ["",{"text":"[ 生成卖货箱 ]","color":"green","clickEvent":{"action":"run_command","value":"/function stardew:economy/spawn_shipping_bin"}},{"text":"   "},{"text":"[ 清除卖货箱 ]","color":"red","clickEvent":{"action":"run_command","value":"/function stardew:economy/economy/kill_shipping_bins"}}]

# ==============================================================================
# 渔具 (Fishing)
# ==============================================================================
tellraw @s ["",{"text":"\n[ 渔具 ]","color":"aqua","bold":true}]

tellraw @s ["",{"text":"[铜竿]","color":"#CD7F32","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/fishing/rod_copper\"}"}}]
tellraw @s [{"text":"[铁竿]","color":"white","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/fishing/rod_iron\"}"}}]
tellraw @s [{"text":"[金竿]","color":"yellow","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/fishing/rod_gold\"}"}}]
tellraw @s [{"text":"[钻竿]","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/fishing/rod_diamond\"}"}}]

tellraw @s ["",{"text":"\n[浮标A] ","color":"gray"},{"text":"[声呐] ","color":"blue","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/fishing/tackle_sonar\"}"}},{"text":"[优质] ","color":"yellow","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/fishing/tackle_quality\"}"}},{"text":"[旋式] ","color":"aqua","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/fishing/tackle_spinner\"}"}},{"text":"[陷阱] ","color":"red","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/fishing/tackle_trap\"}"}}]
tellraw @s ["",{"text":"[浮标B] ","color":"gray"},{"text":"[软木] ","color":"gold","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/fishing/tackle_cork\"}"}},{"text":"[寻宝] ","color":"yellow","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/fishing/tackle_treasure\"}"}},{"text":"[倒刺] ","color":"gray","clickEvent":{"action":"run_command","value":"/function stardew:api/give_item {id:\"items/fishing/tackle_barbed\"}"}}]

tellraw @s ["",{"text":"\n========================================","color":"yellow"}]