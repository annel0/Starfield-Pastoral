# stardew:debug/rebuild_entrance.mcfunction
# 强制重新生成矿洞入口（用于修复问题）
# 使用方法: /function stardew:debug/rebuild_entrance

tellraw @s {"text":"========================================","color":"yellow"}
tellraw @s {"text":"正在强制重建矿洞入口...","color":"yellow"}

# 清除旧实体
execute in stardew:mine positioned 0 65 0 run kill @e[tag=sd_mine_lobby_entity,distance=..30]

# 清除旧方块结构（强制重建）
execute in stardew:mine run fill -10 63 -10 10 70 10 minecraft:air

# 重新生成结构
execute in stardew:mine run function stardew:mine/floor/build_entrance_structure

# 等待一tick后生成实体
schedule function stardew:debug/rebuild_entrance_entities 1t

tellraw @s {"text":"✓ 入口结构已重建！","color":"green"}
tellraw @s {"text":"========================================","color":"yellow"}
