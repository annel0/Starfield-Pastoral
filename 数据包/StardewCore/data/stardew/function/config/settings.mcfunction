# data/stardew/functions/config/settings.mcfunction
# 在这里统一管理所有数值！
# 包含：作物周期、作物价格、树木价格、所有季节鱼类价格

# ==========================================
# 1. 作物生长周期配置
# ==========================================
scoreboard players set #wheat_max sd_config 8
scoreboard players set #tomato_max sd_config 10
scoreboard players set #strawberry_max sd_config 7
scoreboard players set #garlic_max sd_config 12
scoreboard players set #tree_max sd_config 28

# ==========================================
# 2. 作物价格配置
# ==========================================
# 小麦
data modify storage stardew:config Prices.Wheat.Base set value 40
data modify storage stardew:config Prices.Wheat.Silver set value 50
data modify storage stardew:config Prices.Wheat.Gold set value 60
data modify storage stardew:config Prices.Wheat.Diamond set value 80

# 番茄
data modify storage stardew:config Prices.Tomato.Base set value 50
data modify storage stardew:config Prices.Tomato.Silver set value 63
data modify storage stardew:config Prices.Tomato.Gold set value 75
data modify storage stardew:config Prices.Tomato.Diamond set value 100

# 草莓
data modify storage stardew:config Prices.Strawberry.Base set value 30
data modify storage stardew:config Prices.Strawberry.Silver set value 38
data modify storage stardew:config Prices.Strawberry.Gold set value 45
data modify storage stardew:config Prices.Strawberry.Diamond set value 60

# 大蒜
data modify storage stardew:config Prices.Garlic.Base set value 60
data modify storage stardew:config Prices.Garlic.Silver set value 75
data modify storage stardew:config Prices.Garlic.Gold set value 90
data modify storage stardew:config Prices.Garlic.Diamond set value 120

# ==========================================
# 3. 林业资源配置
# ==========================================
data modify storage stardew:config Prices.Wood.Base set value 2
data modify storage stardew:config Prices.Hardwood.Base set value 15

data modify storage stardew:config Prices.Seed.Oak set value 5
data modify storage stardew:config Prices.Seed.Maple set value 5
data modify storage stardew:config Prices.Seed.Pine set value 5
data modify storage stardew:config Prices.Seed.Mahogany set value 5

# ==========================================
# 4. 渔业价格配置 (Complete)
# 倍率: Silver(1.25), Gold(1.5), Diamond(2.0)
# ==========================================

# --- 垃圾 & 资源 ---
data modify storage stardew:config Prices.Trash.Base set value 0
data modify storage stardew:config Prices.Resource.Algae.Base set value 15
data modify storage stardew:config Prices.Resource.Seaweed.Base set value 20
data modify storage stardew:config Prices.Resource.Pearl.Base set value 2000

# --- 🌸 春季鱼类 (Spring) ---

# 小嘴鲈鱼 (50)
data modify storage stardew:config Prices.Fish.Smallmouth.Base set value 50
data modify storage stardew:config Prices.Fish.Smallmouth.Silver set value 62
data modify storage stardew:config Prices.Fish.Smallmouth.Gold set value 75
data modify storage stardew:config Prices.Fish.Smallmouth.Diamond set value 100

# 西鲱 (90)
data modify storage stardew:config Prices.Fish.Shad.Base set value 90
data modify storage stardew:config Prices.Fish.Shad.Silver set value 112
data modify storage stardew:config Prices.Fish.Shad.Gold set value 135
data modify storage stardew:config Prices.Fish.Shad.Diamond set value 180

# 鲶鱼 (300 - BOSS)
data modify storage stardew:config Prices.Fish.Catfish.Base set value 300
data modify storage stardew:config Prices.Fish.Catfish.Silver set value 375
data modify storage stardew:config Prices.Fish.Catfish.Gold set value 450
data modify storage stardew:config Prices.Fish.Catfish.Diamond set value 600

# 太阳鱼 (45)
data modify storage stardew:config Prices.Fish.Sunfish.Base set value 45
data modify storage stardew:config Prices.Fish.Sunfish.Silver set value 56
data modify storage stardew:config Prices.Fish.Sunfish.Gold set value 67
data modify storage stardew:config Prices.Fish.Sunfish.Diamond set value 90

# 鳀鱼 (30)
data modify storage stardew:config Prices.Fish.Anchovy.Base set value 30
data modify storage stardew:config Prices.Fish.Anchovy.Silver set value 37
data modify storage stardew:config Prices.Fish.Anchovy.Gold set value 45
data modify storage stardew:config Prices.Fish.Anchovy.Diamond set value 60

