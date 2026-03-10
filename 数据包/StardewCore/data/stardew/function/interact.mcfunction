# data/stardew/functions/interact.mcfunction

# 0. 菜单 (Menu) - 使用特殊萝卜钓竿 CMD:1
execute if score @s sd_right_click matches 1.. if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=1] run function stardew:menu/toggle
execute if score @s sd_right_click matches 1.. if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=1] run scoreboard players set @s sd_right_click 0

# 0.1. Debug工具 - 杂草放置器 CMD:9600
execute if score @s sd_right_click matches 1.. if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=9600] run function stardew:weeds/place_weed
execute if score @s sd_right_click matches 1.. if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=9600] run scoreboard players set @s sd_right_click 0

# 0.2. Debug工具 - 草放置器 CMD:9601
execute if score @s sd_right_click matches 1.. if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=9601] run function stardew:grass/place_grass
execute if score @s sd_right_click matches 1.. if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=9601] run scoreboard players set @s sd_right_click 0

# 0.3. Debug工具 - 筒仓添加器 CMD:9602
execute if score @s sd_right_click matches 1.. if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=9602] run function stardew:debug/add_silo
execute if score @s sd_right_click matches 1.. if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=9602] run scoreboard players set @s sd_right_click 0

# 0.4. Debug工具 - 筒仓移除器 CMD:9603
execute if score @s sd_right_click matches 1.. if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=9603] run function stardew:debug/remove_silo
execute if score @s sd_right_click matches 1.. if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=9603] run scoreboard players set @s sd_right_click 0

# 0.5. Debug工具 - 筒仓状态查看器 CMD:9604
execute if score @s sd_right_click matches 1.. if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=9604] run function stardew:debug/silo_status
execute if score @s sd_right_click matches 1.. if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=9604] run scoreboard players set @s sd_right_click 0

# 0.3. 钓鱼宝箱 - 右键打开宝箱
execute if score @s sd_right_click matches 1.. if items entity @s weapon.mainhand carrot_on_a_stick[custom_data~{item_type:"treasure_chest"}] run function stardew:fishing/treasure_chest/open

# 1. 睡觉 (Sleep)
execute if score @s sd_sleep matches 1.. run function stardew:time/sleep_action
scoreboard players set @s sd_sleep 0
scoreboard players enable @s sd_sleep

# ==================================================
# 2. 通用工具右键 (sd_right_click)
# ==================================================
# 负责：镰刀、锄头、水壶、种子袋等
execute if score @s sd_right_click matches 1.. run function stardew:tools/main_handler
scoreboard players set @s sd_right_click 0


# ==================================================
# 3. 钓鱼竿相关逻辑 (sd_rod_use)
# ==================================================
# 负责：装配渔具、卸载渔具、抛竿钓鱼

# A. [装配渔具] Attach
# 条件：主手鱼竿 + 副手渔具(paper) + 蹲下
# [修改] id:"minecraft:carrot_on_a_stick" -> id:"minecraft:paper"
execute if score @s sd_rod_use matches 1.. if entity @s[scores={sd_is_sneaking=1}] if data entity @s SelectedItem{id:"minecraft:fishing_rod"} if data entity @s Inventory[{Slot:-106b,id:"minecraft:paper"}] run function stardew:tools/tackle_attach_action

# 阻断
execute if score @s sd_rod_use matches 1.. if entity @s[scores={sd_is_sneaking=1}] if data entity @s SelectedItem{id:"minecraft:fishing_rod"} if data entity @s Inventory[{Slot:-106b,id:"minecraft:paper"}] run scoreboard players set @s sd_rod_use 0
# B. [卸载渔具] Detach
# 条件：主手鱼竿(有tackle_1) + 副手为空 + 蹲下
# --------------------------------------------------
# [修改] 检测 tackle_1 是否存在
execute if score @s sd_rod_use matches 1.. if entity @s[scores={sd_is_sneaking=1}] if data entity @s SelectedItem{id:"minecraft:fishing_rod"} unless data entity @s Inventory[{Slot:-106b}] if data entity @s SelectedItem.components."minecraft:custom_data".tackle_1 run function stardew:tools/tackle_detach_action

# 阻断抛竿
execute if score @s sd_rod_use matches 1.. if entity @s[scores={sd_is_sneaking=1}] if data entity @s SelectedItem{id:"minecraft:fishing_rod"} unless data entity @s Inventory[{Slot:-106b}] if data entity @s SelectedItem.components."minecraft:custom_data".tackle_1 run scoreboard players set @s sd_rod_use 0

# --------------------------------------------------
# C. [正常钓鱼] Fishing
# --------------------------------------------------
execute if score @s sd_rod_use matches 1.. run function stardew:fishing/rod_action
scoreboard players set @s sd_rod_use 0