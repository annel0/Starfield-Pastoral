# stardew:mine/barrel/break_impl.mcfunction
# 木桶破坏的具体实现
# 执行者: interaction实体 (@s, tag=sd_current_barrel_target)
# 执行位置: 木桶位置

# 播放破坏音效和粒子（只在这个木桶位置）
playsound minecraft:block.wood.break master @a ~ ~ ~ 1 0.8
particle minecraft:block{block_state:"minecraft:barrel"} ~ ~0.5 ~ 0.3 0.3 0.3 0.1 20

# 获取当前矿井层数 (从最近的玩家)
execute store result score #barrel_floor sd_temp run scoreboard players get @p sd_mine_floor

# 生成常规loot (矿井1-100层,6个区间)
execute if score #barrel_floor sd_temp matches 1..16 run loot spawn ~ ~0.5 ~ loot stardew:mining/barrel_copper
execute if score #barrel_floor sd_temp matches 17..33 run loot spawn ~ ~0.5 ~ loot stardew:mining/barrel_copper
execute if score #barrel_floor sd_temp matches 34..50 run loot spawn ~ ~0.5 ~ loot stardew:mining/barrel_iron
execute if score #barrel_floor sd_temp matches 51..66 run loot spawn ~ ~0.5 ~ loot stardew:mining/barrel_iron
execute if score #barrel_floor sd_temp matches 67..83 run loot spawn ~ ~0.5 ~ loot stardew:mining/barrel_gold
execute if score #barrel_floor sd_temp matches 84..100 run loot spawn ~ ~0.5 ~ loot stardew:mining/barrel_gold

# 生成特殊loot (2.2%概率,独立掉落池)
execute store result score #random sd_temp run random value 1..1000
execute if score #barrel_floor sd_temp matches 1..16 if score #random sd_temp matches 1..22 run loot spawn ~ ~0.5 ~ loot stardew:mining/barrel_special_1
execute if score #barrel_floor sd_temp matches 17..33 if score #random sd_temp matches 1..22 run loot spawn ~ ~0.5 ~ loot stardew:mining/barrel_special_2
execute if score #barrel_floor sd_temp matches 34..50 if score #random sd_temp matches 1..22 run loot spawn ~ ~0.5 ~ loot stardew:mining/barrel_special_3
execute if score #barrel_floor sd_temp matches 51..66 if score #random sd_temp matches 1..22 run loot spawn ~ ~0.5 ~ loot stardew:mining/barrel_special_4
execute if score #barrel_floor sd_temp matches 67..83 if score #random sd_temp matches 1..22 run loot spawn ~ ~0.5 ~ loot stardew:mining/barrel_special_5
execute if score #barrel_floor sd_temp matches 84..100 if score #random sd_temp matches 1..22 run loot spawn ~ ~0.5 ~ loot stardew:mining/barrel_special_6

# 移除屏障方块（对齐到方块坐标）
execute align xyz run setblock ~ ~ ~ minecraft:air

# 找到对应的item_display并杀死（使用distance=..0.5确保只杀死这个木桶的视觉实体）
kill @e[type=item_display,tag=sd_mine_barrel,distance=..0.5,limit=1,sort=nearest]

# 移除标记并杀死自己（interaction）
tag @s remove sd_current_barrel_target
kill @s
