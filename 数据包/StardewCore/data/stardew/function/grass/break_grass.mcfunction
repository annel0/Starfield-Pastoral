# 草被其他工具破坏时的逻辑（剑/斧/镐）
# 不产生任何掉落物，只破坏

# 播放破坏音效
playsound minecraft:block.grass.break block @a ~ ~ ~ 1 1

# 生成粒子效果
particle minecraft:block{block_state:"minecraft:grass_block"} ~ ~0.5 ~ 0.3 0.3 0.3 0 20

# 杀死草显示实体
kill @e[type=item_display,tag=sd_grass,distance=..1,limit=1,sort=nearest]

# 杀死自己(interaction)
kill @s