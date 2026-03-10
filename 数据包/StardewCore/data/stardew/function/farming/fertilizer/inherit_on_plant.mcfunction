# data/stardew/functions/farming/fertilizer/inherit_on_plant.mcfunction
# 种植新作物时检查是否有肥料,并继承肥料效果
# 由 crops/planting/*/plant.mcfunction 调用

# 检查当前位置是否有肥料marker (作物和肥料在同一Y坐标~1.375)
execute if entity @e[type=marker,tag=sd_fertilizer_marker,distance=..0.1,limit=1] run function stardew:farming/fertilizer/copy_to_new_crop
