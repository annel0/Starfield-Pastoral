# 格挡冷却结束

# 隐藏 Bossbar
bossbar set stardew:block_cooldown visible false

# 重置冷却时间
scoreboard players set @s sd_block_cooldown 0

# 只播放音效提示
playsound minecraft:block.note_block.chime player @s ~ ~ ~ 0.5 2.0
