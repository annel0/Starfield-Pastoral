# stardew:mine/debug/test_spawn_ore.mcfunction
# 测试矿石生成
# 执行者: 玩家

tellraw @s {"text":"[DEBUG] 测试矿石生成...","color":"yellow"}

# 设置玩家层数为1（让 spawn_random_ore 能获取层数）
scoreboard players set @s sd_mine_floor 1

# 使用玩家脚下位置 (feet position)，然后向前移动
# at @s 默认就是脚的位置

# 先在玩家前方 2 格、脚下 1 格放一个石头地面作为基础
execute at @s positioned ~2 ~-1 ~ run setblock ~ ~ ~ minecraft:stone

# 在玩家前方 2 格、脚的高度生成矿石
execute at @s positioned ~2 ~ ~ run function stardew:mine/ore/spawn_random_ore

tellraw @s {"text":"[DEBUG] 矿石生成完成","color":"green"}
