# 背刺技能冷却结束
scoreboard players set @s sd_skill_cooldown 0
tag @s remove sd_using_backstab
bossbar remove stardew:backstab_cooldown
playsound minecraft:block.note_block.chime player @s ~ ~ ~ 0.5 2
title @s actionbar {"text":"✓ 背刺技能已就绪","color":"green","bold":true}
