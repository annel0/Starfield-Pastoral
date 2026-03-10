# 剧毒之刃技能冷却结束
scoreboard players set @s sd_skill_2_cooldown 0
tag @s remove sd_using_poison_blade
bossbar remove stardew:poison_blade_cooldown
playsound minecraft:block.note_block.chime player @s ~ ~ ~ 0.5 2
title @s actionbar {"text":"✓ 剧毒之刃技能已就绪","color":"green","bold":true}
