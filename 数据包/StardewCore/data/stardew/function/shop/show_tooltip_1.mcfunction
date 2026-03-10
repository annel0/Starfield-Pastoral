# 显示槽位1的tooltip
# 先清除旧的tooltip
kill @e[type=text_display,tag=shop_tooltip]

# 读取当前槽位1的物品数据
data modify storage stardew:temp tooltip_data set from storage stardew:temp current_page[0]

# 生成tooltip text_display
# 位置在物品图标右侧，使用与UI相同的rotation
execute in stardew:interiors run summon text_display 1.8 66.4214 8.2 {billboard:"fixed",Tags:["shop_ui","shop_tooltip"],transformation:{left_rotation:[0f,0.156f,0f,0.988f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.7f,0.7f,0.7f]},text:'{"text":""}',alignment:"left",background:1275068416}

# 更新tooltip内容
function stardew:shop/update_tooltip
