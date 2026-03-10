# data/stardew/functions/tools/cooldown/end_pickaxe.mcfunction
# 镐子冷却结束

# 隐藏 Boss 血条
bossbar set stardew:pickaxe_cooldown visible false

# 播放完成音效
execute at @s run playsound minecraft:block.stone.break player @s ~ ~ ~ 0.8 1.2
