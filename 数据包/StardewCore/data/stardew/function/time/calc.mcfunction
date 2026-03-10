# data/stardew/functions/time/calc.mcfunction

# 1. 重置 tick 计数器
scoreboard players set Global sd_tick_counter 0

# 2. 增加时间 (1分钟)
scoreboard players add Global sd_time 1

# 3. 跨维度NPC日程更新 - 每分钟执行一次,确保NPC无论玩家在哪个维度都能更新
function stardew:npc/cross_dimension_update

# --- 关键时间点检测（使用范围检测，防止跳过） ---

# 18:00 (1080分钟) -> 动物回家（触发一次后设置标记）
execute if score Global sd_time matches 1080.. unless score Global sd_event_1800 matches 1 run function stardew:building/animal/start_going_home
execute if score Global sd_time matches 1080.. unless score Global sd_event_1800 matches 1 run scoreboard players set Global sd_event_1800 1

# 22:00 (1320分钟) -> 检查动物是否在外面
execute if score Global sd_time matches 1320.. unless score Global sd_event_2200 matches 1 run function stardew:building/animal/check_animals_outside
execute if score Global sd_time matches 1320.. unless score Global sd_event_2200 matches 1 run scoreboard players set Global sd_event_2200 1

# 00:00 (1440分钟) -> 黄色警告
execute if score Global sd_time matches 1440.. unless score Global sd_event_0000 matches 1 run tellraw @a {"text":"已经很晚了……","color":"yellow"}
execute if score Global sd_time matches 1440.. unless score Global sd_event_0000 matches 1 run playsound minecraft:block.note_block.bass player @a ~ ~ ~ 1 0.5
execute if score Global sd_time matches 1440.. unless score Global sd_event_0000 matches 1 run scoreboard players set Global sd_event_0000 1

# 01:30 (1530分钟) -> 红色警告
execute if score Global sd_time matches 1530.. unless score Global sd_event_0130 matches 1 run tellraw @a {"text":"你必须找个地方休息一下了……","color":"red","bold":true}
execute if score Global sd_time matches 1530.. unless score Global sd_event_0130 matches 1 run playsound minecraft:entity.ender_dragon.growl player @a ~ ~ ~ 0.5 2.0
execute if score Global sd_time matches 1530.. unless score Global sd_event_0130 matches 1 run scoreboard players set Global sd_event_0130 1

# --- 过夜判定 ---

# 02:00 (1560分钟) -> 强制昏迷
execute if score Global sd_time matches 1560.. run function stardew:time/new_day

# 光照同步 (可选)
time add 16