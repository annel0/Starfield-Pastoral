# 准备当前页的文本到 temp_text
# 支持两种模式：
# 1. 多页模式：current.pages[当前页] -> temp_text
# 2. 单页模式：current.text -> temp_text（兼容旧版）

# 清空临时文本
data remove storage stardew:dialogue temp_text

# 如果有 pages 数据（多页模式）
execute if data storage stardew:dialogue current.pages run function stardew:dialogue/menus/prepare_text_multi

# 如果只有 text 数据（单页模式，兼容）
execute unless data storage stardew:dialogue current.pages if data storage stardew:dialogue current.text run data modify storage stardew:dialogue temp_text set from storage stardew:dialogue current.text
