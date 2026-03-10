# data/stardew/functions/fishing/select_fish_summer.mcfunction
# 夏季钓鱼选择 - 星露谷物语原版概率

scoreboard players set @s sd_fish_type 0

# 垃圾 (15%)
execute if score Global sd_rng matches 1..15 run return run function stardew:fishing/select_trash

# S级鱼: 河豚, 鲟鱼, 超级海参 (19.5%)
execute if score @s sd_fish_region matches 3 if score Global sd_weather matches 0 if score @s sd_time_slot matches 3..4 if score Global sd_rng matches 16..19 run scoreboard players set @s sd_fish_type 42060
execute if score @s sd_fish_type matches 42060 run scoreboard players set @s sd_bite_time 122

execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 0..6 if score Global sd_rng matches 20..25 run scoreboard players set @s sd_fish_type 42050
execute if score @s sd_fish_type matches 42050 run scoreboard players set @s sd_bite_time 134

execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 6..7 if score Global sd_rng matches 16..20 run scoreboard players set @s sd_fish_type 42080
execute if score @s sd_fish_type matches 42080 run scoreboard players set @s sd_bite_time 134

# A级鱼: 章鱼, 金枪鱼, 鲯鳅, 狗鱼 (15%)
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0..3 if score Global sd_rng matches 21..25 run scoreboard players set @s sd_fish_type 42070
execute if score @s sd_fish_type matches 42070 run scoreboard players set @s sd_bite_time 122

execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0..6 if score Global sd_rng matches 26..31 run scoreboard players set @s sd_fish_type 42040
execute if score @s sd_fish_type matches 42040 run scoreboard players set @s sd_bite_time 109

execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 0..6 if score Global sd_rng matches 16..20 run scoreboard players set @s sd_fish_type 42090
execute if score @s sd_fish_type matches 42090 run scoreboard players set @s sd_bite_time 103

execute if score @s sd_fish_region matches 1 if score Global sd_rng matches 21..27 run scoreboard players set @s sd_fish_type 42030
execute if score @s sd_fish_region matches 6 if score Global sd_rng matches 16..27 run scoreboard players set @s sd_fish_type 42030
execute if score @s sd_fish_type matches 42030 run scoreboard players set @s sd_bite_time 109

# 小口黑鲈 (Smallmouth Bass) - 难度 28, 夏季河流常见鱼
execute if score @s sd_fish_region matches 1 if score Global sd_rng matches 28..37 run scoreboard players set @s sd_fish_type 41000
execute if score @s sd_fish_type matches 41000 run scoreboard players set @s sd_bite_time 56

# 鲦鱼 (Chub) - 难度 35, 夏季河流常见鱼
execute if score @s sd_fish_region matches 1 if score Global sd_rng matches 38..47 run scoreboard players set @s sd_fish_type 46010
execute if score @s sd_fish_type matches 46010 run scoreboard players set @s sd_bite_time 65

# 鲈鱼 (Perch) - 难度 35, 夏季河流常见鱼
execute if score @s sd_fish_region matches 1 if score Global sd_rng matches 48..57 run scoreboard players set @s sd_fish_type 41050
execute if score @s sd_fish_type matches 41050 run scoreboard players set @s sd_bite_time 65

# 鲤科鱼 (Bream) - 难度 35, 晚间更常见
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 0..1 if score Global sd_rng matches 58..65 run scoreboard players set @s sd_fish_type 46000
execute if score @s sd_fish_region matches 1 if score @s sd_time_slot matches 5..7 if score Global sd_rng matches 58..65 run scoreboard players set @s sd_fish_type 46000
execute if score @s sd_fish_type matches 46000 run scoreboard players set @s sd_bite_time 65

# B级鱼: 虹鳟鱼, 罗非鱼, 红鲻鱼 (30%)
execute if score @s sd_fish_region matches 1..2 if score Global sd_weather matches 0 if score @s sd_time_slot matches 0..6 if score Global sd_rng matches 32..41 run scoreboard players set @s sd_fish_type 42000
execute if score @s sd_fish_type matches 42000 run scoreboard players set @s sd_bite_time 84

execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0..4 if score Global sd_rng matches 42..51 run scoreboard players set @s sd_fish_type 42010
execute if score @s sd_fish_type matches 42010 run scoreboard players set @s sd_bite_time 84

execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0..6 if score Global sd_rng matches 52..65 run scoreboard players set @s sd_fish_type 42020
execute if score @s sd_fish_type matches 42020 run scoreboard players set @s sd_bite_time 77

# C级鱼: 太阳鱼, 鲤鱼 (35%)
execute if score @s sd_fish_region matches 1..2 if score Global sd_weather matches 0 if score @s sd_time_slot matches 0..6 if score Global sd_rng matches 66..77 run scoreboard players set @s sd_fish_type 41030
execute if score @s sd_fish_type matches 41030 run scoreboard players set @s sd_bite_time 58

