# stardew:mine/ladder/use_down.mcfunction
# 使用下层梯子进入下一层
# 执行者: 玩家 (@s)

# 播放音效
playsound minecraft:block.ladder.step master @s
playsound minecraft:entity.enderman.teleport master @s

# 显示消息
tellraw @s {"text":"[矿洞] 你沿着梯子下到了更深处...","color":"yellow"}

# 调用进入下一层函数
function stardew:mine/enter/next_floor
