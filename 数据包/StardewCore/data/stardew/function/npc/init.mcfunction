# NPC系统初始化
# 在主init函数中调用

# 创建NPC系统需要的计分板
scoreboard objectives add stardew_npc_id dummy "NPC ID"
scoreboard objectives add stardew_friendship dummy "友谊值"
scoreboard objectives add stardew_heart_level dummy "友谊心数等级"
scoreboard objectives add stardew_last_talk dummy "上次对话时间"
scoreboard objectives add stardew_daily_talk dummy "今日已对话"
scoreboard objectives add stardew_gift_given dummy "今日赠礼次数"
scoreboard objectives add stardew_animation_state dummy "动画状态"

# NPC ID分配 (每个NPC一个唯一ID)
# 1 = Alex, 2 = Abigail, 3 = Lewis, etc.

# 提示信息
tellraw @a[tag=debug] {"text":"[StardewCore] NPC系统已初始化","color":"green"}