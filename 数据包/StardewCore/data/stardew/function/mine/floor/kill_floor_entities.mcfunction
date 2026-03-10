# stardew:mine/floor/kill_floor_entities.mcfunction
# 宏函数：清理整层的所有实体
# 参数: $(z15) - 房间中心Z坐标
# 在 schedule 延迟后调用，此时区块已完全加载

# 清理所有矿洞相关实体：
# - 矿石：interaction（交互实体）+ item_display（视觉实体）
# - 怪物：所有 sd_monster 标签的生物
# - 掉落物：item 类型
# - 其他矿洞实体：sd_mine_entity 标签（如文本显示等）

# 清理矿石（interaction + item_display）
$execute in stardew:mine positioned 15 65 $(z15) run kill @e[type=interaction,tag=sd_stone,distance=..60]
$execute in stardew:mine positioned 15 65 $(z15) run kill @e[type=item_display,tag=sd_stone_display,distance=..60]

# 清理怪物
$execute in stardew:mine positioned 15 65 $(z15) run kill @e[tag=sd_monster,distance=..60]

# 清理掉落物
$execute in stardew:mine positioned 15 65 $(z15) run kill @e[type=item,distance=..60]

# 清理其他矿洞实体（文本显示等）
$execute in stardew:mine positioned 15 65 $(z15) run kill @e[tag=sd_mine_entity,distance=..60]
