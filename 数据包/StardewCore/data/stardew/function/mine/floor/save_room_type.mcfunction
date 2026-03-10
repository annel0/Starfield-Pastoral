# stardew:mine/floor/save_room_type.mcfunction
# 保存楼层的房间类型和主题
# 参数: $(floor), $(room_type), $(theme)

$data modify storage stardew:mine room_types.$(floor) set value $(room_type)
$data modify storage stardew:mine themes.$(floor) set value $(theme)
