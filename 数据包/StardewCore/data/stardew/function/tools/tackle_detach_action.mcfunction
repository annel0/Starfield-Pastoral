# data/stardew/functions/tools/tackle_detach_action.mcfunction
# [执行者: 玩家]

# 0. 阻止原版抛竿 & 冷却
execute as @s at @s run kill @e[type=fishing_bobber,distance=..4,limit=1,sort=nearest]
execute if entity @s[tag=sd_op_cooldown] run return 0

# ==========================================
# 1. 前置检查
# ==========================================
execute unless data entity @s SelectedItem{id:"minecraft:fishing_rod"} run return 0
execute if data entity @s Inventory[{Slot:-106b}] run tellraw @s [{"text":"❌ 请先腾空副手！","color":"red","italic":false}]
execute if data entity @s Inventory[{Slot:-106b}] run return 0

# 读取当前状态
scoreboard players set @s sd_tackle1 0
scoreboard players set @s sd_tackle2 0
execute store result score @s sd_tackle1 run data get entity @s SelectedItem.components."minecraft:custom_data".tackle_1
execute store result score @s sd_tackle2 run data get entity @s SelectedItem.components."minecraft:custom_data".tackle_2

# 检查是否为空
execute if score @s sd_tackle1 matches 0 if score @s sd_tackle2 matches 0 run tellraw @s [{"text":"❌ 此鱼竿未装备渔具。","color":"gray","italic":false}]
execute if score @s sd_tackle1 matches 0 if score @s sd_tackle2 matches 0 run return 0


# ==========================================
# 2. Storage 操作 & 卸载逻辑
# ==========================================
data modify storage stardew:temp TargetRod set from entity @s SelectedItem
data modify storage stardew:temp TargetRod.Slot set value 0b

scoreboard players set @s sd_detach_id 0

# 优先卸载槽2
execute if score @s sd_tackle2 matches 1.. run scoreboard players operation @s sd_detach_id = @s sd_tackle2
execute if score @s sd_tackle2 matches 1.. run data modify storage stardew:temp TargetRod.components."minecraft:custom_data".tackle_2 set value 0

# 否则卸载槽1
execute if score @s sd_tackle2 matches 0 if score @s sd_tackle1 matches 1.. run scoreboard players operation @s sd_detach_id = @s sd_tackle1
execute if score @s sd_tackle2 matches 0 if score @s sd_tackle1 matches 1.. run data modify storage stardew:temp TargetRod.components."minecraft:custom_data".tackle_1 set value 0


# ==========================================
# 3. 重绘 Lore (暴力穷举版)
# ==========================================
# 重新读取状态
execute store result score @s sd_tackle1 run data get storage stardew:temp TargetRod.components."minecraft:custom_data".tackle_1
execute store result score @s sd_tackle2 run data get storage stardew:temp TargetRod.components."minecraft:custom_data".tackle_2
execute store result score @s sd_rod_type run data get entity @s SelectedItem.components."minecraft:custom_model_data"

data modify storage stardew:temp TargetRod.components."minecraft:lore" set value []

# --- 头部信息 ---
execute if score @s sd_rod_type matches 403 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"📊 🎣 钓鱼等级要求: ","color":"white","italic":false,"extra":[{"text":"6","color":"green","bold":true,"italic":false}]}'
execute if score @s sd_rod_type matches 403 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"💪 基础钓力: ","color":"white","italic":false,"extra":[{"text":"4","color":"aqua","bold":true,"italic":false}]}'
execute if score @s sd_rod_type matches 404 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"📊 🎣 钓鱼等级要求: ","color":"white","italic":false,"extra":[{"text":"9","color":"green","bold":true,"italic":false}]}'
execute if score @s sd_rod_type matches 404 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"💪 基础钓力: ","color":"white","italic":false,"extra":[{"text":"5","color":"aqua","bold":true,"italic":false}]}'

# ==========================================================
# ▼▼▼ 把你脚本生成的代码粘贴在这里 (与上面一样) ▼▼▼
# ==========================================================

# === A. 金竿 (403) 单槽逻辑 ===
execute if score @s sd_rod_type matches 403 if score @s sd_tackle1 matches 0 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"空","color":"dark_gray","italic":false}]}'
execute if score @s sd_rod_type matches 403 if score @s sd_tackle1 matches 5001 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"声呐浮标","color":"blue","italic":false}]}'
execute if score @s sd_rod_type matches 403 if score @s sd_tackle1 matches 5002 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"优质浮标","color":"yellow","italic":false}]}'
execute if score @s sd_rod_type matches 403 if score @s sd_tackle1 matches 5003 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"旋式鱼饵","color":"aqua","italic":false}]}'
execute if score @s sd_rod_type matches 403 if score @s sd_tackle1 matches 5004 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"陷阱浮标","color":"red","italic":false}]}'
execute if score @s sd_rod_type matches 403 if score @s sd_tackle1 matches 5005 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"软木塞浮标","color":"gold","italic":false}]}'
execute if score @s sd_rod_type matches 403 if score @s sd_tackle1 matches 5006 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"寻宝者","color":"yellow","italic":false}]}'
execute if score @s sd_rod_type matches 403 if score @s sd_tackle1 matches 5007 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"倒刺钩","color":"gray","italic":false}]}'

