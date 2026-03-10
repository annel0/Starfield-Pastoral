# data/stardew/function/farming/xp/crop_xp_table.mcfunction
# 作物经验值表 (基于官方公式: XP = 16 × ln(0.018 × price + 1))
# 使用方式: 
#   data modify storage stardew:farming crop set value "parsnip"
#   execute as <player> run function stardew:farming/xp/crop_xp_table

# ==================================================
# 春季作物 (Spring)
# ==================================================
# 防风草 (价格35g) = 8 XP
execute if data storage stardew:farming {crop:"parsnip"} run scoreboard players add @s sd_farming_xp 8

# 大蒜 (价格60g) = 12 XP
execute if data storage stardew:farming {crop:"garlic"} run scoreboard players add @s sd_farming_xp 12

# 土豆 (价格80g) = 14 XP
execute if data storage stardew:farming {crop:"potato"} run scoreboard players add @s sd_farming_xp 14

# 郁金香 (价格30g) = 7 XP
execute if data storage stardew:farming {crop:"tulip"} run scoreboard players add @s sd_farming_xp 7

# 甘蓝 (价格110g) = 17 XP
execute if data storage stardew:farming {crop:"kale"} run scoreboard players add @s sd_farming_xp 17

# 蓝爵士 (价格50g) = 10 XP
execute if data storage stardew:farming {crop:"blue_jazz"} run scoreboard players add @s sd_farming_xp 10

# 花椰菜 (价格175g) = 23 XP
execute if data storage stardew:farming {crop:"cauliflower"} run scoreboard players add @s sd_farming_xp 23

# 胡萝卜 (价格35g) = 8 XP
execute if data storage stardew:farming {crop:"carrot"} run scoreboard players add @s sd_farming_xp 8

# 大黄 (价格220g) = 26 XP
execute if data storage stardew:farming {crop:"rhubarb"} run scoreboard players add @s sd_farming_xp 26

# 青豆 (价格40g) = 9 XP
execute if data storage stardew:farming {crop:"green_bean"} run scoreboard players add @s sd_farming_xp 9

# 草莓 (价格120g) = 18 XP
execute if data storage stardew:farming {crop:"strawberry"} run scoreboard players add @s sd_farming_xp 18

# 咖啡豆 (价格15g) = 4 XP
execute if data storage stardew:farming {crop:"coffee_bean"} run scoreboard players add @s sd_farming_xp 4

# ==================================================
# 夏季作物 (Summer)
# ==================================================
# 小麦 (价格25g) = 6 XP
execute if data storage stardew:farming {crop:"wheat"} run scoreboard players add @s sd_farming_xp 6

# 萝卜 (价格90g) = 15 XP
execute if data storage stardew:farming {crop:"radish"} run scoreboard players add @s sd_farming_xp 15

# 红叶卷心菜 (价格260g) = 28 XP
execute if data storage stardew:farming {crop:"red_cabbage"} run scoreboard players add @s sd_farming_xp 28

# 虞美人 (价格140g) = 20 XP
execute if data storage stardew:farming {crop:"poppy"} run scoreboard players add @s sd_farming_xp 20

# 夏季亮片 (价格90g) = 15 XP
execute if data storage stardew:farming {crop:"summer_spangle"} run scoreboard players add @s sd_farming_xp 15

# 甜瓜 (价格250g) = 27 XP
execute if data storage stardew:farming {crop:"melon"} run scoreboard players add @s sd_farming_xp 27

# 玉米 (价格50g) = 10 XP
execute if data storage stardew:farming {crop:"corn"} run scoreboard players add @s sd_farming_xp 10

# 番茄 (价格60g) = 12 XP
execute if data storage stardew:farming {crop:"tomato"} run scoreboard players add @s sd_farming_xp 12

# 蓝莓 (价格50g) = 10 XP
execute if data storage stardew:farming {crop:"blueberry"} run scoreboard players add @s sd_farming_xp 10

