# data/stardew/functions/tools/debug_wand.mcfunction

# 1. 播放特效
particle minecraft:composter ~ ~1 ~ 0.5 0.5 0.5 0 20
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 1 2

# 2. 执行生长 (核心生长逻辑)
# A. 作物 (sd_crop) - [修复] 作物逻辑实体是 marker，不是 item_display！
execute as @e[type=marker,tag=sd_crop,distance=..5] run function stardew:farming/grow_logic_impl

# B. 树木 (sd_tree) - 树木的逻辑实体是 Interaction
# [修复] 树木调用自己的grow_check（内部会调用grow_logic_impl）
execute as @e[type=interaction,tag=sd_tree,distance=..5] at @s run function stardew:tree/grow_check

tellraw @s {"text":"[Debug] 施展魔法！周围作物与树木生长 +1 天","color":"light_purple"}