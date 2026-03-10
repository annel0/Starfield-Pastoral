# data/stardew/function/utility/highlight/highlight_on.mcfunction
# 添加高亮效果 - 给实用设施的 item_display 添加灰色发光边框
# 执行者: 实用设施 item_display (@s)
# 执行位置: 实用设施位置

# 1. 给 item_display 添加发光效果 (使用 NBT 标签)
data merge entity @s {Glowing:1b,glow_color_override:8421504}

# 2. 播放音效 (可选,较轻的音效)
# execute at @s run playsound block.stone.hit block @a[distance=..8] ~ ~ ~ 0.3 2.0