execute if score @s sd_fish_region matches 1..2 if score Global sd_rng matches 78..100 run scoreboard players set @s sd_fish_type 41070
execute if score @s sd_fish_region matches 6..7 if score Global sd_rng matches 28..100 run scoreboard players set @s sd_fish_type 41070
execute if score @s sd_fish_type matches 41070 run scoreboard players set @s sd_bite_time 40

# ============================================================
# 地区2(湖泊) - 夏季鱼类补充
# ============================================================

# 大口黑鲈 (Largemouth Bass) - 夏季湖泊常见鱼
execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 16..28 run scoreboard players set @s sd_fish_type 41100
execute if score @s sd_fish_type matches 41100 run scoreboard players set @s sd_bite_time 84

# 鲟鱼 (Sturgeon) - 夏季湖泊 白天
execute if score @s sd_fish_region matches 2 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 29..43 run scoreboard players set @s sd_fish_type 42050
execute if score @s sd_fish_type matches 42050 run scoreboard players set @s sd_bite_time 134

# 鲦鱼 (Chub) - 湖泊常见鱼
execute if score @s sd_fish_region matches 2 if score Global sd_rng matches 44..58 run scoreboard players set @s sd_fish_type 46010
execute if score @s sd_fish_type matches 46010 run scoreboard players set @s sd_bite_time 65

# 大头鱼 (Bullhead) - 湖泊常见鱼
execute if score @s sd_fish_region matches 2 if score Global sd_rng matches 59..73 run scoreboard players set @s sd_fish_type 41060
execute if score @s sd_fish_type matches 41060 run scoreboard players set @s sd_bite_time 79

# 彩虹鳟鱼 (Rainbow Trout) - 湖泊 晴天白天
execute if score @s sd_fish_region matches 2 if score Global sd_weather matches 0 if score @s sd_time_slot matches 2..6 if score Global sd_rng matches 74..88 run scoreboard players set @s sd_fish_type 42000
execute if score @s sd_fish_type matches 42000 run scoreboard players set @s sd_bite_time 84

# 鲤鱼 (Carp) - 保底 (已在前面设置 RNG 78-100)

# ============================================================
# 地区3(海洋) - 夏季鱼类补充 (RNG 32-41空白需填补)
# ============================================================

# 沙丁鱼 (Sardine) - 夏季海洋 早晚
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 0..1 if score Global sd_rng matches 32..41 run scoreboard players set @s sd_fish_type 41090
execute if score @s sd_fish_region matches 3 if score @s sd_time_slot matches 5..7 if score Global sd_rng matches 32..41 run scoreboard players set @s sd_fish_type 41090
execute if score @s sd_fish_type matches 41090 run scoreboard players set @s sd_bite_time 58

execute if score @s sd_fish_region matches 3 if score Global sd_rng matches 66..100 run scoreboard players set @s sd_fish_type 42020
execute if score @s sd_fish_type matches 42020 unless score @s sd_bite_time matches 1.. run scoreboard players set @s sd_bite_time 77

# 特殊地点
execute if score @s sd_fish_region matches 4 if score Global sd_rng matches 16..40 run scoreboard players set @s sd_fish_type 45000
execute if score @s sd_fish_type matches 45000 run scoreboard players set @s sd_bite_time 84

execute if score @s sd_fish_region matches 4 if score Global sd_rng matches 41..55 run scoreboard players set @s sd_fish_type 45010
execute if score @s sd_fish_type matches 45010 run scoreboard players set @s sd_bite_time 103

execute if score @s sd_fish_region matches 4 if score Global sd_rng matches 56..65 run scoreboard players set @s sd_fish_type 45020
execute if score @s sd_fish_type matches 45020 run scoreboard players set @s sd_bite_time 128

execute if score @s sd_fish_region matches 4 if score Global sd_rng matches 66..75 run scoreboard players set @s sd_fish_type 45030
execute if score @s sd_fish_type matches 45030 run scoreboard players set @s sd_bite_time 134

execute if score @s sd_fish_region matches 5 if score Global sd_rng matches 16..50 run scoreboard players set @s sd_fish_type 45040
execute if score @s sd_fish_type matches 45040 run scoreboard players set @s sd_bite_time 103

execute if score @s sd_fish_region matches 5 if score Global sd_rng matches 51..70 run scoreboard players set @s sd_fish_type 45050
execute if score @s sd_fish_type matches 45050 run scoreboard players set @s sd_bite_time 134

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

# 保底 - 如果没命中任何鱼，给垃圾
execute if score @s sd_fish_type matches 0 run function stardew:fishing/select_trash
