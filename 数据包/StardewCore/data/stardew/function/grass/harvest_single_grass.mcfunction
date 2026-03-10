# 收割单个草（大范围收割时调用）
# 尝试添加干草到筒仓，然后移除草

# 尝试向筒仓添加干草
execute on target run function stardew:grass/try_add_hay

# 增加收割计数器
scoreboard players add grass_harvest_count sd_temp 1

# 播放破坏音效和粒子效果
playsound minecraft:block.grass.break block @a ~ ~ ~ 0.8 1.2
particle minecraft:block{block_state:"minecraft:grass_block"} ~ ~0.5 ~ 0.2 0.2 0.2 0 10

# 杀死草显示实体
kill @e[type=item_display,tag=sd_grass,distance=..1,limit=1,sort=nearest]

# 杀死自己(interaction)
kill @s