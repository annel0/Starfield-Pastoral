# data/stardew/functions/fishing/select_fish_winter.mcfunction
# Winter季钓鱼选择逻辑
# 共 28 种鱼

# 概率分配:
# - 垃圾: 15% (RNG 1-15)
# - 传说鱼: 1种 × 2% = 2%
# - 极稀有鱼(难度90+): 2种 × 3% = 6%
# - 稀有鱼(难度70-89): 6种 × 5% = 30%
# - 罕见鱼(难度50-69): 11种 × 7% = 77%
# - 常见鱼(难度<50): 8种共 -30%

# 重置鱼类型
scoreboard players set @s sd_fish_type 0

# 垃圾 (15% 概率)
execute if score @s sd_fish_region matches 1..12 if score Global sd_rng matches 1..15 run function stardew:fishing/select_trash

# ============================================================
# 传说鱼
# ============================================================

# 冰川鱼 (Glacierfish) - 难度 100, 概率 2%
execute if score @s sd_fish_region matches 7 if score Global sd_rng matches 16..17 run scoreboard players set @s sd_fish_type 44990
execute if score @s sd_fish_type matches 44990 run scoreboard players set @s sd_bite_time 147

# ============================================================
# 地区1(河流) 补充常见鱼 - 填补空白区域
# ============================================================

# 鲈鱼 (Perch) - 冬季河流常见鱼 (填补RNG 16-33)
execute if score @s sd_fish_region matches 1 if score Global sd_rng matches 16..30 run scoreboard players set @s sd_fish_type 41050
execute if score @s sd_fish_type matches 41050 run scoreboard players set @s sd_bite_time 65

# 鲦鱼 (Chub) - 冬季河流常见鱼 (填补RNG 16-33)
execute if score @s sd_fish_region matches 1 if score Global sd_rng matches 31..33 run scoreboard players set @s sd_fish_type 46010
execute if score @s sd_fish_type matches 46010 run scoreboard players set @s sd_bite_time 65

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

# 鲟鱼 (Sturgeon) - 难度 78, 概率 5%
execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 29..33 run scoreboard players set @s sd_fish_type 42050
execute if score @s sd_fish_type matches 42050 run scoreboard players set @s sd_bite_time 119

# 蛇齿单线鱼 (Lingcod) - 难度 85, 概率 5%
execute if score @s sd_fish_region matches 1..2 if score Global sd_rng matches 34..38 run scoreboard players set @s sd_fish_type 44030
execute if score @s sd_fish_type matches 44030 run scoreboard players set @s sd_bite_time 128

# 鲤科鱼 (Bream) - 冬季河流晚间常见鱼 (填补RNG 39-60)
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 0..1 if score Global sd_rng matches 39..53 run scoreboard players set @s sd_fish_type 46000
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 5..7 if score Global sd_rng matches 39..53 run scoreboard players set @s sd_fish_type 46000
execute if score @s sd_fish_type matches 46000 run scoreboard players set @s sd_bite_time 65

# 小口黑鲈 (Smallmouth Bass) - 冬季河流常见鱼 (填补RNG 39-60)
execute if score @s sd_fish_region matches 1 if score Global sd_rng matches 54..60 run scoreboard players set @s sd_fish_type 41000
execute if score @s sd_fish_type matches 41000 run scoreboard players set @s sd_bite_time 56

# 金枪鱼 (Tuna) - 难度 70, 概率 5%
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 39..43 run scoreboard players set @s sd_fish_type 42040
execute if score @s sd_fish_type matches 42040 run scoreboard players set @s sd_bite_time 109

# 海鳗 (Sea Eel) - 难度 80, 概率 5%
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0 if score Global sd_rng matches 44..48 run scoreboard players set @s sd_fish_type 43070
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 1 if score Global sd_rng matches 44..48 run scoreboard players set @s sd_fish_type 43070
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 5 if score Global sd_rng matches 44..48 run scoreboard players set @s sd_fish_type 43070
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 6 if score Global sd_rng matches 44..48 run scoreboard players set @s sd_fish_type 43070
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 7 if score Global sd_rng matches 44..48 run scoreboard players set @s sd_fish_type 43070
execute if score @s sd_fish_type matches 43070 run scoreboard players set @s sd_bite_time 122

