# data/stardew/functions/tools/cooldown/end_hoe.mcfunction
# 锄头冷却结束

# 隐藏 Boss 血条
bossbar set stardew:hoe_cooldown visible false

# 播放完成音效
playsound minecraft:block.anvil.land player @s ~ ~ ~ 0.5 1.5
