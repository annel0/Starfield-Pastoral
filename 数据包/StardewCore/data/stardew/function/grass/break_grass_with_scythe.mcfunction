# 草被镰刀破坏时的逻辑（单个收割）
# 不产生掉落物，但会尝试添加干草到筒仓

# 破坏草并直接计算干草（右键草时调用）
# 播放破坏音效
playsound minecraft:block.grass.break block @a ~ ~ ~ 1 1

# 生成粒子效果
particle minecraft:block{block_state:"minecraft:grass_block"} ~ ~0.5 ~ 0.3 0.3 0.3 0 20

# 简单增加计数，让镰刀函数统一处理
execute as @a[distance=..10,limit=1,sort=nearest] run scoreboard players add @s sd_grass_harvested 1

# 杀死草显示实体
kill @e[type=item_display,tag=sd_grass,distance=..1,limit=1,sort=nearest]

# 杀死自己(interaction)
kill @s