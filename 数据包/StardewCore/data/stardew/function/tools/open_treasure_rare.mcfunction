# data/stardew/functions/tools/open_treasure_rare.mcfunction
playsound minecraft:block.chest.open player @s ~ ~ ~ 1 1
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 1 2
particle minecraft:wax_on ~ ~1 ~ 0.5 0.5 0.5 0 10
loot give @s loot stardew:fishing/treasure_rare
clear @s carrot_on_a_stick[custom_model_data=40520] 1
tellraw @s {"text":"[系统] 打开了 稀有宝箱！","color":"aqua"}