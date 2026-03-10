# stardew:mine/floor/clear_area.mcfunction
# 清空整个楼层区域 (使用最大房间尺寸 50x5x50)
# 参数: $(z), $(z50) - 房间基础 Z 坐标和结束 Z 坐标
# 确保无论之前是什么房间类型，都能完全清空

# 清空范围:
# X: 0 ~ 50 (覆盖所有房间类型，包括theme4/room5的50x50)
# Y: 64 ~ 68 (5格高度)
# Z: z ~ z+50 (覆盖最大长度)

# 清空方块
$execute in stardew:mine run fill 0 64 $(z) 50 68 $(z50) minecraft:air replace

# 清空该区域的所有矿洞相关实体
# 直接使用 gen.z 和 gen.z50 参数
function stardew:mine/floor/clear_entities with storage stardew:mine gen
