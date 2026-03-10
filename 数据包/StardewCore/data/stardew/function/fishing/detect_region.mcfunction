# data/stardew/functions/fishing/detect_region.mcfunction
# 执行者：玩家 (@s)

# 0. 默认认为是河流区域（修复：如果没有 marker 就默认为河流）
scoreboard players set @s sd_fish_region 1

# 1. 如果附近 32 格内有任意 fish_region，就按优先级判定
#    优先级: 特殊区域 > 森林河流 > 下水道 > 森林池塘 > 神秘森林 > 海洋 > 湖泊 > 河流

# 河流 (优先级最低，最先判定)
execute if entity @e[type=marker,tag=fish_river,distance=..32,limit=1] run scoreboard players set @s sd_fish_region 1

# 湖泊
execute if entity @e[type=marker,tag=fish_lake,distance=..32,limit=1] run scoreboard players set @s sd_fish_region 2

# 海洋
execute if entity @e[type=marker,tag=fish_sea,distance=..32,limit=1] run scoreboard players set @s sd_fish_region 3

# 神秘森林 (Secret Woods)
execute if entity @e[type=marker,tag=fish_secret_woods,distance=..32,limit=1] run scoreboard players set @s sd_fish_region 4

# 森林池塘 (Forest Pond)
execute if entity @e[type=marker,tag=fish_forest_pond,distance=..32,limit=1] run scoreboard players set @s sd_fish_region 5

# 下水道 (Sewer)
execute if entity @e[type=marker,tag=fish_sewer,distance=..32,limit=1] run scoreboard players set @s sd_fish_region 6

# 森林河流 (Forest River) - 专门给传说鱼Glacierfish用
execute if entity @e[type=marker,tag=fish_forest_river,distance=..32,limit=1] run scoreboard players set @s sd_fish_region 7

# 矿井钓鱼区 - 根据不同主题判断深度
# Theme1 (1-25层) → 地区9 (浅层矿井)
execute if entity @e[type=marker,tag=sd_fishing_spot,tag=sd_mine_theme1,distance=..32,limit=1] run scoreboard players set @s sd_fish_region 9

# Theme2 (26-50层) → 地区10 (中层矿井-冰川)
execute if entity @e[type=marker,tag=sd_fishing_spot,tag=sd_mine_theme2,distance=..32,limit=1] run scoreboard players set @s sd_fish_region 10

# Theme3 (51-75层) → 地区11 (深层矿井-熔岩)
execute if entity @e[type=marker,tag=sd_fishing_spot,tag=sd_mine_theme3,distance=..32,limit=1] run scoreboard players set @s sd_fish_region 11

# Theme4 (76-100层) → 地区12 (最深层矿井-骷髅洞穴)
execute if entity @e[type=marker,tag=sd_fishing_spot,tag=sd_mine_theme4,distance=..32,limit=1] run scoreboard players set @s sd_fish_region 12

# 特殊区域 (优先级最高) - 传说鱼专属区域
# 比如 Legend 的特定钓点, Crimsonfish 的海洋东侧等
execute if entity @e[type=marker,tag=fish_special_legend,distance=..16,limit=1] run scoreboard players set @s sd_fish_region 8
execute if entity @e[type=marker,tag=fish_special_crimson,distance=..16,limit=1] run scoreboard players set @s sd_fish_region 9
execute if entity @e[type=marker,tag=fish_special_angler,distance=..16,limit=1] run scoreboard players set @s sd_fish_region 10
