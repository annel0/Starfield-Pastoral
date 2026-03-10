# stardew:mine/debug/force_generate_entrance.mcfunction
# 调试: 强制重新生成 0 层入口

# 先强制加载区块
execute in stardew:mine run forceload add -16 -16 16 16

# 先在传送点放一个临时方块，防止玩家掉落
execute in stardew:mine run setblock 0 64 0 minecraft:stone

# 传送玩家到入口
execute in stardew:mine run tp @s 0 65 0

# 延迟执行生成大厅 (1tick 后)
schedule function stardew:mine/debug/generate_entrance_delayed 1t

tellraw @s {"text":"[调试] 正在生成0层入口...","color":"yellow"}
