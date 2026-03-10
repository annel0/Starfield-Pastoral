# ================================================================
# 移除 marker 和光源
# ================================================================
# @s = light_marker
# 比较 UUID,如果匹配则清理

# 检查 UUID 是否匹配
execute store success score #uuid_match stardew.temp run data modify storage stardew:temp cleanup_uuid set from entity @s data.owner_uuid

# UUID 匹配 (score=0),清理该 marker 的光源
execute if score #uuid_match stardew.temp matches 0 run function stardew:equipment/effects/rings/clear_old_position
execute if score #uuid_match stardew.temp matches 0 run kill @s
