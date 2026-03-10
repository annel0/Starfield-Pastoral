# 处理玩家与阿比盖尔的交互
# @s = 交互的玩家

# 找到最近的interaction实体并获取交互数据
execute as @e[type=interaction,tag=npc.abigail.interaction,limit=1,sort=nearest] run data modify storage stardew:temp interaction set from entity @s

# 判断交互类型：
# 1. attack存在 = shift+右键 = 送礼
# 2. interaction存在 = 右键 = 对话

# 检测是否是攻击交互（送礼）
execute if data storage stardew:temp interaction.attack[0] run function stardew:npc/abigail/interact/gift

# 检测是否是普通交互（对话）
execute if data storage stardew:temp interaction.interaction[0] unless data storage stardew:temp interaction.attack[0] run function stardew:npc/abigail/interact/talk

# 清理interaction实体的数据
execute as @e[type=interaction,tag=npc.abigail.interaction,limit=1,sort=nearest] run data remove entity @s attack
execute as @e[type=interaction,tag=npc.abigail.interaction,limit=1,sort=nearest] run data remove entity @s interaction

# 清理临时存储
data remove storage stardew:temp interaction