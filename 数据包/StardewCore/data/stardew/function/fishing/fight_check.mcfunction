# data/stardew/functions/fishing/fight_check.mcfunction

# 0. 【防崩坏补丁】确保钓鱼需要的计分板存在
scoreboard objectives add sd_fishing_tick dummy
scoreboard objectives add sd_fish_ready dummy
scoreboard objectives add sd_rng dummy
scoreboard players set sd_rng_max_10 sd_const 10

# ======================================================
# ① 战斗中 (Fighting)
# ======================================================
# 如果有战斗标签 + 有鱼钩 -> 跑战斗逻辑
execute if entity @s[tag=is_fighting_fish] if entity @e[type=fishing_bobber,distance=..32,limit=1] run function stardew:fishing/player_fight_status
# 如果有战斗标签 + 没鱼钩 -> 判负
execute if entity @s[tag=is_fighting_fish] unless entity @e[type=fishing_bobber,distance=..32,limit=1] run function stardew:fishing/lose_fight
# 如果还在战斗中，直接退出，不再跑下面的等待逻辑
execute if entity @s[tag=is_fighting_fish] run return 0


# ======================================================
# ② 咬钩反应阶段 (Bite Reaction) - 浮标沉下去那一刻
# ======================================================
# 如果处于咬钩状态 (ready=1)，但鱼钩没了 -> 重置
execute if score @s sd_fish_ready matches 1 unless entity @e[type=fishing_bobber,distance=..32,limit=1] run scoreboard players set @s sd_fish_ready 0

# [核心修复] 这里不能写 "matches 0 run return 0"，否则永远进不了下一阶段！
# 我们只处理 ready=1 的情况，ready=0 的情况会自动流向第 ③ 部分

# 如果 ready=1: 反应时间倒计时
execute if score @s sd_fish_ready matches 1 if score @s sd_bite_window matches 1.. run scoreboard players remove @s sd_bite_window 1

# 如果 ready=1 + 按下Shift -> 开始战斗
execute if score @s sd_fish_ready matches 1 if score @s sd_bite_window matches 1.. if score @s sd_is_sneaking matches 1 run function stardew:fishing/start_fight
# 如果刚进战斗，清理 ready 状态并退出
execute if entity @s[tag=is_fighting_fish] run scoreboard players set @s sd_fish_ready 0
execute if entity @s[tag=is_fighting_fish] run scoreboard players set @s sd_bite_window 0
execute if entity @s[tag=is_fighting_fish] run return 0

# 如果 ready=1 + 时间耗尽 -> 鱼跑了
execute if score @s sd_fish_ready matches 1 if score @s sd_bite_window matches ..0 run function stardew:fishing/miss_bite
# 如果跑了，退出
execute if score @s sd_fish_ready matches 1 if score @s sd_bite_window matches ..0 run return 0

# 如果 ready=1 (还在等玩家按键)，就不要增加正常计时了，退出
execute if score @s sd_fish_ready matches 1 run return 0


# ======================================================
# ③ 正常等待咬钩逻辑 (Waiting) - 还没动静的时候
# ======================================================

# 如果没有鱼钩 -> 重置所有数据
execute unless entity @e[type=fishing_bobber,distance=..32,limit=1] run scoreboard players set @s sd_fishing_tick 0
execute unless entity @e[type=fishing_bobber,distance=..32,limit=1] run scoreboard players set @s sd_bite_time 0
execute unless entity @e[type=fishing_bobber,distance=..32,limit=1] run scoreboard players set @s sd_fish_type 0
execute unless entity @e[type=fishing_bobber,distance=..32,limit=1] run return 0

# 刚抛竿 (tick=0) -> 选鱼、定时间
execute if score @s sd_fishing_tick matches 0 run function stardew:fishing/select_fish

# 计时器 +1
scoreboard players add @s sd_fishing_tick 1

# 时间到了 -> 触发咬钩 (进入 ready=1 状态)
execute if score @s sd_bite_time matches 1.. if score @s sd_fishing_tick >= @s sd_bite_time run function stardew:fishing/on_bite