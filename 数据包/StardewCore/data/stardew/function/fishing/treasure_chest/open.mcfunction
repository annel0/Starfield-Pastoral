# data/stardew/functions/fishing/treasure_chest/open.mcfunction
# 右键打开钓鱼宝箱
# 检测玩家手持的宝箱类型，播放动画并给予战利品

# 检测宝箱类型并调用对应的开启函数
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_data~{treasure_tier:"common"}] run function stardew:fishing/treasure_chest/open_common
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_data~{treasure_tier:"rare"}] run function stardew:fishing/treasure_chest/open_rare
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_data~{treasure_tier:"epic"}] run function stardew:fishing/treasure_chest/open_epic

# 重置右键检测
scoreboard players set @s sd_right_click 0
