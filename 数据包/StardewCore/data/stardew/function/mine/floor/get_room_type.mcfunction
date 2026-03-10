# stardew:mine/floor/get_room_type.mcfunction
# 获取楼层的房间类型和主题
# 参数: $(floor)
# 输出: #room_type sd_mine_temp, #theme sd_mine_temp

# 默认房间类型为 1，主题根据楼层计算
scoreboard players set #room_type sd_mine_temp 1
scoreboard players set #theme sd_mine_temp 1

# 尝试读取存储的房间类型
$execute store success score #has_type sd_mine_temp run data get storage stardew:mine room_types.$(floor)
$execute if score #has_type sd_mine_temp matches 1 store result score #room_type sd_mine_temp run data get storage stardew:mine room_types.$(floor)

# 尝试读取存储的主题
$execute store success score #has_theme sd_mine_temp run data get storage stardew:mine themes.$(floor)
$execute if score #has_theme sd_mine_temp matches 1 store result score #theme sd_mine_temp run data get storage stardew:mine themes.$(floor)
