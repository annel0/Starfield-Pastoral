# NPC系统初始化

# 创建计分板
scoreboard objectives add stardew.npc.id dummy "NPC ID"
scoreboard objectives add stardew.interact.type dummy "交互类型"

# 每个NPC独立的友谊值（避免互相干扰）
scoreboard objectives add stardew.friendship.abigail dummy "阿比盖尔友谊值"
# 未来添加更多NPC时在这里添加：
# scoreboard objectives add stardew.friendship.emily dummy "艾米丽友谊值"
# scoreboard objectives add stardew.friendship.haley dummy "海莉友谊值"
# scoreboard objectives add stardew.friendship.sebastian dummy "塞巴斯蒂安友谊值"

# 每个NPC一个对话标记（避免互相干扰）
scoreboard objectives add stardew.talked.abigail dummy "今日已与阿比盖尔对话"
# 未来添加更多NPC时在这里添加：
# scoreboard objectives add stardew.talked.emily dummy "今日已与艾米丽对话"
# scoreboard objectives add stardew.talked.haley dummy "今日已与海莉对话"

# 每个NPC一个送礼计数（每周每人2次）
scoreboard objectives add stardew.gifted.abigail dummy "本周已送礼给阿比盖尔"
# 未来添加更多NPC时在这里添加：
# scoreboard objectives add stardew.gifted.emily dummy "本周已送礼给艾米丽"
# scoreboard objectives add stardew.gifted.haley dummy "本周已送礼给海莉"

scoreboard objectives add stardew.animation dummy "动画状态"
scoreboard objectives add stardew.npc.schedule dummy "NPC日程"
scoreboard objectives add stardew.npc.target_schedule dummy "NPC目标日程"
scoreboard objectives add stardew.npc.dimension dummy "NPC当前维度"
scoreboard objectives add stardew.npc.pos_x dummy "NPC X坐标"
scoreboard objectives add stardew.npc.pos_y dummy "NPC Y坐标"
scoreboard objectives add stardew.npc.pos_z dummy "NPC Z坐标"
scoreboard objectives add stardew.npc.last_x dummy "NPC上一帧X"
scoreboard objectives add stardew.npc.last_z dummy "NPC上一帧Z"

# 路径移动系统
scoreboard objectives add stardew.npc.path_id dummy "当前路径ID"
scoreboard objectives add stardew.npc.path_index dummy "路径点索引"
scoreboard objectives add stardew.npc.target_x dummy "目标X坐标(*10)"
scoreboard objectives add stardew.npc.target_y dummy "目标Y坐标(*10)"
scoreboard objectives add stardew.npc.target_z dummy "目标Z坐标(*10)"
scoreboard objectives add stardew.npc.target_yaw dummy "目标朝向(度)"
scoreboard objectives add stardew.npc.idle_yaw dummy "待机朝向(度)"

# 多NPC并发支持：每个NPC的临时计算变量
scoreboard objectives add stardew.npc.calc_1 dummy "NPC临时计算1"
scoreboard objectives add stardew.npc.calc_2 dummy "NPC临时计算2"

# 强制加载区块追踪
scoreboard objectives add stardew.npc.chunk_x dummy "NPC区块X"
scoreboard objectives add stardew.npc.chunk_z dummy "NPC区块Z"

# 常量
scoreboard objectives add stardew.const dummy "常量"
scoreboard players set #250 stardew.const 250
scoreboard players set #20 stardew.const 20
scoreboard players set #45 stardew.const 45
scoreboard players set #80 stardew.const 80
scoreboard players set #-20 stardew.const -20
scoreboard players set #-40 stardew.const -40
scoreboard players set #2 stardew.const 2
scoreboard players set #6 stardew.const 6
scoreboard players set #8 stardew.const 8

# 加载NPC系统常量（维度ID、地点ID等）
function stardew:npc/constants

# 临时变量（仅用于不涉及多NPC并发的场景）
scoreboard objectives add stardew.temp dummy "临时变量"

tellraw @a[tag=debug] {"text":"[StardewCraft] NPC系统已初始化","color":"green"}