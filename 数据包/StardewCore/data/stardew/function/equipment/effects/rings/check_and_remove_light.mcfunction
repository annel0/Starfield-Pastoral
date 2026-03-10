# ================================================================
# 检查并移除光源方块
# ================================================================
# @s = light_block marker
# 比较 UUID 并移除匹配的光源

# 将 marker 的 owner_uuid 与 storage 中的 cleanup_uuid 比较
execute store success score #uuid_match stardew.temp run data modify storage stardew:temp cleanup_uuid set from entity @s data.owner_uuid

# 如果 UUID 不匹配 (score=0),说明是该玩家的光源,移除它
execute if score #uuid_match stardew.temp matches 0 at @s run function stardew:equipment/effects/rings/remove_single_light_block
