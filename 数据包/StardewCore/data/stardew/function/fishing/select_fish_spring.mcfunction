# data/stardew/functions/fishing/select_fish_spring.mcfunction
# Spring季钓鱼选择逻辑
# 共 23 种鱼

# 概率分配:
# - 垃圾: 15% (RNG 1-15)
# - 传说鱼: 1种 × 2% = 2%
# - 极稀有鱼(难度90+): 2种 × 3% = 6%
# - 稀有鱼(难度70-89): 4种 × 5% = 20%
# - 罕见鱼(难度50-69): 7种 × 7% = 49%
# - 常见鱼(难度<50): 9种共 8%

# 重置鱼类型
scoreboard players set @s sd_fish_type 0

# 垃圾 (15% 概率)
execute if score @s sd_fish_region matches 1..12 if score Global sd_rng matches 1..15 run function stardew:fishing/select_trash

# ============================================================
# 传说鱼
# ============================================================

# 传说之王 (Legend) - 难度 110, 概率 2%
execute if score @s sd_fish_region matches 8 if score Global sd_weather matches 1 if score Global sd_rng matches 16..17 run scoreboard players set @s sd_fish_type 41990
execute if score @s sd_fish_type matches 41990 run scoreboard players set @s sd_bite_time 160

# ============================================================
# 极稀有鱼
# ============================================================

# 岩浆鳗鱼 (Lava Eel) - 难度 90, 概率 3%
execute if score @s sd_fish_region matches 11 if score Global sd_rng matches 18..20 run scoreboard players set @s sd_fish_type 45030
execute if score @s sd_fish_type matches 45030 run scoreboard players set @s sd_bite_time 134

# 蝎鲤 (Scorpion Carp) - 难度 90, 概率 3%
execute if score @s sd_fish_region matches 12 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 21..23 run scoreboard players set @s sd_fish_type 45050
execute if score @s sd_fish_type matches 45050 run scoreboard players set @s sd_bite_time 134

# ============================================================
# 稀有鱼
# ============================================================

# 冰晶 (Ice Pip) - 难度 85, 概率 5%
execute if score @s sd_fish_region matches 11 if score Global sd_rng matches 24..28 run scoreboard players set @s sd_fish_type 45020
execute if score @s sd_fish_type matches 45020 run scoreboard players set @s sd_bite_time 128

# 鲶鱼 (Catfish) - 难度 75, 概率 5%
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 0 if score Global sd_weather matches 1 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_region matches 4 if score @s sd_time_slot matches 0 if score Global sd_weather matches 1 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 2 if score Global sd_weather matches 1 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_region matches 4 if score @s sd_time_slot matches 2 if score Global sd_weather matches 1 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 3 if score Global sd_weather matches 1 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_region matches 4 if score @s sd_time_slot matches 3 if score Global sd_weather matches 1 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 4 if score Global sd_weather matches 1 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_region matches 4 if score @s sd_time_slot matches 4 if score Global sd_weather matches 1 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 5 if score Global sd_weather matches 1 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_region matches 4 if score @s sd_time_slot matches 5 if score Global sd_weather matches 1 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 6 if score Global sd_weather matches 1 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_region matches 4 if score @s sd_time_slot matches 6 if score Global sd_weather matches 1 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 7 if score Global sd_weather matches 1 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_region matches 4 if score @s sd_time_slot matches 7 if score Global sd_weather matches 1 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_type matches 41020 run scoreboard players set @s sd_bite_time 115

# 鳗鱼 (Eel) - 难度 70, 概率 5%
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0 if score Global sd_weather matches 1 if score Global sd_rng matches 34..38 run scoreboard players set @s sd_fish_type 41090
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 1 if score Global sd_weather matches 1 if score Global sd_rng matches 34..38 run scoreboard players set @s sd_fish_type 41090
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 5 if score Global sd_weather matches 1 if score Global sd_rng matches 34..38 run scoreboard players set @s sd_fish_type 41090
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 6 if score Global sd_weather matches 1 if score Global sd_rng matches 34..38 run scoreboard players set @s sd_fish_type 41090
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 7 if score Global sd_weather matches 1 if score Global sd_rng matches 34..38 run scoreboard players set @s sd_fish_type 41090
execute if score @s sd_fish_type matches 41090 run scoreboard players set @s sd_bite_time 109

