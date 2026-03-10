# data/stardew/functions/farming/fertilizer/copy_to_new_crop.mcfunction
# 将肥料数据复制到新种植的作物

# 找到同位置的肥料marker并复制数据到作物 (作物和肥料都在~1.375)
execute as @e[type=marker,tag=sd_fertilizer_marker,distance=..0.1,limit=1] run function stardew:farming/fertilizer/transfer_data_to_crop
