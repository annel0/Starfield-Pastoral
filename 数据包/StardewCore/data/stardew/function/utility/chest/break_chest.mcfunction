# data/stardew/function/utility/chest/break_chest.mcfunction
# 破坏箱子 - 当玩家用镐子左键交互实体时
# 执行者: 玩家 (@s)
# 上下文: 由 check_interaction 通过 execute on attacker 调用

# 1. 检查玩家是否持有镐子
execute unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=201] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=202] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=203] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=204] run return 0

# 2. 找到对应的交互实体并标记
execute as @e[type=interaction,tag=sd_chest_interaction,distance=..2,limit=1,sort=nearest] at @s run tag @s add sd_breaking_chest

# 3. 在交互实体位置获取箱子内的物品并掉落（通过读取箱子的 Items NBT）
# 先将箱子内容复制到临时存储
execute as @e[tag=sd_breaking_chest,limit=1] at @s align xyz run data modify storage stardew:temp chest_items set from block ~ ~ ~ Items

# 4. 删除箱子方块（使用 replace 不会有任何掉落物，也不会有粒子）
execute as @e[tag=sd_breaking_chest,limit=1] at @s align xyz run setblock ~ ~ ~ minecraft:air replace

# 5. 播放破坏音效和粒子效果
execute as @e[tag=sd_breaking_chest,limit=1] at @s run playsound minecraft:block.wood.break block @a ~ ~ ~ 1 0.8
execute as @e[tag=sd_breaking_chest,limit=1] at @s run particle minecraft:block{block_state:"minecraft:oak_planks"} ~ ~0.5 ~ 0.3 0.3 0.3 0 30

# 6. 召唤掉落物：箱子物品（数据包版本）
execute as @e[tag=sd_breaking_chest,limit=1] at @s run loot spawn ~ ~ ~ loot stardew:items/utility/chest

# 7. 召唤掉落物：箱子内的物品（从存储中读取）
# 使用 marker 实体临时存储并召唤物品
execute as @e[tag=sd_breaking_chest,limit=1] at @s run summon marker ~ ~ ~ {Tags:["sd_chest_dropper"]}
execute as @e[tag=sd_chest_dropper,limit=1] run data modify entity @s data.Items set from storage stardew:temp chest_items
execute as @e[tag=sd_chest_dropper,limit=1] at @s run function stardew:utility/chest/drop_items
kill @e[tag=sd_chest_dropper]

# 8. 清理临时存储
data remove storage stardew:temp chest_items
data remove storage stardew:temp current_item

# 9. 删除交互实体
kill @e[tag=sd_breaking_chest]
