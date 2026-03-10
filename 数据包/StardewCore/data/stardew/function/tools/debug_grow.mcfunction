# data/stardew/functions/tools/debug_grow.mcfunction
tellraw @s {"text":"[Debug] 催熟 Marker 作物...","color":"red"}
execute as @e[type=marker,tag=sd_crop,distance=..5] at @s run function stardew:farming/grow_logic_impl
