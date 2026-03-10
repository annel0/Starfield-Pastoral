# stardew:mine/debug/test_spawn_verbose.mcfunction
# 详细调试矿石生成
# 执行者: 玩家

tellraw @s {"text":"===== 开始详细调试 =====","color":"gold"}

# 显示玩家当前位置
execute at @s run tellraw @s [{"text":"玩家位置: ","color":"gray"},{"text":"X="},{"score":{"name":"@s","objective":"sd_mine_temp"}},{"text":" (实际用 /data get)"}]

# 设置层数
scoreboard players set @s sd_mine_floor 1
tellraw @s [{"text":"设置 sd_mine_floor = 1","color":"yellow"}]

# 测试1: 直接在玩家位置放屏障
tellraw @s {"text":"测试1: 直接在当前位置放屏障","color":"aqua"}
execute at @s align xyz run setblock ~ ~ ~ minecraft:glass
execute at @s align xyz run tellraw @s {"text":"  -> align xyz 后 setblock ~ ~ ~ glass","color":"white"}

# 测试2: 在玩家位置 +0.5 放屏障
tellraw @s {"text":"测试2: positioned ~0.5 ~ ~0.5 后放屏障","color":"aqua"}
execute at @s align xyz positioned ~0.5 ~ ~0.5 run setblock ~ ~ ~ minecraft:barrier
execute at @s align xyz positioned ~0.5 ~ ~0.5 run tellraw @s {"text":"  -> setblock ~ ~ ~ barrier (应该和glass同位置)","color":"white"}

tellraw @s {"text":"===== 调试完成 =====","color":"gold"}
tellraw @s {"text":"检查: 玻璃和屏障应该在同一个方块位置","color":"green"}
