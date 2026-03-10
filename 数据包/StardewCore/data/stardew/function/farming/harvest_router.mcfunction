# data/stardew/functions/farming/harvest_router.mcfunction
# [执行者: 作物实体]

# 枯萎作物处理
execute if entity @s[tag=crop_dead] run function stardew:farming/dead_harvest
execute if entity @s[tag=crop_dead] run return 1

# ==================================================
# 春季作物 (Spring)
# ==================================================
# 防风草 (4天, 不再生)
execute if entity @s[tag=crop_parsnip] run function stardew:farming/api/harvest_crop {id:"spring/parsnip", mature:4, regrow:0}
# 大蒜 (4天, 不再生)
execute if entity @s[tag=crop_garlic] run function stardew:farming/api/harvest_crop {id:"spring/garlic", mature:4, regrow:0}
# 土豆 (6天, 不再生)
execute if entity @s[tag=crop_potato] run function stardew:farming/api/harvest_crop {id:"spring/potato", mature:6, regrow:0}
# 郁金香 (6天, 不再生)
execute if entity @s[tag=crop_tulip] run function stardew:farming/api/harvest_crop {id:"spring/tulip", mature:6, regrow:0}
# 甘蓝 (6天, 不再生)
execute if entity @s[tag=crop_kale] run function stardew:farming/api/harvest_crop {id:"spring/kale", mature:6, regrow:0}
# 蓝爵士 (7天, 不再生)
execute if entity @s[tag=crop_blue_jazz] run function stardew:farming/api/harvest_crop {id:"spring/blue_jazz", mature:7, regrow:0}
# 花椰菜 (12天, 不再生)
execute if entity @s[tag=crop_cauliflower] run function stardew:farming/api/harvest_crop {id:"spring/cauliflower", mature:12, regrow:0}
# 胡萝卜 (3天, 不再生) [1.6新增]
execute if entity @s[tag=crop_carrot] run function stardew:farming/api/harvest_crop {id:"spring/carrot", mature:3, regrow:0}
# 大黄 (13天, 不再生) [1.6新增]
execute if entity @s[tag=crop_rhubarb] run function stardew:farming/api/harvest_crop {id:"spring/rhubarb", mature:13, regrow:0}

# ★ 再生作物 ★
# 青豆 (10天成熟, 3天再生, 收获后回到age=7)
execute if entity @s[tag=crop_green_bean] run function stardew:farming/api/harvest_crop {id:"spring/green_bean", mature:10, regrow:7}
# 草莓 (8天成熟, 4天再生, 收获后回到age=4)
execute if entity @s[tag=crop_strawberry] run function stardew:farming/api/harvest_crop {id:"spring/strawberry", mature:8, regrow:4}
# 咖啡豆 (10天成熟, 2天再生, 收获后回到age=8)
execute if entity @s[tag=crop_coffee_bean] run function stardew:farming/api/harvest_crop {id:"spring/coffee_bean", mature:10, regrow:8}


# ==================================================
# 夏季作物 (Summer)
# ==================================================
# 小麦 (4天, 不再生)
execute if entity @s[tag=crop_wheat] run function stardew:farming/api/harvest_crop {id:"summer/wheat", mature:4, regrow:0}
# 萝卜 (6天, 不再生)
execute if entity @s[tag=crop_radish] run function stardew:farming/api/harvest_crop {id:"summer/radish", mature:6, regrow:0}
# 红叶卷心菜 (9天, 不再生)
execute if entity @s[tag=crop_red_cabbage] run function stardew:farming/api/harvest_crop {id:"summer/red_cabbage", mature:9, regrow:0}
# 虞美人 (7天, 不再生)
execute if entity @s[tag=crop_poppy] run function stardew:farming/api/harvest_crop {id:"summer/poppy", mature:7, regrow:0}
# 夏季亮片 (8天, 不再生)
execute if entity @s[tag=crop_summer_spangle] run function stardew:farming/api/harvest_crop {id:"summer/summer_spangle", mature:8, regrow:0}
# 甜瓜 (12天, 不再生)
execute if entity @s[tag=crop_melon] run function stardew:farming/api/harvest_crop {id:"summer/melon", mature:12, regrow:0}
# 杨桃 (13天, 不再生)
execute if entity @s[tag=crop_starfruit] run function stardew:farming/api/harvest_crop {id:"summer/starfruit", mature:13, regrow:0}

