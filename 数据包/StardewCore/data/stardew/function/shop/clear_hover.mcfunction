# 清除槽位的高光效果
# [执行者: item_icon]

# 恢复正常大小和取消发光（只改scale，保持rotation）
data merge entity @s {start_interpolation:0,interpolation_duration:5,transformation:{scale:[0.2f,0.2f,0.1f]},Glowing:0b}
