# data/stardew/functions/fishing/player_fight_status.mcfunction
# [执行者: 玩家]

# 0. 战斗计时
scoreboard players remove @s sd_fishing_tick 1
execute if score @s sd_fishing_tick matches ..0 run function stardew:fishing/win_fight
execute if score @s sd_fishing_tick matches ..0 run return 0

# 1. 距离检测
execute if entity @e[type=fishing_bobber,distance=30..] run function stardew:fishing/lose_fight

# 2. 提示冷却
execute if score @s sd_fish_hint_cd matches 1.. run scoreboard players remove @s sd_fish_hint_cd 1

# 3. 检测渔具 (只需检测一次)
function stardew:fishing/utils/check_tackle

# ==================================================
# 3.1 阶段控制 (鱼的 AI) - 平静期
# ==================================================
execute if score @s sd_fish_phase matches 1 run scoreboard players add @s sd_fish_shake 1

# 挣扎频率计算 - 改为随机触发
# 每tick有一定概率进入挣扎期,难度越高概率越大
# 同时设置最小平静时长,防止太快切换

# 首先检查是否达到最小平静时长
scoreboard players set @s sd_const 20
execute if score @s sd_final_difficulty matches 2..3 run scoreboard players set @s sd_const 18
execute if score @s sd_final_difficulty matches 4..6 run scoreboard players set @s sd_const 16
execute if score @s sd_final_difficulty matches 7..9 run scoreboard players set @s sd_const 14
execute if score @s sd_final_difficulty matches 10..12 run scoreboard players set @s sd_const 12
execute if score @s sd_final_difficulty matches 13.. run scoreboard players set @s sd_const 10

# 如果达到最小时长,开始随机判定
execute if score @s sd_fish_phase matches 1 if score @s sd_fish_shake >= @s sd_const run function stardew:fishing/check_struggle_trigger

# 平静期惩罚 (自然滑落) - 只有极难才掉
# 低难度(1-12): 不掉进度,给玩家喘息时间
# 极难(13-15): 持续掉落,保持高压
scoreboard players set @s sd_const 0
execute if score @s sd_final_difficulty matches 13 run scoreboard players set @s sd_const 6
execute if score @s sd_final_difficulty matches 14 run scoreboard players set @s sd_const 10
execute if score @s sd_final_difficulty matches 15.. run scoreboard players set @s sd_const 14

execute if score @s sd_fish_phase matches 1 run scoreboard players operation @s sd_fish_progress -= @s sd_const

# 视觉更新
execute if score @s sd_fish_phase matches 1 run bossbar set stardew:fishing color white
execute if score @s sd_fish_phase matches 1 if score @s sd_const matches 0 run bossbar set stardew:fishing name {"text":"鱼暂时平静了下来...","color":"green"}
execute if score @s sd_fish_phase matches 1 if score @s sd_const matches 1.. run bossbar set stardew:fishing name {"text":"鱼在慢慢滑走... (等待挣扎时机)","color":"gray"}


# ==================================================
# 3.2 阶段控制 - 反抗期 (鱼在挣扎！)
# ==================================================
# 倒计时
execute if score @s sd_fish_phase matches 2 run scoreboard players remove @s sd_fish_pull_time 1
execute if score @s sd_fish_phase matches 2 if score @s sd_fish_pull_time matches ..0 run function stardew:fishing/end_pull_phase

execute if score @s sd_fish_phase matches 2 at @s run execute as @e[type=fishing_bobber,distance=..32,limit=1] rotated as @s run tp @s ^ ^ ^-0.1

# --- 计算基础逃跑惩罚 (更精细的15档难度) ---
# 低难度: 逃跑慢
# 高难度: 逃跑快 (不按Shift后果严重)
scoreboard players set @s sd_const 8
execute if score @s sd_final_difficulty matches 2 run scoreboard players set @s sd_const 10
execute if score @s sd_final_difficulty matches 3 run scoreboard players set @s sd_const 12
execute if score @s sd_final_difficulty matches 4 run scoreboard players set @s sd_const 14
execute if score @s sd_final_difficulty matches 5 run scoreboard players set @s sd_const 16
execute if score @s sd_final_difficulty matches 6 run scoreboard players set @s sd_const 18
execute if score @s sd_final_difficulty matches 7 run scoreboard players set @s sd_const 21
execute if score @s sd_final_difficulty matches 8 run scoreboard players set @s sd_const 24
execute if score @s sd_final_difficulty matches 9 run scoreboard players set @s sd_const 27
execute if score @s sd_final_difficulty matches 10 run scoreboard players set @s sd_const 30
execute if score @s sd_final_difficulty matches 11 run scoreboard players set @s sd_const 34
execute if score @s sd_final_difficulty matches 12 run scoreboard players set @s sd_const 38
execute if score @s sd_final_difficulty matches 13 run scoreboard players set @s sd_const 42
execute if score @s sd_final_difficulty matches 14 run scoreboard players set @s sd_const 46
execute if score @s sd_final_difficulty matches 15.. run scoreboard players set @s sd_const 50

# --- [实装] 陷阱浮标 (Trap Bobber - 5004) ---
# 效果：逃跑速度除以 3
execute if score @s sd_tackle_id matches 5004 run scoreboard players operation @s sd_const /= #3 sd_const

# --- [实装] 倒刺钩 (Barbed Hook - 5007) ---
# 效果：完全不逃跑 (自动吸附的简易实现)
# 如果有倒刺钩，将惩罚直接设为 0 (进度条不会掉，只要你稍微按按Shift就能赢)
execute if score @s sd_tackle_id matches 5007 run scoreboard players set @s sd_const 0

# --- 执行扣除 ---
execute if score @s sd_fish_phase matches 2 run scoreboard players operation @s sd_fish_progress -= @s sd_const

# 视觉更新
execute if score @s sd_fish_phase matches 2 run bossbar set stardew:fishing color red
execute if score @s sd_fish_phase matches 2 run bossbar set stardew:fishing name {"text":"鱼在挣扎！按住 Shift！","color":"red","bold":true}

# ==================================================
# 4. 结算
# ==================================================

# 限制范围
execute if score @s sd_fish_progress matches 1000.. run scoreboard players set @s sd_fish_progress 1000

# 判负逻辑
execute if score @s sd_fish_progress matches ..0 run function stardew:fishing/lose_fight
execute unless entity @s[tag=is_fighting_fish] run return 0

# 判胜逻辑
execute if score @s sd_fish_progress matches 1000.. run function stardew:fishing/win_fight
execute unless entity @s[tag=is_fighting_fish] run return 0

# 更新 Bossbar
execute store result bossbar stardew:fishing value run scoreboard players get @s sd_fish_progress

# 5. 显示倒计时 (调试模式外)
scoreboard players operation @s sd_time_tmp = @s sd_fishing_tick
scoreboard players operation @s sd_time_tmp /= #20 sd_const
scoreboard players operation @s sd_time_min = @s sd_time_tmp
scoreboard players operation @s sd_time_min /= #60 sd_const
scoreboard players operation @s sd_time_sec = @s sd_time_tmp
scoreboard players operation @s sd_time_sec %= #60 sd_const

# 集成到 actionbar 特殊进度条（类型2=钓鱼）
scoreboard players operation @s sd_special_value = @s sd_fishing_tick
scoreboard players set @s sd_special_max 1200
scoreboard players set @s sd_special_type 2