# === B. 钻竿 (404) 双槽逻辑 ===
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 0 if score @s sd_tackle2 matches 0 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"空","color":"dark_gray"},{"text":" | ","color":"gray"},{"text":"空","color":"dark_gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 0 if score @s sd_tackle2 matches 5001 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"空","color":"dark_gray"},{"text":" | ","color":"gray"},{"text":"声呐浮标","color":"blue"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 0 if score @s sd_tackle2 matches 5002 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"空","color":"dark_gray"},{"text":" | ","color":"gray"},{"text":"优质浮标","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 0 if score @s sd_tackle2 matches 5003 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"空","color":"dark_gray"},{"text":" | ","color":"gray"},{"text":"旋式鱼饵","color":"aqua"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 0 if score @s sd_tackle2 matches 5004 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"空","color":"dark_gray"},{"text":" | ","color":"gray"},{"text":"陷阱浮标","color":"red"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 0 if score @s sd_tackle2 matches 5005 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"空","color":"dark_gray"},{"text":" | ","color":"gray"},{"text":"软木塞浮标","color":"gold"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 0 if score @s sd_tackle2 matches 5006 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"空","color":"dark_gray"},{"text":" | ","color":"gray"},{"text":"寻宝者","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 0 if score @s sd_tackle2 matches 5007 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"空","color":"dark_gray"},{"text":" | ","color":"gray"},{"text":"倒刺钩","color":"gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5001 if score @s sd_tackle2 matches 0 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"声呐浮标","color":"blue"},{"text":" | ","color":"gray"},{"text":"空","color":"dark_gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5001 if score @s sd_tackle2 matches 5001 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"声呐浮标","color":"blue"},{"text":" | ","color":"gray"},{"text":"声呐浮标","color":"blue"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5001 if score @s sd_tackle2 matches 5002 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"声呐浮标","color":"blue"},{"text":" | ","color":"gray"},{"text":"优质浮标","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5001 if score @s sd_tackle2 matches 5003 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"声呐浮标","color":"blue"},{"text":" | ","color":"gray"},{"text":"旋式鱼饵","color":"aqua"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5001 if score @s sd_tackle2 matches 5004 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"声呐浮标","color":"blue"},{"text":" | ","color":"gray"},{"text":"陷阱浮标","color":"red"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5001 if score @s sd_tackle2 matches 5005 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"声呐浮标","color":"blue"},{"text":" | ","color":"gray"},{"text":"软木塞浮标","color":"gold"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5001 if score @s sd_tackle2 matches 5006 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"声呐浮标","color":"blue"},{"text":" | ","color":"gray"},{"text":"寻宝者","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5001 if score @s sd_tackle2 matches 5007 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"声呐浮标","color":"blue"},{"text":" | ","color":"gray"},{"text":"倒刺钩","color":"gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5002 if score @s sd_tackle2 matches 0 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"优质浮标","color":"yellow"},{"text":" | ","color":"gray"},{"text":"空","color":"dark_gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5002 if score @s sd_tackle2 matches 5001 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"优质浮标","color":"yellow"},{"text":" | ","color":"gray"},{"text":"声呐浮标","color":"blue"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5002 if score @s sd_tackle2 matches 5002 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"优质浮标","color":"yellow"},{"text":" | ","color":"gray"},{"text":"优质浮标","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5002 if score @s sd_tackle2 matches 5003 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"优质浮标","color":"yellow"},{"text":" | ","color":"gray"},{"text":"旋式鱼饵","color":"aqua"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5002 if score @s sd_tackle2 matches 5004 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"优质浮标","color":"yellow"},{"text":" | ","color":"gray"},{"text":"陷阱浮标","color":"red"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5002 if score @s sd_tackle2 matches 5005 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"优质浮标","color":"yellow"},{"text":" | ","color":"gray"},{"text":"软木塞浮标","color":"gold"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5002 if score @s sd_tackle2 matches 5006 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"优质浮标","color":"yellow"},{"text":" | ","color":"gray"},{"text":"寻宝者","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5002 if score @s sd_tackle2 matches 5007 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"优质浮标","color":"yellow"},{"text":" | ","color":"gray"},{"text":"倒刺钩","color":"gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5003 if score @s sd_tackle2 matches 0 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"旋式鱼饵","color":"aqua"},{"text":" | ","color":"gray"},{"text":"空","color":"dark_gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5003 if score @s sd_tackle2 matches 5001 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"旋式鱼饵","color":"aqua"},{"text":" | ","color":"gray"},{"text":"声呐浮标","color":"blue"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5003 if score @s sd_tackle2 matches 5002 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"旋式鱼饵","color":"aqua"},{"text":" | ","color":"gray"},{"text":"优质浮标","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5003 if score @s sd_tackle2 matches 5003 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"旋式鱼饵","color":"aqua"},{"text":" | ","color":"gray"},{"text":"旋式鱼饵","color":"aqua"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5003 if score @s sd_tackle2 matches 5004 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"旋式鱼饵","color":"aqua"},{"text":" | ","color":"gray"},{"text":"陷阱浮标","color":"red"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5003 if score @s sd_tackle2 matches 5005 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"旋式鱼饵","color":"aqua"},{"text":" | ","color":"gray"},{"text":"软木塞浮标","color":"gold"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5003 if score @s sd_tackle2 matches 5006 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"旋式鱼饵","color":"aqua"},{"text":" | ","color":"gray"},{"text":"寻宝者","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5003 if score @s sd_tackle2 matches 5007 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"旋式鱼饵","color":"aqua"},{"text":" | ","color":"gray"},{"text":"倒刺钩","color":"gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5004 if score @s sd_tackle2 matches 0 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"陷阱浮标","color":"red"},{"text":" | ","color":"gray"},{"text":"空","color":"dark_gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5004 if score @s sd_tackle2 matches 5001 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"陷阱浮标","color":"red"},{"text":" | ","color":"gray"},{"text":"声呐浮标","color":"blue"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5004 if score @s sd_tackle2 matches 5002 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"陷阱浮标","color":"red"},{"text":" | ","color":"gray"},{"text":"优质浮标","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5004 if score @s sd_tackle2 matches 5003 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"陷阱浮标","color":"red"},{"text":" | ","color":"gray"},{"text":"旋式鱼饵","color":"aqua"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5004 if score @s sd_tackle2 matches 5004 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"陷阱浮标","color":"red"},{"text":" | ","color":"gray"},{"text":"陷阱浮标","color":"red"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5004 if score @s sd_tackle2 matches 5005 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"陷阱浮标","color":"red"},{"text":" | ","color":"gray"},{"text":"软木塞浮标","color":"gold"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5004 if score @s sd_tackle2 matches 5006 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"陷阱浮标","color":"red"},{"text":" | ","color":"gray"},{"text":"寻宝者","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5004 if score @s sd_tackle2 matches 5007 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"陷阱浮标","color":"red"},{"text":" | ","color":"gray"},{"text":"倒刺钩","color":"gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5005 if score @s sd_tackle2 matches 0 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"软木塞浮标","color":"gold"},{"text":" | ","color":"gray"},{"text":"空","color":"dark_gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5005 if score @s sd_tackle2 matches 5001 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"软木塞浮标","color":"gold"},{"text":" | ","color":"gray"},{"text":"声呐浮标","color":"blue"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5005 if score @s sd_tackle2 matches 5002 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"软木塞浮标","color":"gold"},{"text":" | ","color":"gray"},{"text":"优质浮标","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5005 if score @s sd_tackle2 matches 5003 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"软木塞浮标","color":"gold"},{"text":" | ","color":"gray"},{"text":"旋式鱼饵","color":"aqua"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5005 if score @s sd_tackle2 matches 5004 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"软木塞浮标","color":"gold"},{"text":" | ","color":"gray"},{"text":"陷阱浮标","color":"red"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5005 if score @s sd_tackle2 matches 5005 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"软木塞浮标","color":"gold"},{"text":" | ","color":"gray"},{"text":"软木塞浮标","color":"gold"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5005 if score @s sd_tackle2 matches 5006 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"软木塞浮标","color":"gold"},{"text":" | ","color":"gray"},{"text":"寻宝者","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5005 if score @s sd_tackle2 matches 5007 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"软木塞浮标","color":"gold"},{"text":" | ","color":"gray"},{"text":"倒刺钩","color":"gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5006 if score @s sd_tackle2 matches 0 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"寻宝者","color":"yellow"},{"text":" | ","color":"gray"},{"text":"空","color":"dark_gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5006 if score @s sd_tackle2 matches 5001 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"寻宝者","color":"yellow"},{"text":" | ","color":"gray"},{"text":"声呐浮标","color":"blue"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5006 if score @s sd_tackle2 matches 5002 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"寻宝者","color":"yellow"},{"text":" | ","color":"gray"},{"text":"优质浮标","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5006 if score @s sd_tackle2 matches 5003 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"寻宝者","color":"yellow"},{"text":" | ","color":"gray"},{"text":"旋式鱼饵","color":"aqua"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5006 if score @s sd_tackle2 matches 5004 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"寻宝者","color":"yellow"},{"text":" | ","color":"gray"},{"text":"陷阱浮标","color":"red"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5006 if score @s sd_tackle2 matches 5005 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"寻宝者","color":"yellow"},{"text":" | ","color":"gray"},{"text":"软木塞浮标","color":"gold"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5006 if score @s sd_tackle2 matches 5006 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"寻宝者","color":"yellow"},{"text":" | ","color":"gray"},{"text":"寻宝者","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5006 if score @s sd_tackle2 matches 5007 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"寻宝者","color":"yellow"},{"text":" | ","color":"gray"},{"text":"倒刺钩","color":"gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5007 if score @s sd_tackle2 matches 0 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"倒刺钩","color":"gray"},{"text":" | ","color":"gray"},{"text":"空","color":"dark_gray"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5007 if score @s sd_tackle2 matches 5001 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"倒刺钩","color":"gray"},{"text":" | ","color":"gray"},{"text":"声呐浮标","color":"blue"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5007 if score @s sd_tackle2 matches 5002 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"倒刺钩","color":"gray"},{"text":" | ","color":"gray"},{"text":"优质浮标","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5007 if score @s sd_tackle2 matches 5003 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"倒刺钩","color":"gray"},{"text":" | ","color":"gray"},{"text":"旋式鱼饵","color":"aqua"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5007 if score @s sd_tackle2 matches 5004 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"倒刺钩","color":"gray"},{"text":" | ","color":"gray"},{"text":"陷阱浮标","color":"red"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5007 if score @s sd_tackle2 matches 5005 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"倒刺钩","color":"gray"},{"text":" | ","color":"gray"},{"text":"软木塞浮标","color":"gold"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5007 if score @s sd_tackle2 matches 5006 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"倒刺钩","color":"gray"},{"text":" | ","color":"gray"},{"text":"寻宝者","color":"yellow"}]}'
execute if score @s sd_rod_type matches 404 if score @s sd_tackle1 matches 5007 if score @s sd_tackle2 matches 5007 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🎨 [渔具槽] ","color":"white","italic":false,"extra":[{"text":"倒刺钩","color":"gray"},{"text":" | ","color":"gray"},{"text":"倒刺钩","color":"gray"}]}'

# ==========================================================
# ▲▲▲ 粘贴结束 ▲▲▲
# ==========================================================

# --- 尾部信息 ---
data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"💰 售价：","color":"white","italic":false,"extra":[{"text":"无法出售","color":"red","bold":true}]}'
data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🔧 物品种类：","color":"white","italic":false,"extra":[{"text":"工具","color":"gold","bold":true}]}'
execute if score @s sd_rod_type matches 403 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🔨 一把金制的优良钓竿。","color":"gray","italic":false}'
execute if score @s sd_rod_type matches 404 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"🔨 传说中的钻石钓竿。","color":"gray","italic":false}'


