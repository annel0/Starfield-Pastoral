# 初始化阿比盖尔的NPC数据
# @s = 阿比盖尔村民实体

# 加载礼物偏好数据
function stardew:npc/abigail/data/gifts

# 加载位置数据
function stardew:npc/abigail/data/locations

# 初始化位置相关scoreboard
# 设置为1（在家），因为spawn时就在家里
scoreboard players set @s stardew.npc.schedule 1

# 初始化维度为主世界（未来扩展多维度支持）
scoreboard players set @s stardew.npc.dimension 0
