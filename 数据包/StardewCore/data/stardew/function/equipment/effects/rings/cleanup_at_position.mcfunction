# ================================================================
# 在指定位置清理光源
# ================================================================
# 使用宏参数指定位置
# $old_x $old_y $old_z

# 清理脚下的光源
$execute positioned $(old_x) $(old_y) $(old_z) align xyz positioned ~0.5 ~ ~0.5 if block ~ ~ ~ minecraft:light run setblock ~ ~ ~ air replace

# 清理身体位置的光源
$execute positioned $(old_x) $(old_y) $(old_z) align xyz positioned ~0.5 ~1 ~0.5 if block ~ ~ ~ minecraft:light run setblock ~ ~ ~ air replace
