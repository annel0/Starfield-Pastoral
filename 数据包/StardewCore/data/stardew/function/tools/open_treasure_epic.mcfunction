# data/stardew/functions/tools/open_treasure_epic.mcfunction
playsound minecraft:block.ender_chest.open player @s ~ ~ ~ 1 0.5
playsound minecraft:ui.toast.challenge_complete player @s ~ ~ ~ 1 1
particle minecraft:totem_of_undying ~ ~1 ~ 0.5 0.5 0.5 0.1 20
loot give @s loot stardew:fishing/treasure_epic
clear @s carrot_on_a_stick[custom_model_data=40530] 1
tellraw @s {"text":"[系统] 打开了 史诗宝箱！！！","color":"light_purple","bold":true}