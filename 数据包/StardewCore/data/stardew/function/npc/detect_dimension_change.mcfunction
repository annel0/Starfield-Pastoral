# 玩家维度检测系统
# 检测玩家是否刚从其他维度返回主世界

# 标记当前在主世界的玩家
execute as @a[predicate=stardew:in_overworld] run tag @s add in_overworld_now

# 如果之前不在主世界,现在在主世界 = 刚返回
execute as @a[tag=in_overworld_now,tag=!was_in_overworld] run function stardew:npc/sync_on_return

# 更新标记
tag @a[tag=in_overworld_now] add was_in_overworld
tag @a[tag=!in_overworld_now] remove was_in_overworld
tag @a remove in_overworld_now
