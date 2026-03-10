# 蓄力重击技能冷却结束

# 隐藏bossbar
bossbar set stardew:heavy_charge_cooldown visible false
bossbar set stardew:heavy_charge_cooldown players

# 移除标签
tag @s remove sd_using_heavy_charge

# 播放提示音
playsound minecraft:block.note_block.pling player @s ~ ~ ~ 0.5 2
playsound minecraft:block.anvil.land player @s ~ ~ ~ 0.3 1.5
