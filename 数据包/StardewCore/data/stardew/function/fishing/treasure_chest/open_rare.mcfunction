# data/stardew/functions/fishing/treasure_chest/open_rare.mcfunction
# 打开稀有钓鱼宝箱
# 蓝色主题 - 使用 scrape 粒子 + 铁门音效

# 消耗宝箱（使用物品修改器）
item modify entity @s weapon.mainhand stardew:consume_one

# 声音效果 - 开箱音效
execute at @s run playsound minecraft:block.iron_door.open master @a[distance=..20] ~ ~ ~ 1.0 1.5
execute at @s run playsound minecraft:block.chest.open master @a[distance=..20] ~ ~ ~ 1.0 1.4

# 给玩家标签
tag @s add sd_treasure_opening
tag @s add sd_treasure_rare

# 先生成战利品到副手
loot replace entity @s weapon.offhand loot stardew:fishing/treasure_rare

# 存储战利品数据到storage
data modify storage stardew:treasure temp_item set from entity @s Inventory[{Slot:-106b}]

# 立即清空副手
item replace entity @s weapon.offhand with air

# 播放动画（从storage读取CMD）
function stardew:fishing/treasure_chest/display_animation_start_with_storage



