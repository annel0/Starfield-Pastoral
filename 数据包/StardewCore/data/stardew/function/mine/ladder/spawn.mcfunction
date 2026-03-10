# stardew:mine/ladder/spawn.mcfunction
# 生成下层梯子 (挖矿随机出现，通往下一层)
# 执行位置: 石头被破坏的位置 (执行者是石头 interaction 实体)

# 检查是否已经生成过下层梯子
execute if score @p sd_mine_ladder matches 1 run return 0

# 标记下层梯子已生成
scoreboard players set @p sd_mine_ladder 1

# 保存当前坐标到 storage (因为 @s 是石头实体，在正确位置)
# 注意: 使用 x, y, z 作为键名，这样宏函数可以用 $(x), $(y), $(z) 访问
execute store result storage stardew:mine ladder_pos.x int 1 run data get entity @s Pos[0]
execute store result storage stardew:mine ladder_pos.y int 1 run data get entity @s Pos[1]
execute store result storage stardew:mine ladder_pos.z int 1 run data get entity @s Pos[2]

# 调用实际生成函数 (使用保存的坐标)
function stardew:mine/ladder/spawn_impl with storage stardew:mine ladder_pos

# 播放音效 (在当前位置)
playsound minecraft:block.gravel.break master @a ~ ~ ~ 1 0.8
playsound minecraft:entity.player.levelup master @a ~ ~ ~ 0.5 1.5

# 显示发现坑的消息
tellraw @a[distance=..20] {"text":"[矿洞] 发现了通往下一层的坑！","color":"yellow"}
