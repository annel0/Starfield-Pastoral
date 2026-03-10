# stardew:debug/summon_mine_entrance.mcfunction
# 快速进入矿洞系统（用于测试）
# 使用方法: /function stardew:debug/summon_mine_entrance

# 提示玩家
tellraw @s {"text":"========================================","color":"aqua"}
tellraw @s {"text":"正在初始化矿洞系统...","color":"yellow"}

# 确保矿洞系统已初始化
function stardew:mine/init

# 传送玩家到矿洞入口（0层）
function stardew:mine/enter/from_overworld

tellraw @s {"text":"✓ 已传送到矿洞入口（第0层）","color":"green"}
tellraw @s [{"text":"提示: ","color":"gray"},{"text":"走进洞口进入第1层，或右键电梯传送到其他层","color":"white"}]
tellraw @s {"text":"========================================","color":"aqua"}