# 海鳗 (Sea Eel) - 难度 80, 概率 5%
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0 if score Global sd_rng matches 39..43 run scoreboard players set @s sd_fish_type 43070
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 1 if score Global sd_rng matches 39..43 run scoreboard players set @s sd_fish_type 43070
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 5 if score Global sd_rng matches 39..43 run scoreboard players set @s sd_fish_type 43070
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 6 if score Global sd_rng matches 39..43 run scoreboard players set @s sd_fish_type 43070
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 7 if score Global sd_rng matches 39..43 run scoreboard players set @s sd_fish_type 43070
execute if score @s sd_fish_type matches 43070 run scoreboard players set @s sd_bite_time 122

# ============================================================
# 罕见鱼
# ============================================================

# 石鱼 (Stonefish) - 难度 65, 概率 7%
execute if score @s sd_fish_region matches 11 if score Global sd_rng matches 44..50 run scoreboard players set @s sd_fish_type 45010
execute if score @s sd_fish_type matches 45010 run scoreboard players set @s sd_bite_time 103

# 大口黑鲈 (Largemouth Bass) - 难度 50, 概率 7%
execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 51..57 run scoreboard players set @s sd_fish_type 43020
execute if score @s sd_fish_type matches 43020 run scoreboard players set @s sd_bite_time 84

# 比目鱼 (Flounder) - 难度 50, 概率 7%
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 58..64 run scoreboard players set @s sd_fish_type 46030
execute if score @s sd_fish_type matches 46030 run scoreboard players set @s sd_bite_time 84

# 大比目鱼 (Halibut) - 难度 50, 概率 7%
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0 if score Global sd_rng matches 65..71 run scoreboard players set @s sd_fish_type 41080
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 1 if score Global sd_rng matches 65..71 run scoreboard players set @s sd_fish_type 41080
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 2 if score Global sd_rng matches 65..71 run scoreboard players set @s sd_fish_type 41080
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 3 if score Global sd_rng matches 65..71 run scoreboard players set @s sd_fish_type 41080
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 6 if score Global sd_rng matches 65..71 run scoreboard players set @s sd_fish_type 41080
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 7 if score Global sd_rng matches 65..71 run scoreboard players set @s sd_fish_type 41080
execute if score @s sd_fish_type matches 41080 run scoreboard players set @s sd_bite_time 84

# 沙鱼 (Sandfish) - 难度 65, 概率 7%
execute if score @s sd_fish_region matches 12 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 72..78 run scoreboard players set @s sd_fish_type 45040
execute if score @s sd_fish_type matches 45040 run scoreboard players set @s sd_bite_time 103

# 木跃鱼 (Woodskip) - 难度 50, 概率 7%
execute if score @s sd_fish_region matches 4 if score Global sd_rng matches 79..85 run scoreboard players set @s sd_fish_type 46040
execute if score @s sd_fish_type matches 46040 run scoreboard players set @s sd_bite_time 84

# 幽灵鱼 (Ghostfish) - 难度 50, 概率 7%
execute if score @s sd_fish_region matches 11 if score Global sd_rng matches 86..92 run scoreboard players set @s sd_fish_type 45000
execute if score @s sd_fish_type matches 45000 run scoreboard players set @s sd_bite_time 84

# ============================================================
# 常见鱼 - 地区1(河流)重新分配
# ============================================================

# 小口黑鲈 (Smallmouth Bass) - 难度 28, 概率 15%
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 0..7 if score Global sd_rng matches 34..48 run scoreboard players set @s sd_fish_type 41000
execute if score @s sd_fish_region matches 5 if score @s sd_time_slot matches 0..7 if score Global sd_rng matches 34..48 run scoreboard players set @s sd_fish_type 41000
execute if score @s sd_fish_type matches 41000 run scoreboard players set @s sd_bite_time 56

# 鲦鱼 (Chub) - 难度 35, 概率 12%
execute if score @s sd_fish_region matches 1 if score Global sd_rng matches 49..60 run scoreboard players set @s sd_fish_type 46010
execute if score @s sd_fish_region matches 2 if score Global sd_rng matches 92..93 run scoreboard players set @s sd_fish_type 46010
execute if score @s sd_fish_type matches 46010 run scoreboard players set @s sd_bite_time 65

# 鲤科鱼 (Bream) - 难度 35, 概率 10% (晚间概率更高)
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 0..1 if score Global sd_rng matches 61..70 run scoreboard players set @s sd_fish_type 46000
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 5..7 if score Global sd_rng matches 61..70 run scoreboard players set @s sd_fish_type 46000
execute if score @s sd_fish_type matches 46000 run scoreboard players set @s sd_bite_time 65

# 太阳鱼 (Sunfish) - 难度 30, 概率 15% (晴天白天)
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 2..6 if score Global sd_weather matches 0 if score Global sd_rng matches 71..85 run scoreboard players set @s sd_fish_type 41030
execute if score @s sd_fish_type matches 41030 run scoreboard players set @s sd_bite_time 58

