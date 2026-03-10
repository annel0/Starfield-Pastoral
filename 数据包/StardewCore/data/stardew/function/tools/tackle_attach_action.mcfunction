# data/stardew/functions/tools/tackle_attach_action.mcfunction
# [执行者: 玩家]

# 0. 阻止原版抛竿
execute as @s at @s run kill @e[type=fishing_bobber,distance=..4,limit=1,sort=nearest]

# ==========================================
# 1. 前置检查
# ==========================================
execute store result score @s sd_rod_type run data get entity @s SelectedItem.components."minecraft:custom_model_data"
execute unless score @s sd_rod_type matches 403..404 run tellraw @s [{"text":"❌ 只有金制(403)及以上鱼竿可装备渔具！","color":"red","italic":false}]
execute unless score @s sd_rod_type matches 403..404 run return 0

execute store result score @s sd_new_tackle run data get entity @s Inventory[{Slot:-106b}].components."minecraft:custom_model_data"
execute unless score @s sd_new_tackle matches 5001..5999 run tellraw @s [{"text":"❌ 副手必须持有有效渔具！","color":"red","italic":false}]
execute unless score @s sd_new_tackle matches 5001..5999 run return 0

execute store result score @s sd_config run data get entity @s Inventory[{Slot:-106b}].components."minecraft:custom_data".min_level
execute if score @s sd_fishing_lvl < @s sd_config run tellraw @s [{"text":"❌ 等级不足！这个渔具需要 Lv.","color":"red","italic":false},{"score":{"name":"@s","objective":"sd_config"},"color":"yellow","bold":true,"italic":false}]
execute if score @s sd_fishing_lvl < @s sd_config run return 0

# 读取副手ID
execute store result score @s sd_new_tackle run data get entity @s Inventory[{Slot:-106b}].components."minecraft:custom_data".tackle_id


# ==========================================
# 2. Storage 操作 (写入槽位)
# ==========================================
data modify storage stardew:temp TargetRod set from entity @s SelectedItem
data modify storage stardew:temp TargetRod.Slot set value 0b

# 确保数据结构存在
execute unless data storage stardew:temp TargetRod.components."minecraft:custom_data" run data modify storage stardew:temp TargetRod.components."minecraft:custom_data" set value {}
execute unless data storage stardew:temp TargetRod.components."minecraft:custom_data".tackle_1 run data modify storage stardew:temp TargetRod.components."minecraft:custom_data".tackle_1 set value 0
execute unless data storage stardew:temp TargetRod.components."minecraft:custom_data".tackle_2 run data modify storage stardew:temp TargetRod.components."minecraft:custom_data".tackle_2 set value 0

# 读取当前槽位
execute store result score @s sd_tackle1 run data get storage stardew:temp TargetRod.components."minecraft:custom_data".tackle_1
execute store result score @s sd_tackle2 run data get storage stardew:temp TargetRod.components."minecraft:custom_data".tackle_2

# 决策逻辑
execute if score @s sd_tackle1 matches 0 run scoreboard players set @s sd_temp 1
execute if score @s sd_tackle1 matches 1.. if score @s sd_tackle2 matches 0 if score @s sd_rod_type matches 404 run scoreboard players set @s sd_temp 2
execute if score @s sd_tackle1 matches 1.. if score @s sd_rod_type matches 403 run scoreboard players set @s sd_temp -1
execute if score @s sd_tackle2 matches 1.. run scoreboard players set @s sd_temp -1

# 满载检查
execute if score @s sd_temp matches -1 run tellraw @s [{"text":"❌ 渔具槽已满！","color":"red","italic":false}]
execute if score @s sd_temp matches -1 run return 0

# 写入新ID
execute store result storage stardew:temp NewTackleId int 1 run scoreboard players get @s sd_new_tackle
execute if score @s sd_temp matches 1 run data modify storage stardew:temp TargetRod.components."minecraft:custom_data".tackle_1 set from storage stardew:temp NewTackleId
execute if score @s sd_temp matches 2 run data modify storage stardew:temp TargetRod.components."minecraft:custom_data".tackle_2 set from storage stardew:temp NewTackleId


# ==========================================
# 3. 重绘 Lore (暴力穷举版 - 绝对稳定)
# ==========================================
# 重新读取状态
execute store result score @s sd_tackle1 run data get storage stardew:temp TargetRod.components."minecraft:custom_data".tackle_1
execute store result score @s sd_tackle2 run data get storage stardew:temp TargetRod.components."minecraft:custom_data".tackle_2

data modify storage stardew:temp TargetRod.components."minecraft:lore" set value []

# --- 头部信息 (等级/钓力) ---
# 金竿
execute if score @s sd_rod_type matches 403 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"📊 🎣 钓鱼等级要求: ","color":"white","italic":false,"extra":[{"text":"6","color":"green","bold":true,"italic":false}]}'
execute if score @s sd_rod_type matches 403 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"💪 基础钓力: ","color":"white","italic":false,"extra":[{"text":"4","color":"aqua","bold":true,"italic":false}]}'
# 钻竿
execute if score @s sd_rod_type matches 404 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"📊 🎣 钓鱼等级要求: ","color":"white","italic":false,"extra":[{"text":"9","color":"green","bold":true,"italic":false}]}'
execute if score @s sd_rod_type matches 404 run data modify storage stardew:temp TargetRod.components."minecraft:lore" append value '{"text":"💪 基础钓力: ","color":"white","italic":false,"extra":[{"text":"5","color":"aqua","bold":true,"italic":false}]}'

# ==========================================================
# ▼▼▼ 把你脚本生成的代码粘贴在这里 (替换原来的空行) ▼▼▼
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
# 4. 替换 & 收尾
# ==========================================
setblock 0 -64 0 barrel
data modify block 0 -64 0 Items append from storage stardew:temp TargetRod
item replace entity @s weapon.mainhand from block 0 -64 0 container.0

item replace entity @s weapon.offhand with air

tag @s add sd_op_cooldown
schedule function stardew:tools/reset_cooldown 20t
playsound minecraft:block.anvil.use player @s ~ ~ ~ 0.5 1.5
tellraw @s [{"text":"✓ 渔具装备成功！","color":"green","bold":true,"italic":false}]

# 清理
scoreboard players set @s sd_const 0
scoreboard players set @s sd_temp 0
scoreboard players set @s sd_config 0
scoreboard players set @s sd_tackle1 0
scoreboard players set @s sd_tackle2 0