# 乌贼 (Squid) - 难度 75, 概率 5%
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0 if score Global sd_rng matches 49..53 run scoreboard players set @s sd_fish_type 44010
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 1 if score Global sd_rng matches 49..53 run scoreboard players set @s sd_fish_type 44010
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 5 if score Global sd_rng matches 49..53 run scoreboard players set @s sd_fish_type 44010
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 6 if score Global sd_rng matches 49..53 run scoreboard players set @s sd_fish_type 44010
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 7 if score Global sd_rng matches 49..53 run scoreboard players set @s sd_fish_type 44010
execute if score @s sd_fish_type matches 44010 run scoreboard players set @s sd_bite_time 115

# ============================================================
# 罕见鱼
# ============================================================

# 石鱼 (Stonefish) - 难度 65, 概率 7%
execute if score @s sd_fish_region matches 11 if score Global sd_rng matches 54..60 run scoreboard players set @s sd_fish_type 45010
execute if score @s sd_fish_type matches 45010 run scoreboard players set @s sd_bite_time 103

# 虎纹鳟鱼 (Tiger Trout) - 难度 60, 概率 7%
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 61..67 run scoreboard players set @s sd_fish_type 43010
execute if score @s sd_fish_type matches 43010 run scoreboard players set @s sd_bite_time 96

# 西鲱 (Shad) - 冬季河流 雨天 (填补RNG 68-74)
execute if score @s sd_fish_region matches 1 if score Global sd_weather matches 1 if score Global sd_rng matches 68..74 run scoreboard players set @s sd_fish_type 41010
execute if score @s sd_fish_type matches 41010 run scoreboard players set @s sd_bite_time 77

# 午夜鲤鱼 (Midnight Carp) - 难度 55, 概率 7%
execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 0 if score Global sd_rng matches 68..74 run scoreboard players set @s sd_fish_type 43060
execute if score @s sd_fish_region matches 5 if score @s sd_time_slot matches 0 if score Global sd_rng matches 68..74 run scoreboard players set @s sd_fish_type 43060
execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 1 if score Global sd_rng matches 68..74 run scoreboard players set @s sd_fish_type 43060
execute if score @s sd_fish_region matches 5 if score @s sd_time_slot matches 1 if score Global sd_rng matches 68..74 run scoreboard players set @s sd_fish_type 43060
execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 7 if score Global sd_rng matches 68..74 run scoreboard players set @s sd_fish_type 43060
execute if score @s sd_fish_region matches 5 if score @s sd_time_slot matches 7 if score Global sd_rng matches 68..74 run scoreboard players set @s sd_fish_type 43060
execute if score @s sd_fish_type matches 43060 run scoreboard players set @s sd_bite_time 90

# 狗鱼 (Pike) - 难度 60, 概率 7%
execute if score @s sd_fish_region matches 1 if score Global sd_rng matches 75..81 run scoreboard players set @s sd_fish_type 42030
execute if score @s sd_fish_region matches 5 if score Global sd_rng matches 75..81 run scoreboard players set @s sd_fish_type 42030
execute if score @s sd_fish_type matches 42030 run scoreboard players set @s sd_bite_time 96

# 鲤鱼 (Carp) - 冬季河流保底鱼 (填补RNG 82-93)
execute if score @s sd_fish_region matches 1 if score Global sd_rng matches 82..90 run scoreboard players set @s sd_fish_type 41070
execute if score @s sd_fish_type matches 41070 run scoreboard players set @s sd_bite_time 40

# 大口黑鲈 (Largemouth Bass) - 难度 50, 概率 7%
execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 82..88 run scoreboard players set @s sd_fish_type 43020
execute if score @s sd_fish_type matches 43020 run scoreboard players set @s sd_bite_time 84

# 大比目鱼 (Halibut) - 难度 50, 概率 7%
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0 if score Global sd_rng matches 89..95 run scoreboard players set @s sd_fish_type 41080
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 1 if score Global sd_rng matches 89..95 run scoreboard players set @s sd_fish_type 41080
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 2 if score Global sd_rng matches 89..95 run scoreboard players set @s sd_fish_type 41080
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 3 if score Global sd_rng matches 89..95 run scoreboard players set @s sd_fish_type 41080
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 6 if score Global sd_rng matches 89..95 run scoreboard players set @s sd_fish_type 41080
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 7 if score Global sd_rng matches 89..95 run scoreboard players set @s sd_fish_type 41080
execute if score @s sd_fish_type matches 41080 run scoreboard players set @s sd_bite_time 84

# 红鲻鱼 (Red Mullet) - 难度 55, 概率 5%
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 89..93 run scoreboard players set @s sd_fish_type 42020
execute if score @s sd_fish_type matches 42020 run scoreboard players set @s sd_bite_time 90

