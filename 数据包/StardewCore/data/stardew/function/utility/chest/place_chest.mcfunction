# data/stardew/function/utility/chest/place_chest.mcfunction
# 在射线击中的位置放置箱子
# 执行位置：射线击中的方块表面

# 0. 获取玩家朝向并计算旋转角度
execute as @a[tag=sd_placing_chest,limit=1] run function stardew:utility/get_rotation

# 1. 检查上方两格是否都是空气（允许在箱子上面叠箱子，但箱子上面需要有空间）
execute align xyz positioned ~ ~1 ~ unless block ~ ~ ~ #minecraft:air unless block ~ ~ ~ minecraft:barrier run tellraw @a[tag=sd_placing_chest,limit=1] {"text":"上方空间不足！","color":"red"}
execute align xyz positioned ~ ~1 ~ unless block ~ ~ ~ #minecraft:air unless block ~ ~ ~ minecraft:barrier run tag @a[tag=sd_placing_chest] remove sd_placing_chest
execute align xyz positioned ~ ~1 ~ unless block ~ ~ ~ #minecraft:air unless block ~ ~ ~ minecraft:barrier run return 1

execute align xyz positioned ~ ~2 ~ unless block ~ ~ ~ #minecraft:air run tellraw @a[tag=sd_placing_chest,limit=1] {"text":"上方空间不足！","color":"red"}
execute align xyz positioned ~ ~2 ~ unless block ~ ~ ~ #minecraft:air run tag @a[tag=sd_placing_chest] remove sd_placing_chest
execute align xyz positioned ~ ~2 ~ unless block ~ ~ ~ #minecraft:air run return 1

# 2. 检查是否已有箱子 (防止重复放置在同一位置)
execute align xyz positioned ~0.5 ~1 ~0.5 if entity @e[type=interaction,tag=sd_chest,distance=..0.5] as @a[tag=sd_placing_chest,limit=1] run tellraw @s {"text":"这里已经有箱子了！","color":"red"}
execute align xyz positioned ~0.5 ~1 ~0.5 if entity @e[type=interaction,tag=sd_chest,distance=..0.5] as @a[tag=sd_placing_chest,limit=1] run tag @s remove sd_placing_chest
execute align xyz positioned ~0.5 ~1 ~0.5 if entity @e[type=interaction,tag=sd_chest,distance=..0.5] run return 1

# 3. 放置箱子方块（朝向玩家的反方向），并设置 loot_table 为空防止掉落
execute if score #rotation sd_temp matches 0 align xyz positioned ~ ~1 ~ run setblock ~ ~ ~ minecraft:chest[facing=south]{LootTable:"stardew:blocks/empty_chest"}
execute if score #rotation sd_temp matches 90 align xyz positioned ~ ~1 ~ run setblock ~ ~ ~ minecraft:chest[facing=east]{LootTable:"stardew:blocks/empty_chest"}
execute if score #rotation sd_temp matches 180 align xyz positioned ~ ~1 ~ run setblock ~ ~ ~ minecraft:chest[facing=north]{LootTable:"stardew:blocks/empty_chest"}
execute if score #rotation sd_temp matches 270 align xyz positioned ~ ~1 ~ run setblock ~ ~ ~ minecraft:chest[facing=west]{LootTable:"stardew:blocks/empty_chest"}

# 4. 召唤交互实体（缩小尺寸，只覆盖箱子上半部分，避免阻挡右键打开箱子）
# 位置抬高到箱子上半部，尺寸缩小为 0.8x0.5，这样玩家可以点击箱子下半部分打开GUI
execute align xyz positioned ~0.5 ~1.5 ~0.5 run summon minecraft:interaction ~ ~ ~ {Tags:["sd_utility","sd_chest","sd_chest_interaction","init_chest"],width:0.8f,height:0.5f,response:1b}

# 5. 初始化交互实体数据
execute as @e[tag=init_chest,distance=..2] run data modify entity @s Tags append value "sd_utility_interaction"
execute as @e[tag=init_chest,distance=..2] run scoreboard players operation @s sd_rotation = #rotation sd_temp

# 6. 播放放置音效和粒子效果
execute align xyz positioned ~0.5 ~1.5 ~0.5 run playsound minecraft:block.wood.place block @a ~ ~ ~ 1 0.8
execute align xyz positioned ~0.5 ~1.5 ~0.5 run particle minecraft:block{block_state:"minecraft:oak_planks"} ~ ~ ~ 0.3 0.3 0.3 0 20

# 7. 消耗物品（非创造模式）
execute if entity @e[tag=init_chest,distance=..2] as @a[tag=sd_placing_chest,limit=1,gamemode=!creative,gamemode=!spectator] run item modify entity @s weapon.mainhand stardew:consume_one

# 7.5 初始化视觉实体的高亮系统
execute as @e[tag=init_chest,type=item_display,distance=..2] run function stardew:utility/init_highlight

# 8. 初始化完成
tag @e[tag=init_chest] remove init_chest

# 9. 清除标记
tag @a[tag=sd_placing_chest] remove sd_placing_chest