# ★ 再生作物 ★
# 番茄 (11天成熟, 再生回第7阶段)
execute if entity @s[tag=crop_tomato] run function stardew:farming/api/harvest_crop {id:"summer/tomato", mature:11, regrow:7}
# 辣椒 (5天成熟, 再生回第2阶段)
execute if entity @s[tag=crop_hot_pepper] run function stardew:farming/api/harvest_crop {id:"summer/hot_pepper", mature:5, regrow:2}
# 蓝莓 (13天成熟, 再生回第9阶段)
execute if entity @s[tag=crop_blueberry] run function stardew:farming/api/harvest_crop {id:"summer/blueberry", mature:13, regrow:9}
# 玉米 (14天成熟, 再生回第10阶段)
execute if entity @s[tag=crop_corn] run function stardew:farming/api/harvest_crop {id:"summer/corn", mature:14, regrow:10}
# 啤酒花 (11天成熟, 再生回第10阶段)
execute if entity @s[tag=crop_hops] run function stardew:farming/api/harvest_crop {id:"summer/hops", mature:11, regrow:10}
# 夏季南瓜 (6天成熟, 3天再生, 收获后回到age=3) [1.6新增]
execute if entity @s[tag=crop_summer_squash] run function stardew:farming/api/harvest_crop {id:"summer/summer_squash", mature:6, regrow:3}


# ==================================================
# 秋季作物 (Fall)
# ==================================================
# 小白菜 (4天, 不再生)
execute if entity @s[tag=crop_bok_choy] run function stardew:farming/api/harvest_crop {id:"fall/bok_choy", mature:4, regrow:0}
# 甜薯 (10天, 不再生)
execute if entity @s[tag=crop_yam] run function stardew:farming/api/harvest_crop {id:"fall/yam", mature:10, regrow:0}
# 苋菜 (7天, 不再生)
execute if entity @s[tag=crop_amaranth] run function stardew:farming/api/harvest_crop {id:"fall/amaranth", mature:7, regrow:0}
# 向日葵 (8天, 不再生)
execute if entity @s[tag=crop_sunflower] run function stardew:farming/api/harvest_crop {id:"fall/sunflower", mature:8, regrow:0}
# 仙女玫瑰 (12天, 不再生)
execute if entity @s[tag=crop_fairy_rose] run function stardew:farming/api/harvest_crop {id:"fall/fairy_rose", mature:12, regrow:0}
# 洋蓟 (8天, 不再生)
execute if entity @s[tag=crop_artichoke] run function stardew:farming/api/harvest_crop {id:"fall/artichoke", mature:8, regrow:0}
# 南瓜 (13天, 不再生)
execute if entity @s[tag=crop_pumpkin] run function stardew:farming/api/harvest_crop {id:"fall/pumpkin", mature:13, regrow:0}

# ★ 再生作物 ★
# 茄子 (5天成熟, 再生回第1阶段)
execute if entity @s[tag=crop_eggplant] run function stardew:farming/api/harvest_crop {id:"fall/eggplant", mature:5, regrow:1}
# 蔓越莓 (7天成熟, 再生回第2阶段)
execute if entity @s[tag=crop_cranberry] run function stardew:farming/api/harvest_crop {id:"fall/cranberry", mature:7, regrow:2}
# 葡萄 (10天成熟, 再生回第7阶段)
execute if entity @s[tag=crop_grape] run function stardew:farming/api/harvest_crop {id:"fall/grape", mature:10, regrow:7}
# 西兰花 (8天成熟, 再生回第4阶段) [1.6新增]
execute if entity @s[tag=crop_broccoli] run function stardew:farming/api/harvest_crop {id:"fall/broccoli", mature:8, regrow:4}


# ==================================================
# 冬季作物 (Winter)
# ==================================================
# 冬根 (7天, 不再生)
execute if entity @s[tag=crop_winter_root] run function stardew:farming/api/harvest_crop {id:"winter/winter_root", mature:7, regrow:0}
# 雪山药 (7天, 不再生)
execute if entity @s[tag=crop_snow_yam] run function stardew:farming/api/harvest_crop {id:"winter/snow_yam", mature:7, regrow:0}
# 水晶果 (9天, 不再生)
execute if entity @s[tag=crop_crystal_fruit] run function stardew:farming/api/harvest_crop {id:"winter/crystal_fruit", mature:9, regrow:0}
# 霜瓜 (7天, 不再生) [1.6新增]
execute if entity @s[tag=crop_powder_melon] run function stardew:farming/api/harvest_crop {id:"winter/powder_melon", mature:7, regrow:0}

# ★ 远古水果 (28天成熟, 再生回第21阶段) - 春夏秋三季可种植
execute if entity @s[tag=crop_ancient_fruit,tag=season_1] run function stardew:farming/api/harvest_crop {id:"spring/ancient_fruit", mature:28, regrow:21}
execute if entity @s[tag=crop_ancient_fruit,tag=season_2] run function stardew:farming/api/harvest_crop {id:"summer/ancient_fruit", mature:28, regrow:21}
execute if entity @s[tag=crop_ancient_fruit,tag=season_3] run function stardew:farming/api/harvest_crop {id:"fall/ancient_fruit", mature:28, regrow:21}