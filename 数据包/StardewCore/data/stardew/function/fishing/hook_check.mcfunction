# data/stardew/functions/fishing/hook_check.mcfunction
# 先当空壳，后面如果你想加“鱼挣扎移动”的高级逻辑，再往里填

# 如果玩家已经在战斗中，这里可以顺手让鱼钩动一动（可选）
# 暂时先不做任何事，防止干扰主逻辑
# data/stardew/functions/fishing/hook_check.mcfunction
# 执行者：鱼钩 (fishing_bobber)

# 只有在“咬钩阶段”才抖动
execute unless entity @s[tag=sd_bite_hook] run return 0

# 附近有没有还在“咬钩反应时间”里的玩家？没有就取消标记
execute unless entity @e[type=player,distance=..32,limit=1,scores={sd_bite_react=1..}] run tag @s remove sd_bite_hook
execute unless entity @s[tag=sd_bite_hook] run return 0

# 计数器 +1，用来做简单的上下循环
scoreboard players add @s sd_bite_anim 1
execute if score @s sd_bite_anim matches 4.. run scoreboard players set @s sd_bite_anim 0

execute if score @s sd_bite_anim matches 0..1 run tp @s ~ ~0.03 ~
execute if score @s sd_bite_anim matches 2..3 run tp @s ~ ~-0.03 ~
