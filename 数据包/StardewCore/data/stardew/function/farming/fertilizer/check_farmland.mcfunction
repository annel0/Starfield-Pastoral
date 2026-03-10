# data/stardew/functions/farming/fertilizer/check_farmland.mcfunction
# 检查耕地上是否有作物和肥料标记

# 对齐到方块中心 (耕地层是~,作物在~1)
execute align xyz positioned ~0.5 ~1 ~0.5 run function stardew:farming/fertilizer/check_crop_marker
