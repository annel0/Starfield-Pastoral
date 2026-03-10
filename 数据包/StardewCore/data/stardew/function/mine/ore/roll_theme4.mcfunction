# stardew:mine/ore/roll_theme4.mcfunction
# 主题4 概率表 - 第76-100层
# 执行位置: 要生成矿石的位置
#
# 概率分布:
# 石头(7204):50%   (1-50)     - theme4专用石头
# 煤炭:      7%    (51-57)    
# 铜矿:      2%    (58-59)    
# 铁矿:      10%   (60-69)    
# 金矿:      12%   (70-81)    
# 钻石(7224):8%    (82-89)    - 新增！最高级矿石
# 石英:      2%    (90-91)    
# 地晶:      2%    (92-93)    
# 翡翠:      2%    (94-95)    
# 紫水晶:    2%    (96-97)    
# 红宝石:    2%    (98-99)    
# 五彩碎片(7231): 0.2% (100, 1-20) - 极为罕见！
# (预留):    0.8%  (100, 21-100) -> 红宝石

# 随机 1-100
execute store result score #ore_roll sd_mine_temp run random value 1..100

# 石头 50% (theme4专用，CMD 7204)
execute if score #ore_roll sd_mine_temp matches 1..50 run function stardew:mine/ore/spawn_stone_theme4

# 煤炭 7%
execute if score #ore_roll sd_mine_temp matches 51..57 run function stardew:mine/ore/spawn_coal

# 铜矿 2%
execute if score #ore_roll sd_mine_temp matches 58..59 run function stardew:mine/ore/spawn_copper

# 铁矿 10%
execute if score #ore_roll sd_mine_temp matches 60..69 run function stardew:mine/ore/spawn_iron

# 金矿 12%
execute if score #ore_roll sd_mine_temp matches 70..81 run function stardew:mine/ore/spawn_gold

# 钻石 8% (CMD 7224)
execute if score #ore_roll sd_mine_temp matches 82..89 run function stardew:mine/ore/spawn_diamond

# 石英 2%
execute if score #ore_roll sd_mine_temp matches 90..91 run function stardew:mine/ore/spawn_quartz

# 地晶 2%
execute if score #ore_roll sd_mine_temp matches 92..93 run function stardew:mine/ore/spawn_earth_crystal

# 翡翠 2%
execute if score #ore_roll sd_mine_temp matches 94..95 run function stardew:mine/ore/spawn_emerald

# 紫水晶 2%
execute if score #ore_roll sd_mine_temp matches 96..97 run function stardew:mine/ore/spawn_amethyst

# 红宝石 2%
execute if score #ore_roll sd_mine_temp matches 98..99 run function stardew:mine/ore/spawn_ruby

# 100的话再随机判断是五彩碎片还是红宝石
execute if score #ore_roll sd_mine_temp matches 100 store result score #rare_roll sd_mine_temp run random value 1..100
execute if score #ore_roll sd_mine_temp matches 100 if score #rare_roll sd_mine_temp matches 1..20 run function stardew:mine/ore/spawn_prismatic_shard
execute if score #ore_roll sd_mine_temp matches 100 if score #rare_roll sd_mine_temp matches 21..100 run function stardew:mine/ore/spawn_ruby
