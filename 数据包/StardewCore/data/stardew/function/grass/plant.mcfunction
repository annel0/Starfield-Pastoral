# 种植草籽
# 在目标位置立即生长出草，并将下方土壤变为草方块

# 1. 前置检查 (防堆叠)
execute align xyz positioned ~0.5 ~1.0 ~0.5 if entity @e[type=minecraft:interaction,tag=sd_grass,distance=..0.5] run tellraw @s {"text":"这里已经有草了！","color":"red"}
execute align xyz positioned ~0.5 ~1.0 ~0.5 if entity @e[type=minecraft:interaction,tag=sd_grass,distance=..0.5] run return 1

# 2. 季节判断 (草可以在春夏秋种植)
# Global sd_season 1=春, 2=夏, 3=秋, 4=冬
execute if score Global sd_season matches 4 run tellraw @s {"text":"草不能在冬季种植","color":"red"}
execute if score Global sd_season matches 4 run return 1

# 3. 检查是否在合适的地面上（土壤类型）
execute unless block ~ ~ ~ #minecraft:dirt unless block ~ ~ ~ minecraft:grass_block unless block ~ ~ ~ minecraft:coarse_dirt unless block ~ ~ ~ minecraft:podzol run tellraw @s {"text":"草只能种植在土壤上","color":"red"}
execute unless block ~ ~ ~ #minecraft:dirt unless block ~ ~ ~ minecraft:grass_block unless block ~ ~ ~ minecraft:coarse_dirt unless block ~ ~ ~ minecraft:podzol run return 1

# 4. 检查上方是否有空间
execute unless block ~ ~1 ~ #minecraft:air run tellraw @s {"text":"此位置上方空间不足，无法种植草","color":"red"}
execute unless block ~ ~1 ~ #minecraft:air run return 1

# 5. 将下方土壤变为草方块
function stardew:grass/convert_to_grass_block

# 6. 种植 (立即生成草)
execute align xyz positioned ~0.5 ~1 ~0.5 run function stardew:grass/spawn_random_grass

# 7. 反馈特效
playsound minecraft:item.hoe.till block @a ~ ~ ~ 1 1.2
particle minecraft:happy_villager ~ ~0.5 ~ 0.3 0.3 0.3 0 8

# 8. 消耗种子（创造模式不消耗）
execute unless entity @s[gamemode=creative] run item modify entity @s weapon.mainhand stardew:consume_one

# 9. 成功提示
