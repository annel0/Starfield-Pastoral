# ================================================================
# 清理玩家的旧光源方块
# ================================================================
# @s = 玩家
# 移除该玩家之前放置的所有 light block

# 保存玩家 UUID 到 storage
data modify storage stardew:temp cleanup_uuid set from entity @s UUID

# 找到所有属于该玩家的 light block marker 并清理
execute as @e[tag=stardew.light_block] if data entity @s {data:{owner_uuid:[]}} run function stardew:equipment/effects/rings/check_and_remove_light
