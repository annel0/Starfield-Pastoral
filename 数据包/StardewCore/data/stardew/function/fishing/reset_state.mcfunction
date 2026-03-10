# data/stardew/functions/fishing/reset_state.mcfunction
# 用于强制重置玩家的所有钓鱼相关状态

# 1. 移除所有状态标签
tag @s remove is_fighting_fish
tag @s remove is_stardew_rod

# 2. 归零所有计数器
scoreboard players set @s sd_fishing_tick 0
scoreboard players set @s sd_fish_phase 0
scoreboard players set @s sd_fish_progress 0
scoreboard players set @s sd_fish_type 0
scoreboard players set @s sd_bite_time 0
scoreboard players set @s sd_fish_ready 0
scoreboard players set @s sd_bite_window 0
scoreboard players set @s sd_fish_bite_state 0
# [新增] 重置鱼钩安全锁
scoreboard players set @s sd_hook_safe 0
# [修复] 重置Shift检测状态，否则第二次钓鱼时Shift检测失效
scoreboard players set @s sd_sneak_last 0
scoreboard players set @s sd_is_sneaking 0

# 3. 隐藏 Bossbar
bossbar set stardew:fishing visible false

# 4. 杀掉附近的鱼钩 (可选，但推荐)
# execute as @e[type=fishing_bobber,distance=..32] run kill @s