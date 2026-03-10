# data/stardew/functions/fishing/select_trash.mcfunction
# [执行者: 玩家]
# 作用：当选鱼逻辑决定生成“垃圾”时调用此函数，随机分配具体的垃圾 ID

# 1. 再次掷骰子 (1-100) 决定具体垃圾种类
execute store result score Global sd_rng run random value 1..100

# ==============================================================
# A. 通用垃圾 (60% 概率)
# 漂流木(40010), 垃圾(40020), 报纸(40030), 眼镜(40040), 可乐(40050), 烂植物(40060)
# ==============================================================
execute if score Global sd_rng matches 1..10 run scoreboard players set @s sd_fish_type 40010
execute if score Global sd_rng matches 11..20 run scoreboard players set @s sd_fish_type 40020
execute if score Global sd_rng matches 21..30 run scoreboard players set @s sd_fish_type 40030
execute if score Global sd_rng matches 31..40 run scoreboard players set @s sd_fish_type 40040
execute if score Global sd_rng matches 41..50 run scoreboard players set @s sd_fish_type 40050
execute if score Global sd_rng matches 51..60 run scoreboard players set @s sd_fish_type 40060

# ==============================================================
# B. 区域特产资源 (40% 概率)
# ==============================================================

# --- 河流(1) / 湖泊(2) -> 绿藻 (40100) ---
execute if score @s sd_fish_region matches 1..2 if score Global sd_rng matches 61..100 run scoreboard players set @s sd_fish_type 40100

# --- 海洋(3) -> 海草 (40120) ---
execute if score @s sd_fish_region matches 3 if score Global sd_rng matches 61..100 run scoreboard players set @s sd_fish_type 40120

# --- 特殊区域(4) -> 白藻 (40110) ---
execute if score @s sd_fish_region matches 4 if score Global sd_rng matches 61..100 run scoreboard players set @s sd_fish_type 40110

# 设定统一的垃圾咬钩时间 (比较快)
scoreboard players set @s sd_bite_time 30