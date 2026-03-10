# stardew:mine/debug/test_floor1_complete.mcfunction
# 完整测试第一层生成流程

tellraw @s {"text":"===== 完整测试第一层生成 =====","color":"gold"}

# 确保在矿洞维度
execute unless entity @s[nbt={Dimension:"stardew:mine"}] run tellraw @s {"text":"警告: 你不在矿洞维度!","color":"red"}
execute unless entity @s[nbt={Dimension:"stardew:mine"}] run return 0

# 清理之前的实体
execute in stardew:mine run kill @e[tag=sd_mine_entity,x=0,z=100,dx=30,dz=30]
execute in stardew:mine run kill @e[tag=sd_stone,x=0,z=100,dx=30,dz=30]
tellraw @s {"text":"已清理第一层区域实体","color":"gray"}

# 设置为第一层
scoreboard players set @s sd_mine_floor 1
tellraw @s {"text":"设置 sd_mine_floor = 1","color":"gray"}

# 调用生成函数
tellraw @s {"text":"调用 generate_room...","color":"yellow"}
function stardew:mine/floor/generate_room

# 检查结果
tellraw @s {"text":"===== 检查生成结果 =====","color":"gold"}

# 检查出口梯子实体
execute in stardew:mine positioned 0 65 102 if entity @e[type=interaction,tag=sd_mine_ladder_exit,distance=..2] run tellraw @s {"text":"✓ 出口梯子实体已生成","color":"green"}
execute in stardew:mine positioned 0 65 102 unless entity @e[type=interaction,tag=sd_mine_ladder_exit,distance=..2] run tellraw @s {"text":"✗ 出口梯子实体未生成!","color":"red"}

# 检查梯子方块
execute in stardew:mine if block 0 65 102 minecraft:ladder run tellraw @s {"text":"✓ 梯子方块已生成","color":"green"}
execute in stardew:mine unless block 0 65 102 minecraft:ladder run tellraw @s {"text":"✗ 梯子方块未生成!","color":"red"}

# 检查地面
execute in stardew:mine if block 0 64 110 minecraft:stone run tellraw @s {"text":"✓ 地面 (stone) 已生成","color":"green"}
execute in stardew:mine unless block 0 64 110 minecraft:stone run tellraw @s {"text":"✗ 地面未生成!","color":"red"}

# 检查矿物
execute store result score #stone_count sd_mine_temp in stardew:mine run execute if entity @e[type=interaction,tag=sd_mine_stone,x=-12,z=102,dx=24,dz=20]
tellraw @s [{"text":"矿物实体数量: ","color":"gray"},{"score":{"name":"#stone_count","objective":"sd_mine_temp"},"color":"aqua"}]

# 检查 barrier
execute store result score #barrier_count sd_mine_temp in stardew:mine positioned 0 65 112 run execute if block ~ ~ ~ minecraft:barrier
execute in stardew:mine positioned 0 65 112 if block ~ ~ ~ minecraft:barrier run tellraw @s {"text":"✓ 至少有一个 barrier (矿物碰撞体)","color":"green"}

# 传送到第一层查看
tellraw @s {"text":"传送到第一层...","color":"yellow"}
execute in stardew:mine run tp @s 0 65 103

tellraw @s {"text":"===== 测试完成 =====","color":"gold"}
