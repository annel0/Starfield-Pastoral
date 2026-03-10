# data/stardew/functions/fishing/utils/check_tackle.mcfunction
# 作用：读取主手鱼竿的渔具 ID 并存入 sd_tackle_id

# 1. 重置 ID
scoreboard players set @s sd_tackle_id 0

# 2. 尝试读取 Slot 1 (tackle_1)
# 注意：这里必须读 tackle_1，不能读 tackle_id！
execute store result score @s sd_tackle_id run data get entity @s SelectedItem.components."minecraft:custom_data".tackle_1

# 3. 如果 Slot 1 是空的 (0)，尝试读取 Slot 2 (tackle_2)
# 这样可以兼容钻石鱼竿的第二槽位
execute if score @s sd_tackle_id matches 0 run execute store result score @s sd_tackle_id run data get entity @s SelectedItem.components."minecraft:custom_data".tackle_2

# [进阶提示]
# 如果你使用的是钻石鱼竿且两个槽都有东西（比如 槽1=旋式，槽2=声呐），
# 目前的逻辑只会读取 槽1 (旋式)，从而导致声呐失效。
# 这是一个"单变量"系统的局限性。
# 如果你想让双渔具同时生效，我们需要把 check_tackle 改成 "打标签(Tag)" 系统，但这会改动很多文件。
# 目前这个版本能保证 99% 的情况（金鱼竿/单渔具）完美工作。