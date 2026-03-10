# 取消悬停效果
# [执行者: item_display按钮]

# 恢复正常大小和关闭发光
data merge entity @s {start_interpolation:0,interpolation_duration:5,transformation:{scale:[0.5f,0.5f,0.5f]},Glowing:0b}
