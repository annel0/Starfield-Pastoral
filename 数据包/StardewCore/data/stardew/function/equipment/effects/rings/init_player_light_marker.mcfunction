# ================================================================
# 初始化玩家光源 marker
# ================================================================
# @s = 玩家
# 为玩家创建一个追踪 marker

# 召唤追踪 marker
summon marker ~ ~ ~ {Tags:["stardew.light_marker","stardew.light_marker.new"]}

# 绑定玩家 UUID
data modify entity @e[tag=stardew.light_marker.new,limit=1] data.owner_uuid set from entity @s UUID
tag @e[tag=stardew.light_marker.new,limit=1] remove stardew.light_marker.new

# 立即放置光源
function stardew:equipment/effects/rings/refresh_lights
