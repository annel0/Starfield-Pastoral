# data/stardew/functions/fishing/win_fight.mcfunction
# 执行者: 玩家

# 1. 提示胜利
playsound minecraft:entity.player.levelup player @s ~ ~ ~ 1 1
title @s subtitle {"text":"收杆成功！","color":"green","bold":true}
title @s title {"text":"🎉 捕获 🎉","color":"gold","bold":true}

# 2. 先执行鱼类掉落
function stardew:fishing/loot_drop

# 3. [新增] 执行宝藏判定 (额外掉落，互不冲突)
function stardew:fishing/try_spawn_treasure

# 3.5 [新增] 消耗能量 - 成功钓上任何东西扣除5点能量
scoreboard players remove @s sd_energy 5
execute if score @s sd_energy matches ..0 run scoreboard players set @s sd_energy 0

# 4. 隐藏 Bossbar
bossbar set stardew:fishing visible false

# 4.5 [修复] 杀死鱼钩实体，让鱼竿收回
execute at @s as @e[type=fishing_bobber,distance=..32,limit=1] run kill @s

# 5. 清理状态
tag @s remove is_fighting_fish
scoreboard players set @s sd_fishing_tick 0
scoreboard players set @s sd_bite_time 0
scoreboard players set @s sd_fish_type 0
scoreboard players set @s sd_fish_shake 0
scoreboard players set @s sd_fish_phase 0
scoreboard players set @s sd_fish_pull_time 0
scoreboard players set @s sd_fish_progress 0
# [修复] 重置Shift检测状态
scoreboard players set @s sd_sneak_last 0
scoreboard players set @s sd_is_sneaking 0
scoreboard players set @s sd_fish_ready 0
scoreboard players set @s sd_bite_window 0