# 沙丁鱼 (40)
data modify storage stardew:config Prices.Fish.Sardine.Base set value 40
data modify storage stardew:config Prices.Fish.Sardine.Silver set value 50
data modify storage stardew:config Prices.Fish.Sardine.Gold set value 60
data modify storage stardew:config Prices.Fish.Sardine.Diamond set value 80

# 大头鱼 (75)
data modify storage stardew:config Prices.Fish.Bullhead.Base set value 75
data modify storage stardew:config Prices.Fish.Bullhead.Silver set value 93
data modify storage stardew:config Prices.Fish.Bullhead.Gold set value 112
data modify storage stardew:config Prices.Fish.Bullhead.Diamond set value 150

# 鲤鱼 (30)
data modify storage stardew:config Prices.Fish.Carp.Base set value 30
data modify storage stardew:config Prices.Fish.Carp.Silver set value 37
data modify storage stardew:config Prices.Fish.Carp.Gold set value 45
data modify storage stardew:config Prices.Fish.Carp.Diamond set value 60

# 大比目鱼 (80)
data modify storage stardew:config Prices.Fish.Halibut.Base set value 80
data modify storage stardew:config Prices.Fish.Halibut.Silver set value 100
data modify storage stardew:config Prices.Fish.Halibut.Gold set value 120
data modify storage stardew:config Prices.Fish.Halibut.Diamond set value 160

# 鳗鱼 (325 - Rare)
data modify storage stardew:config Prices.Fish.Eel.Base set value 325
data modify storage stardew:config Prices.Fish.Eel.Silver set value 406
data modify storage stardew:config Prices.Fish.Eel.Gold set value 487
data modify storage stardew:config Prices.Fish.Eel.Diamond set value 650

# [传说] 绯红鱼 (3000)
data modify storage stardew:config Prices.Fish.LegendCrimson.Base set value 3000
data modify storage stardew:config Prices.Fish.LegendCrimson.Silver set value 3750
data modify storage stardew:config Prices.Fish.LegendCrimson.Gold set value 4500
data modify storage stardew:config Prices.Fish.LegendCrimson.Diamond set value 6000


# --- 🔥 夏季鱼类 (Summer) ---

# 虹鳟鱼 (65)
data modify storage stardew:config Prices.Fish.RainbowTrout.Base set value 65
data modify storage stardew:config Prices.Fish.RainbowTrout.Silver set value 81
data modify storage stardew:config Prices.Fish.RainbowTrout.Gold set value 97
data modify storage stardew:config Prices.Fish.RainbowTrout.Diamond set value 130

# 罗非鱼 (75)
data modify storage stardew:config Prices.Fish.Tilapia.Base set value 75
data modify storage stardew:config Prices.Fish.Tilapia.Silver set value 93
data modify storage stardew:config Prices.Fish.Tilapia.Gold set value 112
data modify storage stardew:config Prices.Fish.Tilapia.Diamond set value 150

# 红鲻鱼 (85)
data modify storage stardew:config Prices.Fish.RedMullet.Base set value 85
data modify storage stardew:config Prices.Fish.RedMullet.Silver set value 106
data modify storage stardew:config Prices.Fish.RedMullet.Gold set value 127
data modify storage stardew:config Prices.Fish.RedMullet.Diamond set value 170

# 狗鱼 (100)
data modify storage stardew:config Prices.Fish.Pike.Base set value 100
data modify storage stardew:config Prices.Fish.Pike.Silver set value 125
data modify storage stardew:config Prices.Fish.Pike.Gold set value 150
data modify storage stardew:config Prices.Fish.Pike.Diamond set value 200

# 金枪鱼 (130) - 注意原白皮书是130，代码里统一调整
data modify storage stardew:config Prices.Fish.Tuna.Base set value 130
data modify storage stardew:config Prices.Fish.Tuna.Silver set value 162
data modify storage stardew:config Prices.Fish.Tuna.Gold set value 195
data modify storage stardew:config Prices.Fish.Tuna.Diamond set value 260

# 鲟鱼 (200)
data modify storage stardew:config Prices.Fish.Sturgeon.Base set value 200
data modify storage stardew:config Prices.Fish.Sturgeon.Silver set value 250
data modify storage stardew:config Prices.Fish.Sturgeon.Gold set value 300
data modify storage stardew:config Prices.Fish.Sturgeon.Diamond set value 400

# 河豚 (250)
data modify storage stardew:config Prices.Fish.Pufferfish.Base set value 250
data modify storage stardew:config Prices.Fish.Pufferfish.Silver set value 312
data modify storage stardew:config Prices.Fish.Pufferfish.Gold set value 375
data modify storage stardew:config Prices.Fish.Pufferfish.Diamond set value 500