# 西鲱 (Shad) - 难度 45, 概率 8% (雨天)
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 0..1 if score Global sd_weather matches 1 if score Global sd_rng matches 71..78 run scoreboard players set @s sd_fish_type 41010
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 3..7 if score Global sd_weather matches 1 if score Global sd_rng matches 71..78 run scoreboard players set @s sd_fish_type 41010
execute if score @s sd_fish_type matches 41010 run scoreboard players set @s sd_bite_time 77

# 鲈鱼 (Perch) - 难度 35, 概率 10% (春季河流常见鱼)
execute if score @s sd_fish_region matches 1 if score Global sd_rng matches 16..25 run scoreboard players set @s sd_fish_type 41050
execute if score @s sd_fish_type matches 41050 run scoreboard players set @s sd_bite_time 65

# 鲶鱼 (Catfish) - 难度 75, 概率 3% (雨天河流)
execute if score @s sd_fish_region matches 1 if score Global sd_weather matches 1 if score Global sd_rng matches 26..28 run scoreboard players set @s sd_fish_type 41020
execute if score @s sd_fish_type matches 41020 run scoreboard players set @s sd_bite_time 103

# 鲤鱼 (Carp) - 难度 15, 保底鱼 概率 12%
execute if score @s sd_fish_region matches 1 if score Global sd_rng matches 86..97 run scoreboard players set @s sd_fish_type 41070
execute if score @s sd_fish_region matches 2 if score Global sd_rng matches 93..100 run scoreboard players set @s sd_fish_type 41070
execute if score @s sd_fish_region matches 4 if score Global sd_rng matches 93..100 run scoreboard players set @s sd_fish_type 41070
execute if score @s sd_fish_region matches 5 if score Global sd_rng matches 86..93 run scoreboard players set @s sd_fish_type 41070
execute if score @s sd_fish_region matches 6 if score Global sd_rng matches 93..100 run scoreboard players set @s sd_fish_type 41070
execute if score @s sd_fish_type matches 41070 run scoreboard players set @s sd_bite_time 40

# ============================================================
# 地区2(湖泊) - 春季鱼类补充
# ============================================================

# 大口黑鲈 (Largemouth Bass) - 春季湖泊常见鱼
execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 16..30 run scoreboard players set @s sd_fish_type 41100
execute if score @s sd_fish_type matches 41100 run scoreboard players set @s sd_bite_time 84

# 鲤鱼 (Carp) - 湖泊常见鱼
execute if score @s sd_fish_region matches 2 if score Global sd_rng matches 31..50 run scoreboard players set @s sd_fish_type 41070
# bite_time已在河流部分设置

# 鲦鱼 (Chub) - 湖泊常见鱼
execute if score @s sd_fish_region matches 2 if score Global sd_rng matches 51..63 run scoreboard players set @s sd_fish_type 46010
# bite_time已设置

# 太阳鱼 (Sunfish) - 湖泊晴天常见鱼
execute if score @s sd_fish_region matches 2 if score Global sd_weather matches 0 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 64..75 run scoreboard players set @s sd_fish_type 41030
# bite_time已设置

# 鲤科鱼 (Bream) - 湖泊晚间常见鱼
execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 0..1 if score Global sd_rng matches 76..85 run scoreboard players set @s sd_fish_type 46000
execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 5..7 if score Global sd_rng matches 76..85 run scoreboard players set @s sd_fish_type 46000
# bite_time已设置

# 大头鱼 (Bullhead) - 湖泊常见鱼
execute if score @s sd_fish_region matches 2 if score Global sd_rng matches 86..95 run scoreboard players set @s sd_fish_type 41060
execute if score @s sd_fish_type matches 41060 run scoreboard players set @s sd_bite_time 79

# ============================================================
# 地区3(海洋) - 春季鱼类补充
# ============================================================

# 沙丁鱼 (Sardine) - 春季海洋常见鱼 早晚
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0..1 if score Global sd_rng matches 16..28 run scoreboard players set @s sd_fish_type 41090
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 5..7 if score Global sd_rng matches 16..28 run scoreboard players set @s sd_fish_type 41090
execute if score @s sd_fish_type matches 41090 run scoreboard players set @s sd_bite_time 58

# 凤尾鱼 (Anchovy) - 春季海洋常见鱼 全天
execute if score @s sd_fish_region matches 3 if score Global sd_rng matches 29..43 run scoreboard players set @s sd_fish_type 41040
execute if score @s sd_fish_type matches 41040 run scoreboard players set @s sd_bite_time 58

