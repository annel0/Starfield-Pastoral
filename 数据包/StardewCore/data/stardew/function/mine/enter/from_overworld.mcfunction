# stardew:mine/enter/from_overworld.mcfunction
# 从主世界进入矿洞
# 执行者: 玩家 (@s)

# 清除最后一石高亮标签（重新进入矿洞）
tag @s remove sd_mine_last_stone

# 播放进入矿井音效
playsound minecraft:block.portal.travel master @s
playsound minecraft:block.stone.break master @s

# 设置玩家初始层数为 0
scoreboard players set @s sd_mine_floor 0

# 如果玩家没有最深记录，初始化为 0
execute unless score @s sd_mine_deepest matches 0.. run scoreboard players set @s sd_mine_deepest 0

# 传送到矿洞 0 层入口
execute in stardew:mine run tp @s 0 65 0

# 显示欢迎信息
title @s subtitle {"text":"矿洞入口","color":"gray"}
title @s title {"text":"第 0 层","color":"gold"}

tellraw @s {"text":"[矿洞] 欢迎来到矿洞！走进洞口进入第 1 层。","color":"yellow"}
