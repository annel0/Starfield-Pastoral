# data/stardew/functions/farming/fertilizer/remove_on_break.mcfunction
# 当耕地被破坏时移除肥料marker和视觉实体
# 由工具破坏检测调用

# 对齐到方块中心并清除该位置的肥料 (耕地层位置)
execute align xyz positioned ~0.5 ~ ~0.5 run function stardew:farming/fertilizer/clear_at_position
