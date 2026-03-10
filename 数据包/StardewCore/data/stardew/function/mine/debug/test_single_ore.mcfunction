# stardew:mine/debug/test_single_ore.mcfunction
# 在矿洞内直接测试单个矿石生成
# 先进入矿洞，然后执行此命令

tellraw @s {"text":"===== 直接测试单个矿石 =====","color":"gold"}

# 设置层数
scoreboard players set @s sd_mine_floor 1

# 测试位置: X=0, Y=65, Z=110 (第1层中间位置)
tellraw @s {"text":"测试位置: X=0 Y=65 Z=110","color":"yellow"}

# 尝试生成 - 不做任何条件检查，直接调用
tellraw @s {"text":"直接调用 spawn_stone...","color":"yellow"}
execute in stardew:mine positioned 0 65 110 run function stardew:mine/ore/spawn_stone

tellraw @s [{"text":"生成后石头数量: ","color":"green"},{"score":{"name":"@s","objective":"sd_mine_stones"}}]
tellraw @s {"text":"===== 测试完成 =====","color":"gold"}
tellraw @s {"text":"请检查 X=0 Y=65 Z=110 位置是否有屏障和矿石模型","color":"aqua"}