# 辣椒 (价格40g) = 9 XP
execute if data storage stardew:farming {crop:"hot_pepper"} run scoreboard players add @s sd_farming_xp 9

# 啤酒花 (价格25g) = 6 XP
execute if data storage stardew:farming {crop:"hops"} run scoreboard players add @s sd_farming_xp 6

# 杨桃 (价格750g) = 43 XP
execute if data storage stardew:farming {crop:"starfruit"} run scoreboard players add @s sd_farming_xp 43

# 夏南瓜 (价格45g) = 9 XP
execute if data storage stardew:farming {crop:"summer_squash"} run scoreboard players add @s sd_farming_xp 9

# ==================================================
# 秋季作物 (Fall)
# ==================================================
# 茄子 (价格60g) = 12 XP
execute if data storage stardew:farming {crop:"eggplant"} run scoreboard players add @s sd_farming_xp 12

# 西兰花 (价格70g) = 13 XP
execute if data storage stardew:farming {crop:"broccoli"} run scoreboard players add @s sd_farming_xp 13

# 小白菜 (价格80g) = 14 XP
execute if data storage stardew:farming {crop:"bok_choy"} run scoreboard players add @s sd_farming_xp 14

# 蔓越莓 (价格75g) = 14 XP
execute if data storage stardew:farming {crop:"cranberries"} run scoreboard players add @s sd_farming_xp 14

# 葡萄 (价格80g) = 14 XP
execute if data storage stardew:farming {crop:"grape"} run scoreboard players add @s sd_farming_xp 14

# 向日葵 (价格40g) = 5 XP (特殊: 使用种子价格计算)
execute if data storage stardew:farming {crop:"sunflower"} run scoreboard players add @s sd_farming_xp 5

# 甜菜 (价格100g) = 16 XP
execute if data storage stardew:farming {crop:"beet"} run scoreboard players add @s sd_farming_xp 16

# 苋菜 (价格150g) = 21 XP
execute if data storage stardew:farming {crop:"amaranth"} run scoreboard players add @s sd_farming_xp 21

# 朝鲜蓟 (价格160g) = 22 XP
execute if data storage stardew:farming {crop:"artichoke"} run scoreboard players add @s sd_farming_xp 22

# 山芋 (价格160g) = 22 XP
execute if data storage stardew:farming {crop:"yam"} run scoreboard players add @s sd_farming_xp 22

# 仙女玫瑰 (价格290g) = 29 XP
execute if data storage stardew:farming {crop:"fairy_rose"} run scoreboard players add @s sd_farming_xp 29

# 南瓜 (价格320g) = 31 XP
execute if data storage stardew:farming {crop:"pumpkin"} run scoreboard players add @s sd_farming_xp 31

# ==================================================
# 特殊作物 (Special/Multi-Season)
# ==================================================
# 远古水果 (价格550g) = 38 XP
execute if data storage stardew:farming {crop:"ancient_fruit"} run scoreboard players add @s sd_farming_xp 38

# 甜宝石莓 (价格3000g) = 64 XP
execute if data storage stardew:farming {crop:"sweet_gem_berry"} run scoreboard players add @s sd_farming_xp 64

# 仙人掌果 (价格75g) = 14 XP
execute if data storage stardew:farming {crop:"cactus_fruit"} run scoreboard players add @s sd_farming_xp 14

# 芋头 (价格100g) = 16 XP
execute if data storage stardew:farming {crop:"taro_root"} run scoreboard players add @s sd_farming_xp 16

# 菠萝 (价格300g) = 30 XP
execute if data storage stardew:farming {crop:"pineapple"} run scoreboard players add @s sd_farming_xp 30

# 粉瓜 (价格60g) = 12 XP
execute if data storage stardew:farming {crop:"powdermelon"} run scoreboard players add @s sd_farming_xp 12

# 未分类米 (价格30g) = 7 XP
execute if data storage stardew:farming {crop:"unmilled_rice"} run scoreboard players add @s sd_farming_xp 7
