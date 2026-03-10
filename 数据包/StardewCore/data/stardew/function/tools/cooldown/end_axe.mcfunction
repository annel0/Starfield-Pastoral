# data/stardew/functions/tools/cooldown/end_axe.mcfunction
# 斧头冷却结束

# 隐藏 Boss 血条
bossbar set stardew:axe_cooldown visible false

# 播放完成音效
execute at @s run playsound minecraft:block.wood.break player @s ~ ~ ~ 1 1.2
