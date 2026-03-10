# data/stardew/functions/fishing/pull_logic.mcfunction
# [执行者: 玩家，每 tick 调用]

# 0. 不在战斗就不用管
execute unless entity @s[tag=is_fighting_fish] run return 0

# 1. 检测 Shift 状态
scoreboard players set @s sd_is_sneaking 0
execute if entity @s[predicate=stardew:is_sneaking] run scoreboard players set @s sd_is_sneaking 1

# 2. 不在蹲下也什么都不做
execute unless score @s sd_is_sneaking matches 1 run return 0

# 2.5 保护时间
execute if score @s sd_fish_safe matches 1.. run return 0

# 3. 平静期乱拉 = 惩罚 (鱼没动你乱动)
# 惩罚力度随难度平滑递增,确保所有难度都需要集中精力
scoreboard players set @s sd_pull_penalty 25
execute if score @s sd_final_difficulty matches 2 run scoreboard players set @s sd_pull_penalty 28
execute if score @s sd_final_difficulty matches 3 run scoreboard players set @s sd_pull_penalty 31
execute if score @s sd_final_difficulty matches 4 run scoreboard players set @s sd_pull_penalty 34
execute if score @s sd_final_difficulty matches 5 run scoreboard players set @s sd_pull_penalty 37
execute if score @s sd_final_difficulty matches 6 run scoreboard players set @s sd_pull_penalty 40
execute if score @s sd_final_difficulty matches 7 run scoreboard players set @s sd_pull_penalty 43
execute if score @s sd_final_difficulty matches 8 run scoreboard players set @s sd_pull_penalty 46
execute if score @s sd_final_difficulty matches 9 run scoreboard players set @s sd_pull_penalty 49
execute if score @s sd_final_difficulty matches 10 run scoreboard players set @s sd_pull_penalty 52
execute if score @s sd_final_difficulty matches 11 run scoreboard players set @s sd_pull_penalty 55
execute if score @s sd_final_difficulty matches 12 run scoreboard players set @s sd_pull_penalty 58
execute if score @s sd_final_difficulty matches 13 run scoreboard players set @s sd_pull_penalty 62
execute if score @s sd_final_difficulty matches 14 run scoreboard players set @s sd_pull_penalty 66
execute if score @s sd_final_difficulty matches 15.. run scoreboard players set @s sd_pull_penalty 70

execute if score @s sd_fish_phase matches 1 run scoreboard players operation @s sd_fish_progress -= @s sd_pull_penalty
execute if score @s sd_fish_phase matches 1 run playsound minecraft:block.note_block.bass player @s ~ ~ ~ 0.7 0.7
execute if score @s sd_fish_phase matches 1 run bossbar set stardew:fishing color red
execute if score @s sd_fish_phase matches 1 if score @s sd_fish_hint_cd matches ..0 run tellraw @s ["",{"text":"[钓鱼] ","color":"aqua"},{"text":"鱼还没开始挣扎，现在按 ","color":"gray"},{"keybind":"key.sneak","color":"yellow"},{"text":" 只会吓跑它！","color":"red","bold":true}]
execute if score @s sd_fish_phase matches 1 if score @s sd_fish_hint_cd matches ..0 run scoreboard players set @s sd_fish_hint_cd 60

# 4. 反抗期蹲下 = 涨进度 (核心逻辑)
# ==========================================
# [优化] 动态计算拉力 (软木塞浮标支持)
# ==========================================

# 4.1 设定基础拉力
scoreboard players set @s sd_pull_power 28

# 4.2 检测渔具：软木塞浮标 (ID 5005)
function stardew:fishing/utils/check_tackle
# 如果装备了软木塞，拉力 +12 (变成 40，更容易把鱼拉回来)
execute if score @s sd_tackle_id matches 5005 run scoreboard players add @s sd_pull_power 12

# 4.3 执行增加进度
execute if score @s sd_fish_phase matches 2 run scoreboard players operation @s sd_fish_progress += @s sd_pull_power

# 4.4 视觉效果
execute if score @s sd_fish_phase matches 2 at @s run tp @e[type=fishing_bobber,distance=..32,limit=1] ^ ^ ^0.03
execute if score @s sd_fish_phase matches 2 run playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 0.5 2.0

# 5. 更新 bossbar
execute store result bossbar stardew:fishing value run scoreboard players get @s sd_fish_progress
