# data/stardew/function/mining/highlight/highlight_off.mcfunction
# 移除高亮效果
# 执行者: 矿石 item_display (@s)
# 执行位置: 矿石位置

# 1. 移除发光效果
data merge entity @s {Glowing:0b}
data remove entity @s glow_color_override
