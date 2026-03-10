# 检查卸下发光戒指后是否还有其他发光戒指
# @s = 玩家
# 在unequip_ringX后调用(此时已经卸下了戒指且清除了storage)
# 前置条件: #this_ring_glow 已设置为卸下戒指的发光值

# 重新扫描剩余戒指的发光效果
scoreboard players set #remaining_glow stardew.temp 0
execute if score @s sd_equip_ring1 matches 1.. store result score #temp stardew.temp run data get storage stardew:equipment ring1.effects.glow 1
scoreboard players operation #remaining_glow stardew.temp += #temp stardew.temp
execute if score @s sd_equip_ring2 matches 1.. store result score #temp stardew.temp run data get storage stardew:equipment ring2.effects.glow 1
scoreboard players operation #remaining_glow stardew.temp += #temp stardew.temp
execute if score @s sd_equip_ring3 matches 1.. store result score #temp stardew.temp run data get storage stardew:equipment ring3.effects.glow 1
scoreboard players operation #remaining_glow stardew.temp += #temp stardew.temp
execute if score @s sd_equip_ring4 matches 1.. store result score #temp stardew.temp run data get storage stardew:equipment ring4.effects.glow 1
scoreboard players operation #remaining_glow stardew.temp += #temp stardew.temp

# 如果没有其他发光戒指了,清理光源
execute if score #remaining_glow stardew.temp matches 0 run function stardew:equipment/effects/rings/cleanup_nearby_lights