# 章鱼 (400)
data modify storage stardew:config Prices.Fish.Octopus.Base set value 400
data modify storage stardew:config Prices.Fish.Octopus.Silver set value 500
data modify storage stardew:config Prices.Fish.Octopus.Gold set value 600
data modify storage stardew:config Prices.Fish.Octopus.Diamond set value 800

# 超级海参 (500)
data modify storage stardew:config Prices.Fish.SuperCucumber.Base set value 500
data modify storage stardew:config Prices.Fish.SuperCucumber.Silver set value 625
data modify storage stardew:config Prices.Fish.SuperCucumber.Gold set value 750
data modify storage stardew:config Prices.Fish.SuperCucumber.Diamond set value 1000

# 多拉多鱼 (100)
data modify storage stardew:config Prices.Fish.Dorado.Base set value 100
data modify storage stardew:config Prices.Fish.Dorado.Silver set value 125
data modify storage stardew:config Prices.Fish.Dorado.Gold set value 150
data modify storage stardew:config Prices.Fish.Dorado.Diamond set value 200

# [传说] 琵琶鱼 (3000)
data modify storage stardew:config Prices.Fish.LegendAngler.Base set value 3000
data modify storage stardew:config Prices.Fish.LegendAngler.Silver set value 3750
data modify storage stardew:config Prices.Fish.LegendAngler.Gold set value 4500
data modify storage stardew:config Prices.Fish.LegendAngler.Diamond set value 6000


# --- 🍂 秋季鱼类 (Fall) ---

# 三文鱼 (120)
data modify storage stardew:config Prices.Fish.Salmon.Base set value 120
data modify storage stardew:config Prices.Fish.Salmon.Silver set value 150
data modify storage stardew:config Prices.Fish.Salmon.Gold set value 180
data modify storage stardew:config Prices.Fish.Salmon.Diamond set value 240

# 虎纹鳟鱼 (200)
data modify storage stardew:config Prices.Fish.TigerTrout.Base set value 200
data modify storage stardew:config Prices.Fish.TigerTrout.Silver set value 250
data modify storage stardew:config Prices.Fish.TigerTrout.Gold set value 300
data modify storage stardew:config Prices.Fish.TigerTrout.Diamond set value 400

# 大嘴鲈鱼 (100)
data modify storage stardew:config Prices.Fish.Largemouth.Base set value 100
data modify storage stardew:config Prices.Fish.Largemouth.Silver set value 125
data modify storage stardew:config Prices.Fish.Largemouth.Gold set value 150
data modify storage stardew:config Prices.Fish.Largemouth.Diamond set value 200

# 红鲷鱼 (60)
data modify storage stardew:config Prices.Fish.RedSnapper.Base set value 60
data modify storage stardew:config Prices.Fish.RedSnapper.Silver set value 75
data modify storage stardew:config Prices.Fish.RedSnapper.Gold set value 90
data modify storage stardew:config Prices.Fish.RedSnapper.Diamond set value 120

# 海参 (180)
data modify storage stardew:config Prices.Fish.SeaCucumber.Base set value 180
data modify storage stardew:config Prices.Fish.SeaCucumber.Silver set value 225
data modify storage stardew:config Prices.Fish.SeaCucumber.Gold set value 270
data modify storage stardew:config Prices.Fish.SeaCucumber.Diamond set value 360

# 大眼鱼 (220)
data modify storage stardew:config Prices.Fish.Walleye.Base set value 220
data modify storage stardew:config Prices.Fish.Walleye.Silver set value 275
data modify storage stardew:config Prices.Fish.Walleye.Gold set value 330
data modify storage stardew:config Prices.Fish.Walleye.Diamond set value 440

# 午夜鲤鱼 (150)
data modify storage stardew:config Prices.Fish.MidnightCarp.Base set value 150
data modify storage stardew:config Prices.Fish.MidnightCarp.Silver set value 187
data modify storage stardew:config Prices.Fish.MidnightCarp.Gold set value 225
data modify storage stardew:config Prices.Fish.MidnightCarp.Diamond set value 300

# 海鳗 (350)
data modify storage stardew:config Prices.Fish.SeaEel.Base set value 350
data modify storage stardew:config Prices.Fish.SeaEel.Silver set value 437
data modify storage stardew:config Prices.Fish.SeaEel.Gold set value 525
data modify storage stardew:config Prices.Fish.SeaEel.Diamond set value 700

# [传说] 鮟鱇鱼 (3000)
data modify storage stardew:config Prices.Fish.LegendAnglerfish.Base set value 3000
data modify storage stardew:config Prices.Fish.LegendAnglerfish.Silver set value 3750
data modify storage stardew:config Prices.Fish.LegendAnglerfish.Gold set value 4500
data modify storage stardew:config Prices.Fish.LegendAnglerfish.Diamond set value 6000


# --- ❄️ 冬季鱼类 (Winter) ---

