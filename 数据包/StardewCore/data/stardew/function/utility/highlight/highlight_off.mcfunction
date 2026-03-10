# data/stardew/function/utility/highlight/highlight_off.mcfunction
# 移除高亮效果
# 执行者: 实用设施 item_display (@s)
# 执行位置: 实用设施位置

# 1. 移除发光效果
data merge entity @s {Glowing:0b}
data remove entity @s glow_color_override