# 鲱鱼 (Herring) - 春季海洋常见鱼
execute if score @s sd_fish_region matches 3 if score Global sd_rng matches 44..57 run scoreboard players set @s sd_fish_type 46030
execute if score @s sd_fish_type matches 46030 run scoreboard players set @s sd_bite_time 65

# 比目鱼 (Flounder) - 春季海洋 早晚
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0..1 if score Global sd_rng matches 65..71 run scoreboard players set @s sd_fish_type 41080
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 5..7 if score Global sd_rng matches 65..71 run scoreboard players set @s sd_fish_type 41080
# bite_time已设置

# 海藻 (Seaweed) - 春季海洋常见
execute if score @s sd_fish_region matches 3 if score Global sd_rng matches 72..90 run scoreboard players set @s sd_fish_type 46020
execute if score @s sd_fish_type matches 46020 run scoreboard players set @s sd_bite_time 65

# 沙丁鱼/凤尾鱼保底
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 91..93 run scoreboard players set @s sd_fish_type 41040

# ============================================================
# 地区9(浅层矿井-25层) - 矿井鱼类 (不分季节)
# ============================================================

# 幽灵鱼 (Ghostfish) - 难度50, 矿井通用鱼 60%
execute if score @s sd_fish_region matches 9 if score Global sd_rng matches 16..75 run scoreboard players set @s sd_fish_type 45010
execute if score @s sd_fish_type matches 45010 run scoreboard players set @s sd_bite_time 95

# 绿藻 (Green Algae) - 难度5, 常见 15%
execute if score @s sd_fish_region matches 9 if score Global sd_rng matches 76..90 run scoreboard players set @s sd_fish_type 46010
execute if score @s sd_fish_type matches 46010 run scoreboard players set @s sd_bite_time 65

# 鲤鱼 (Carp) - 保底 5%
execute if score @s sd_fish_region matches 9 if score Global sd_rng matches 91..95 run scoreboard players set @s sd_fish_type 41070

# ============================================================
# 地区10(中层矿井-50层冰川) - 矿井鱼类 (不分季节)
# ============================================================

# 幽灵鱼 (Ghostfish) - 难度50, 矿井通用鱼 50%
execute if score @s sd_fish_region matches 10 if score Global sd_rng matches 16..65 run scoreboard players set @s sd_fish_type 45010
# bite_time已设置

# 冰晶 (Ice Pip) - 难度85, 冰川层稀有鱼 20%
execute if score @s sd_fish_region matches 10 if score Global sd_rng matches 66..85 run scoreboard players set @s sd_fish_type 45020
execute if score @s sd_fish_type matches 45020 run scoreboard players set @s sd_bite_time 128

# 白藻 (White Algae) - 难度5, 常见 10%
execute if score @s sd_fish_region matches 10 if score Global sd_rng matches 86..95 run scoreboard players set @s sd_fish_type 46020

# ============================================================
# 地区11(深层矿井-75层熔岩) - 矿井鱼类 (不分季节)
# ============================================================

# 岩浆鳗鱼 (Lava Eel) - 难度90, 熔岩层极稀有 15%
execute if score @s sd_fish_region matches 11 if score Global sd_rng matches 16..30 run scoreboard players set @s sd_fish_type 45030
# bite_time已设置

# 石鱼 (Stonefish) - 难度65, 深层罕见鱼 35%
execute if score @s sd_fish_region matches 11 if score Global sd_rng matches 31..65 run scoreboard players set @s sd_fish_type 45040
execute if score @s sd_fish_type matches 45040 run scoreboard players set @s sd_bite_time 105

# 幽灵鱼 (Ghostfish) - 难度50, 矿井通用鱼 30%
execute if score @s sd_fish_region matches 11 if score Global sd_rng matches 66..95 run scoreboard players set @s sd_fish_type 45010

# ============================================================
# 地区12(最深层矿井-100层骷髅洞穴) - 矿井鱼类 (不分季节)
# ============================================================

# 岩浆鳗鱼 (Lava Eel) - 难度90, 最深层常见 35%
execute if score @s sd_fish_region matches 12 if score Global sd_rng matches 16..50 run scoreboard players set @s sd_fish_type 45030
# bite_time已设置

# 石鱼 (Stonefish) - 难度65, 深层罕见鱼 30%
execute if score @s sd_fish_region matches 12 if score Global sd_rng matches 51..80 run scoreboard players set @s sd_fish_type 45040
# bite_time已设置

# 幽灵鱼 (Ghostfish) - 难度50, 矿井通用鱼 15%
execute if score @s sd_fish_region matches 12 if score Global sd_rng matches 81..95 run scoreboard players set @s sd_fish_type 45010