# 河鲈 (60)
data modify storage stardew:config Prices.Fish.Perch.Base set value 60
data modify storage stardew:config Prices.Fish.Perch.Silver set value 75
data modify storage stardew:config Prices.Fish.Perch.Gold set value 90
data modify storage stardew:config Prices.Fish.Perch.Diamond set value 120

# 鱿鱼 (180)
data modify storage stardew:config Prices.Fish.Squid.Base set value 180
data modify storage stardew:config Prices.Fish.Squid.Silver set value 225
data modify storage stardew:config Prices.Fish.Squid.Gold set value 270
data modify storage stardew:config Prices.Fish.Squid.Diamond set value 360

# 青花鱼 (75)
data modify storage stardew:config Prices.Fish.Albacore.Base set value 75
data modify storage stardew:config Prices.Fish.Albacore.Silver set value 93
data modify storage stardew:config Prices.Fish.Albacore.Gold set value 112
data modify storage stardew:config Prices.Fish.Albacore.Diamond set value 150

# 蛇齿单线鱼 (500)
data modify storage stardew:config Prices.Fish.Lingcod.Base set value 500
data modify storage stardew:config Prices.Fish.Lingcod.Silver set value 625
data modify storage stardew:config Prices.Fish.Lingcod.Gold set value 750
data modify storage stardew:config Prices.Fish.Lingcod.Diamond set value 1000

# [传说] 冰川鱼 (5000)
data modify storage stardew:config Prices.Fish.LegendGlacier.Base set value 5000
data modify storage stardew:config Prices.Fish.LegendGlacier.Silver set value 6250
data modify storage stardew:config Prices.Fish.LegendGlacier.Gold set value 7500
data modify storage stardew:config Prices.Fish.LegendGlacier.Diamond set value 10000


# --- 🔮 特殊鱼类 (Special) ---

# 幽灵鱼 (150)
data modify storage stardew:config Prices.Fish.Ghostfish.Base set value 150
data modify storage stardew:config Prices.Fish.Ghostfish.Silver set value 187
data modify storage stardew:config Prices.Fish.Ghostfish.Gold set value 225
data modify storage stardew:config Prices.Fish.Ghostfish.Diamond set value 300

# 石鱼 (200)
data modify storage stardew:config Prices.Fish.Stonefish.Base set value 200
data modify storage stardew:config Prices.Fish.Stonefish.Silver set value 250
data modify storage stardew:config Prices.Fish.Stonefish.Gold set value 300
data modify storage stardew:config Prices.Fish.Stonefish.Diamond set value 400

# 冰柱鱼 (300)
data modify storage stardew:config Prices.Fish.IcePip.Base set value 300
data modify storage stardew:config Prices.Fish.IcePip.Silver set value 375
data modify storage stardew:config Prices.Fish.IcePip.Gold set value 450
data modify storage stardew:config Prices.Fish.IcePip.Diamond set value 600

# 岩浆鳗鱼 (800)
data modify storage stardew:config Prices.Fish.LavaEel.Base set value 800
data modify storage stardew:config Prices.Fish.LavaEel.Silver set value 1000
data modify storage stardew:config Prices.Fish.LavaEel.Gold set value 1200
data modify storage stardew:config Prices.Fish.LavaEel.Diamond set value 1600

# 沙鱼 (200)
data modify storage stardew:config Prices.Fish.Sandfish.Base set value 200
data modify storage stardew:config Prices.Fish.Sandfish.Silver set value 250
data modify storage stardew:config Prices.Fish.Sandfish.Gold set value 300
data modify storage stardew:config Prices.Fish.Sandfish.Diamond set value 400

# 蝎鲤 (400)
data modify storage stardew:config Prices.Fish.ScorpionCarp.Base set value 400
data modify storage stardew:config Prices.Fish.ScorpionCarp.Silver set value 500
data modify storage stardew:config Prices.Fish.ScorpionCarp.Gold set value 600
data modify storage stardew:config Prices.Fish.ScorpionCarp.Diamond set value 800

# 钓竿基础钓力
data modify storage stardew:config Rods.Copper.BasePower set value 1
data modify storage stardew:config Rods.Iron.BasePower set value 2
data modify storage stardew:config Rods.Gold.BasePower set value 3
data modify storage stardew:config Rods.Diamond.BasePower set value 4

# 钓竿最低等级
data modify storage stardew:config Rods.Copper.MinLevel set value 0
data modify storage stardew:config Rods.Iron.MinLevel set value 3
data modify storage stardew:config Rods.Gold.MinLevel set value 6
data modify storage stardew:config Rods.Diamond.MinLevel set value 9

# 渔具最低等级
data modify storage stardew:config Tackles.Sonar.MinLevel set value 6
data modify storage stardew:config Tackles.Quality.MinLevel set value 7