# ==========================================
# 4. 替换 & 归还 (★ Loot Table 接入 ★)
# ==========================================
setblock 0 -64 0 barrel
data modify block 0 -64 0 Items append from storage stardew:temp TargetRod
item replace entity @s weapon.mainhand from block 0 -64 0 container.0

# 直接调用 Loot Table 返还物品 (极大简化！)
# 这里利用了我们之前生成的 items/fishing/tackle_xxx.json
execute if score @s sd_detach_id matches 5001 run loot give @s loot stardew:items/fishing/tackle_sonar
execute if score @s sd_detach_id matches 5002 run loot give @s loot stardew:items/fishing/tackle_quality
execute if score @s sd_detach_id matches 5003 run loot give @s loot stardew:items/fishing/tackle_spinner
execute if score @s sd_detach_id matches 5004 run loot give @s loot stardew:items/fishing/tackle_trap
execute if score @s sd_detach_id matches 5005 run loot give @s loot stardew:items/fishing/tackle_cork
execute if score @s sd_detach_id matches 5006 run loot give @s loot stardew:items/fishing/tackle_treasure
execute if score @s sd_detach_id matches 5007 run loot give @s loot stardew:items/fishing/tackle_barbed

# 冷却 & 反馈
tag @s add sd_op_cooldown
schedule function stardew:tools/reset_cooldown 20t
playsound minecraft:item.bundle.remove_one player @s ~ ~ ~ 1 1.2
tellraw @s [{"text":"✓ 渔具已卸载。","color":"green","italic":false}]

# 清理
scoreboard players set @s sd_const 0
scoreboard players set @s sd_temp 0
scoreboard players set @s sd_tackle1 0
scoreboard players set @s sd_tackle2 0
scoreboard players set @s sd_detach_id 0