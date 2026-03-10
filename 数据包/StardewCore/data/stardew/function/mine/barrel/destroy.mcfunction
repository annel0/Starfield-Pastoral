# stardew:mine/barrel/destroy.mcfunction
# 木桶破坏逻辑

# 检查玩家是否持有武器
execute unless data entity @s SelectedItem.components."minecraft:custom_data".stardew_weapon run return 0

# 播放破坏音效和粒子
execute as @e[type=item_display,tag=sd_mine_barrel,distance=..2] at @s run playsound minecraft:block.wood.break master @a ~ ~ ~ 1 0.8
execute as @e[type=item_display,tag=sd_mine_barrel,distance=..2] at @s run particle minecraft:block{block_state:"minecraft:barrel"} ~ ~0.5 ~ 0.3 0.3 0.3 0.1 20

# 获取当前矿井层数
execute store result score #barrel_floor sd_temp run data get storage stardew:mine current_floor

# 在每个木桶位置生成loot (矿井1-100层,3个区间)
execute as @e[type=item_display,tag=sd_mine_barrel,distance=..2] at @s if score #barrel_floor sd_temp matches 1..39 run loot spawn ~ ~ ~ loot stardew:mining/barrel_copper
execute as @e[type=item_display,tag=sd_mine_barrel,distance=..2] at @s if score #barrel_floor sd_temp matches 40..79 run loot spawn ~ ~ ~ loot stardew:mining/barrel_iron
execute as @e[type=item_display,tag=sd_mine_barrel,distance=..2] at @s if score #barrel_floor sd_temp matches 80..100 run loot spawn ~ ~ ~ loot stardew:mining/barrel_gold

# 移除屏障方块 (在interaction位置,即地面)
execute as @e[type=interaction,tag=sd_barrel_interaction,distance=..2] at @s align xyz run setblock ~ ~ ~ minecraft:air

# 移除所有木桶相关实体
kill @e[type=item_display,tag=sd_mine_barrel,distance=..2]
kill @e[type=interaction,tag=sd_barrel_interaction,distance=..2]
