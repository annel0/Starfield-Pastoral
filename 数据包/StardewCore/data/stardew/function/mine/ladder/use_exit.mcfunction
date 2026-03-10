# stardew:mine/ladder/use_exit.mcfunction
# 使用出口梯子返回 0 层
# 执行者: 玩家 (@s)

# 播放音效
playsound minecraft:block.ladder.step master @s ~ ~ ~ 1 1
playsound minecraft:entity.enderman.teleport master @s ~ ~ ~ 0.5 1.2

# 显示消息
tellraw @s {"text":"[矿洞] 你通过梯子返回了入口...","color":"green"}

# 设置层数为 0
scoreboard players set @s sd_mine_floor 0

# 传送到 0 层
tp @s 0 65 0

# 显示层数
title @s subtitle {"text":"矿洞入口","color":"gray"}
title @s title {"text":"第 0 层","color":"gold"}
