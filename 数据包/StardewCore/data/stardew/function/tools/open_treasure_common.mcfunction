# data/stardew/functions/tools/open_treasure_common.mcfunction
playsound minecraft:block.barrel.open player @s ~ ~ ~ 1 0.8
particle minecraft:wax_on ~ ~1 ~ 0.5 0.5 0.5 0 5
loot give @s loot stardew:fishing/treasure_common
clear @s carrot_on_a_stick[custom_model_data=40510] 1
tellraw @s {"text":"[系统] 打开了 普通宝箱。","color":"white"}