# 长鳍金枪鱼 (Albacore) - 难度 60, 概率 3% (仅特定时间)
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0..3 if score Global sd_rng matches 91..93 run scoreboard players set @s sd_fish_type 44020
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 5..7 if score Global sd_rng matches 91..93 run scoreboard players set @s sd_fish_type 44020
execute if score @s sd_fish_type matches 44020 run scoreboard players set @s sd_bite_time 96

# 沙鱼 (Sandfish) - 难度 65, 概率 3% (特殊区域12)
execute if score @s sd_fish_region matches 12 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 91..93 run scoreboard players set @s sd_fish_type 45040
execute if score @s sd_fish_type matches 45040 run scoreboard players set @s sd_bite_time 103

# 木跃鱼 (Woodskip) - 难度 50, 概率 3% (特殊区域4)
execute if score @s sd_fish_region matches 4 if score Global sd_rng matches 91..93 run scoreboard players set @s sd_fish_type 46040
execute if score @s sd_fish_type matches 46040 run scoreboard players set @s sd_bite_time 84

# 幽灵鱼 (Ghostfish) - 难度 50, 概率 3% (特殊区域11)
execute if score @s sd_fish_region matches 11 if score Global sd_rng matches 91..93 run scoreboard players set @s sd_fish_type 45000
execute if score @s sd_fish_type matches 45000 run scoreboard players set @s sd_bite_time 84

# ============================================================
# 地区2(湖泊) - 冬季鱼类补充
# ============================================================

# 狗鱼 (Pike) - 冬季湖泊常见鱼
execute if score @s sd_fish_region matches 2 if score Global sd_rng matches 16..30 run scoreboard players set @s sd_fish_type 42030
execute if score @s sd_fish_type matches 42030 run scoreboard players set @s sd_bite_time 96

# 鲟鱼 (Sturgeon) - 冬季湖泊 白天
execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 31..45 run scoreboard players set @s sd_fish_type 42050
execute if score @s sd_fish_type matches 42050 run scoreboard players set @s sd_bite_time 134

# 鲈鱼 (Perch) - 冬季湖泊常见鱼
execute if score @s sd_fish_region matches 2 if score Global sd_rng matches 46..60 run scoreboard players set @s sd_fish_type 41050
execute if score @s sd_fish_type matches 41050 run scoreboard players set @s sd_bite_time 65

# 鲤鱼 (Carp) - 湖泊常见鱼
execute if score @s sd_fish_region matches 2 if score Global sd_rng matches 61..75 run scoreboard players set @s sd_fish_type 41070
execute if score @s sd_fish_type matches 41070 run scoreboard players set @s sd_bite_time 40

# 鲤科鱼 (Bream) - 湖泊晚间常见鱼
execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 0..1 if score Global sd_rng matches 76..88 run scoreboard players set @s sd_fish_type 46000
execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 5..7 if score Global sd_rng matches 76..88 run scoreboard players set @s sd_fish_type 46000
execute if score @s sd_fish_type matches 46000 run scoreboard players set @s sd_bite_time 65

# ============================================================
# 地区3(海洋) - 冬季鱼类补充
# ============================================================

# 金枪鱼 (Tuna) - 冬季海洋 早晚
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0..1 if score Global sd_rng matches 16..28 run scoreboard players set @s sd_fish_type 43070
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 5..7 if score Global sd_rng matches 16..28 run scoreboard players set @s sd_fish_type 43070
execute if score @s sd_fish_type matches 43070 run scoreboard players set @s sd_bite_time 122

# 沙丁鱼 (Sardine) - 冬季海洋 早晚
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0..1 if score Global sd_rng matches 29..38 run scoreboard players set @s sd_fish_type 41090
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 5..7 if score Global sd_rng matches 29..38 run scoreboard players set @s sd_fish_type 41090
execute if score @s sd_fish_type matches 41090 run scoreboard players set @s sd_bite_time 58

# 鲱鱼 (Herring) - 冬季海洋常见鱼
execute if score @s sd_fish_region matches 3 if score Global sd_rng matches 54..68 run scoreboard players set @s sd_fish_type 46030
execute if score @s sd_fish_type matches 46030 run scoreboard players set @s sd_bite_time 65

# 罗非鱼 (Tilapia) - 冬季海洋常见鱼
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 69..78 run scoreboard players set @s sd_fish_type 42040
execute if score @s sd_fish_type matches 42040 run scoreboard players set @s sd_bite_time 109

