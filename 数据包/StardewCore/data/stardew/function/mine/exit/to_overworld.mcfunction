# stardew:mine/exit/to_overworld.mcfunction
# 返回主世界
# 执行者: 玩家 (@s)

# 播放回到地面音效
playsound minecraft:entity.enderman.teleport master @s
playsound minecraft:block.portal.trigger master @s

# 传送回主世界矿洞入口 (朝向正北)
execute in minecraft:overworld run tp @s -81 -42 344 180 0

# 显示消息
title @s subtitle {"text":"","color":"gray"}
title @s title {"text":"返回地面","color":"green"}

tellraw @s {"text":"[矿洞] 你离开了矿洞。","color":"green"}
