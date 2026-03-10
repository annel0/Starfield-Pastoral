# 阿比盖尔地点数据
# 坐标格式: {pos:[x,y,z], dimension:"维度ID", facing:"朝向"}

# 室内地点（将来在独立维度实现）
data modify storage stardew:locations abigail_room set value {pos:[73.5d,-54.0d,130.5d],dimension:"minecraft:overworld",facing:"south",interior:true}
data modify storage stardew:locations store_counter set value {pos:[73.5d,-54.0d,130.5d],dimension:"minecraft:overworld",facing:"west",interior:true}
data modify storage stardew:locations store_kitchen set value {pos:[73.5d,-54.0d,130.5d],dimension:"minecraft:overworld",facing:"north",interior:true}

# 室外地点
data modify storage stardew:locations town_square set value {pos:[90.5d,-54.0d,111.5d],dimension:"minecraft:overworld",facing:"south",interior:false}
data modify storage stardew:locations graveyard set value {pos:[66.5d,-54.0d,46.5d],dimension:"minecraft:overworld",facing:"east",interior:false}
data modify storage stardew:locations carpenter_shop set value {pos:[3.5d,-42.0d,279.5d],dimension:"minecraft:overworld",facing:"south",interior:false}
data modify storage stardew:locations mountain_lake set value {pos:[-27.5d,-42.0d,247.5d],dimension:"minecraft:overworld",facing:"west",interior:false}
data modify storage stardew:locations saloon_inside set value {pos:[66.5d,-52.0d,100.5d],dimension:"minecraft:overworld",facing:"north",interior:true}
data modify storage stardew:locations saloon_arcade set value {pos:[66.5d,-52.0d,100.5d],dimension:"minecraft:overworld",facing:"east",interior:true}