# 鱿鱼 (Squid) - 冬季海洋 晚上
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0..1 if score Global sd_rng matches 79..88 run scoreboard players set @s sd_fish_type 44110
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 5..7 if score Global sd_rng matches 79..88 run scoreboard players set @s sd_fish_type 44110
execute if score @s sd_fish_type matches 44110 run scoreboard players set @s sd_bite_time 115

# 海藻 (Seaweed) - 冬季海洋保底
execute if score @s sd_fish_region matches 3 if score Global sd_rng matches 89..96 run scoreboard players set @s sd_fish_type 46020
execute if score @s sd_fish_type matches 46020 run scoreboard players set @s sd_bite_time 65

# ============================================================
# 常见鱼 (保底鱼, 在普通区域 94-100)
# ============================================================

# 大眼鲈鱼 (Walleye) - 难度 45 (雨天晚间)
execute if score @s sd_fish_region matches 1..2 if score @s sd_time_slot matches 0..1 if score Global sd_weather matches 1 if score Global sd_rng matches 94..96 run scoreboard players set @s sd_fish_type 43050
execute if score @s sd_fish_region matches 5 if score @s sd_time_slot matches 0..1 if score Global sd_weather matches 1 if score Global sd_rng matches 94..96 run scoreboard players set @s sd_fish_type 43050
execute if score @s sd_fish_region matches 1..2 if score @s sd_time_slot matches 4..7 if score Global sd_weather matches 1 if score Global sd_rng matches 94..96 run scoreboard players set @s sd_fish_type 43050
execute if score @s sd_fish_region matches 5 if score @s sd_time_slot matches 4..7 if score Global sd_weather matches 1 if score Global sd_rng matches 94..96 run scoreboard players set @s sd_fish_type 43050
execute if score @s sd_fish_type matches 43050 run scoreboard players set @s sd_bite_time 77

# 大头鱼 (Bullhead) - 难度 46
execute if score @s sd_fish_region matches 2 if score Global sd_rng matches 94..96 run scoreboard players set @s sd_fish_type 41060
execute if score @s sd_fish_type matches 41060 run scoreboard players set @s sd_bite_time 79

# 海参 (Sea Cucumber) - 难度 40
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 94..96 run scoreboard players set @s sd_fish_type 43040
execute if score @s sd_fish_type matches 43040 run scoreboard players set @s sd_bite_time 71

# 鲈鱼 (Perch) - 难度 35
execute if score @s sd_fish_region matches 1 if score Global sd_rng matches 97..98 run scoreboard players set @s sd_fish_type 44000
execute if score @s sd_fish_region matches 2 if score Global sd_rng matches 97..98 run scoreboard players set @s sd_fish_type 44000
execute if score @s sd_fish_region matches 5 if score Global sd_rng matches 97..98 run scoreboard players set @s sd_fish_type 44000
execute if score @s sd_fish_type matches 44000 run scoreboard players set @s sd_bite_time 65

# 鲦鱼 (Chub) - 难度 35
execute if score @s sd_fish_region matches 1..2 if score Global sd_rng matches 99..100 run scoreboard players set @s sd_fish_type 46010
execute if score @s sd_fish_type matches 46010 run scoreboard players set @s sd_bite_time 65

# 鲤科鱼 (Bream) - 难度 35 (晚间)
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 0..1 if score Global sd_rng matches 99..100 run scoreboard players set @s sd_fish_type 46000
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 5..7 if score Global sd_rng matches 99..100 run scoreboard players set @s sd_fish_type 46000
execute if score @s sd_fish_type matches 46000 run scoreboard players set @s sd_bite_time 65

# 沙丁鱼 (Sardine) - 难度 30
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 99..100 run scoreboard players set @s sd_fish_type 41050
execute if score @s sd_fish_type matches 41050 run scoreboard players set @s sd_bite_time 58

# 鲤鱼 (Carp) - 难度 15 (保底)
execute if score @s sd_fish_region matches 2 if score Global sd_rng matches 99..100 run scoreboard players set @s sd_fish_type 41070
execute if score @s sd_fish_region matches 4 if score Global sd_rng matches 94..100 run scoreboard players set @s sd_fish_type 41070
execute if score @s sd_fish_region matches 6 if score Global sd_rng matches 94..100 run scoreboard players set @s sd_fish_type 41070
execute if score @s sd_fish_type matches 41070 run scoreboard players set @s sd_bite_time 40

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
execute if score @s sd_fish_type matches 45030 run scoreboard players set @s sd_bite_time 134

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
