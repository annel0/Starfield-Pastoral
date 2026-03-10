# stardew:mine/floor/build_entrance_structure.mcfunction
# 生成大厅的建筑结构 (只在首次加载时调用)

# ===== 生成入口大厅 =====
# 地面 (15x15 石砖)
execute in stardew:mine run fill -7 64 -7 7 64 7 minecraft:stone_bricks

# 墙壁
execute in stardew:mine run fill -8 64 -8 8 68 8 minecraft:stone_bricks hollow
execute in stardew:mine run fill -7 65 -7 7 67 7 minecraft:air

# 天花板
execute in stardew:mine run fill -7 68 -7 7 68 7 minecraft:stone_bricks

# 添加一些光源
execute in stardew:mine run setblock 0 67 0 minecraft:lantern[hanging=true]
execute in stardew:mine run setblock -4 67 -4 minecraft:lantern[hanging=true]
execute in stardew:mine run setblock 4 67 -4 minecraft:lantern[hanging=true]
execute in stardew:mine run setblock -4 67 4 minecraft:lantern[hanging=true]
execute in stardew:mine run setblock 4 67 4 minecraft:lantern[hanging=true]

# ===== 生成电梯区域 (在入口侧面) =====
execute in stardew:mine run setblock -6 65 0 minecraft:air
execute in stardew:mine run setblock -6 66 0 minecraft:air

# ===== 生成下层入口 (洞口) =====
# 在房间另一侧开一个洞口通向第一层
execute in stardew:mine run fill 6 65 -1 7 66 1 minecraft:air

# 入口标识
execute in stardew:mine run setblock 5 67 0 minecraft:lantern[hanging=true]
