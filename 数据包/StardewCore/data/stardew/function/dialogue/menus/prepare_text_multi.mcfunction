# 多页模式：从 pages[当前页] 复制文本到 temp_text

# 获取当前页数
execute store result score #page sd_temp run data get storage stardew:dialogue current.dialogue_page

# 根据页数复制对应的文本（使用宏或者条件判断）
execute if score #page sd_temp matches 0 run data modify storage stardew:dialogue temp_text set from storage stardew:dialogue current.pages[0]
execute if score #page sd_temp matches 1 run data modify storage stardew:dialogue temp_text set from storage stardew:dialogue current.pages[1]
execute if score #page sd_temp matches 2 run data modify storage stardew:dialogue temp_text set from storage stardew:dialogue current.pages[2]
execute if score #page sd_temp matches 3 run data modify storage stardew:dialogue temp_text set from storage stardew:dialogue current.pages[3]
execute if score #page sd_temp matches 4 run data modify storage stardew:dialogue temp_text set from storage stardew:dialogue current.pages[4]
execute if score #page sd_temp matches 5 run data modify storage stardew:dialogue temp_text set from storage stardew:dialogue current.pages[5]
execute if score #page sd_temp matches 6 run data modify storage stardew:dialogue temp_text set from storage stardew:dialogue current.pages[6]
execute if score #page sd_temp matches 7 run data modify storage stardew:dialogue temp_text set from storage stardew:dialogue current.pages[7]
execute if score #page sd_temp matches 8 run data modify storage stardew:dialogue temp_text set from storage stardew:dialogue current.pages[8]
execute if score #page sd_temp matches 9 run data modify storage stardew:dialogue temp_text set from storage stardew:dialogue current.pages[9]
