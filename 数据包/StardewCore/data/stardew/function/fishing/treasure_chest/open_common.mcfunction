# data/stardew/functions/fishing/treasure_chest/open_common.mcfunction
# 打开普通钓鱼宝箱
# 绿色主题 - 使用 happy_villager 粒子 + 木头音效

# 消耗宝箱（使用物品修改器）
item modify entity @s weapon.mainhand stardew:consume_one

# 声音效果 - 开箱音效
execute at @s run playsound minecraft:block.wood.place master @a[distance=..20] ~ ~ ~ 1.0 1.0
execute at @s run playsound minecraft:block.chest.open master @a[distance=..20] ~ ~ ~ 0.8 1.2

# 给玩家标签
tag @s add sd_treasure_opening
tag @s add sd_treasure_common

# 先生成战利品到副手
loot replace entity @s weapon.offhand loot stardew:fishing/treasure_common

# 存储战利品数据到storage（用于后续动画和显示）
data modify storage stardew:treasure temp_item set from entity @s Inventory[{Slot:-106b}]

# 立即清空副手
item replace entity @s weapon.offhand with air

# 播放动画（从storage读取CMD）
function stardew:fishing/treasure_chest/display_animation_start_with_storage




