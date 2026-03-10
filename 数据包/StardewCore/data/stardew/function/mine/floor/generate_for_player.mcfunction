# stardew:mine/floor/generate_for_player.mcfunction
# 为找到的玩家生成房间
# 执行者: 匹配 UUID 的玩家 (@s)

# 检查玩家 UUID 是否匹配
execute store result score #uuid0 sd_temp run data get entity @s UUID[0]
execute store result score #uuid1 sd_temp run data get entity @s UUID[1]
execute store result score #uuid2 sd_temp run data get entity @s UUID[2]
execute store result score #uuid3 sd_temp run data get entity @s UUID[3]

execute store result score #stored_uuid0 sd_temp run data get storage stardew:mine gen.player_uuid[0]
execute store result score #stored_uuid1 sd_temp run data get storage stardew:mine gen.player_uuid[1]
execute store result score #stored_uuid2 sd_temp run data get storage stardew:mine gen.player_uuid[2]
execute store result score #stored_uuid3 sd_temp run data get storage stardew:mine gen.player_uuid[3]

# 如果 UUID 匹配，执行生成
execute if score #uuid0 sd_temp = #stored_uuid0 sd_temp if score #uuid1 sd_temp = #stored_uuid1 sd_temp if score #uuid2 sd_temp = #stored_uuid2 sd_temp if score #uuid3 sd_temp = #stored_uuid3 sd_temp run function stardew:mine/floor/do_generate
