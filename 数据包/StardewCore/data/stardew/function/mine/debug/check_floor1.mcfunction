# stardew:mine/debug/check_floor1.mcfunction
# 详细检查第一层状态

tellraw @s {"text":"===== 检查第一层状态 =====","color":"gold"}

# 第一层参数: z=100, z2=102, z22=122
tellraw @s {"text":"第一层: z=100, z2=102, z22=122","color":"gray"}

# 检查地面方块 (Y=64 应该是 stone)
execute in stardew:mine if block 0 64 110 minecraft:stone run tellraw @s {"text":"✓ 地面 (0,64,110) = stone","color":"green"}
execute in stardew:mine unless block 0 64 110 minecraft:stone run tellraw @s {"text":"✗ 地面 (0,64,110) 不是 stone!","color":"red"}

# 检查空气层 (Y=65 应该是 air)
execute in stardew:mine if block 0 65 110 minecraft:air run tellraw @s {"text":"✓ 空气层 (0,65,110) = air","color":"green"}
execute in stardew:mine unless block 0 65 110 minecraft:air run tellraw @s {"text":"✗ 空气层 (0,65,110) 不是 air!","color":"red"}

# 检查出口梯子位置 (0, 65, 102)
execute in stardew:mine if block 0 65 102 minecraft:ladder run tellraw @s {"text":"✓ 梯子 (0,65,102) = ladder","color":"green"}
execute in stardew:mine unless block 0 65 102 minecraft:ladder run tellraw @s {"text":"✗ 梯子 (0,65,102) 不是 ladder!","color":"red"}

# 检查梯子后面的墙 (0, 65, 101)
execute in stardew:mine if block 0 65 101 minecraft:stone_bricks run tellraw @s {"text":"✓ 墙壁 (0,65,101) = stone_bricks","color":"green"}
execute in stardew:mine unless block 0 65 101 minecraft:stone_bricks run tellraw @s {"text":"✗ 墙壁 (0,65,101) 不是 stone_bricks!","color":"red"}

# 检查第一层出口梯子交互实体
tellraw @s {"text":"--- 检查交互实体 ---","color":"yellow"}
execute in stardew:mine positioned 0 65 102 as @e[type=interaction,tag=sd_mine_ladder_exit,distance=..2] run tellraw @a [{"text":"✓ 找到出口梯子实体: ","color":"green"},{"nbt":"Pos","entity":"@s"}]
execute in stardew:mine positioned 0 65 102 unless entity @e[type=interaction,tag=sd_mine_ladder_exit,distance=..2] run tellraw @s {"text":"✗ 在 (0,65,102) 附近没找到出口梯子实体!","color":"red"}

# 检查第二层出口梯子交互实体 (z=200, z2=202)
execute in stardew:mine positioned 0 65 202 as @e[type=interaction,tag=sd_mine_ladder_exit,distance=..2] run tellraw @a [{"text":"✓ 第二层出口梯子实体: ","color":"green"},{"nbt":"Pos","entity":"@s"}]

# 列出所有出口梯子实体
tellraw @s {"text":"--- 所有出口梯子实体 ---","color":"yellow"}
execute as @e[type=interaction,tag=sd_mine_ladder_exit] run tellraw @a [{"text":"  - ","color":"gray"},{"nbt":"Pos","entity":"@s"}]

tellraw @s {"text":"===== 检查完成 =====","color":"gold"}
