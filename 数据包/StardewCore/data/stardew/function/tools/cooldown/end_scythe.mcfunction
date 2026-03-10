# data/stardew/functions/tools/cooldown/end_scythe.mcfunction
# 镰刀冷却结束

# 隐藏 Boss 血条
bossbar set stardew:scythe_cooldown visible false

# 播放完成音效
playsound minecraft:item.trident.return player @s ~ ~ ~ 0.8 1.2
