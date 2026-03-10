# data/stardew/function/utility/furnace/break_furnace.mcfunction
# 拆除熔炉 - 当玩家用镐子左键交互实体时
# 执行者: 玩家 (@s)
# 上下文: 由 check_interaction 通过 execute on attacker 调用

# 1. 检查玩家是否持有镐子
execute unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=201] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=202] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=203] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=204] run return 0

# 2. 找到对应的视觉实体和交互实体并标记
execute as @e[type=item_display,tag=sd_furnace_visual,distance=..2,limit=1,sort=nearest] at @s run tag @s add sd_breaking_furnace
execute as @e[type=interaction,tag=sd_furnace_interaction,distance=..2,limit=1,sort=nearest] at @s run tag @s add sd_breaking_interaction

# 3. 在视觉实体位置掉落熔炉物品
execute as @e[tag=sd_breaking_furnace,type=item_display,limit=1] at @s run loot spawn ~ ~ ~ loot stardew:items/utility/furnace

# 4. 删除屏障方块 (使用交互实体的位置，它就在屏障方块上)
execute as @e[tag=sd_breaking_interaction,limit=1] at @s align xyz run setblock ~ ~ ~ minecraft:air

# 5. 删除光源方块（如果熔炉正在工作）- 基于视觉实体位置
execute as @e[tag=sd_breaking_furnace,limit=1] at @s align xyz positioned ~ ~1 ~ run setblock ~ ~ ~ minecraft:air

# 6. 删除产物展示和时间文本（如果存在）- 使用ID匹配，避免误删其他熔炉的实体
execute as @e[tag=sd_breaking_furnace,limit=1] run scoreboard players operation #breaking_id sd_furnace_id = @s sd_furnace_id
execute as @e[type=item_display,tag=sd_furnace_product] if score @s sd_furnace_id = #breaking_id sd_furnace_id run kill @s
execute as @e[type=text_display,tag=sd_furnace_time] if score @s sd_furnace_id = #breaking_id sd_furnace_id run kill @s

# 7. 播放破坏音效和粒子效果
execute as @e[tag=sd_breaking_furnace,limit=1] at @s run playsound minecraft:block.stone.break block @a ~ ~ ~ 1 0.8
execute as @e[tag=sd_breaking_furnace,limit=1] at @s run particle minecraft:block{block_state:"minecraft:furnace"} ~ ~0.5 ~ 0.3 0.3 0.3 0 30

# 8. 删除视觉实体和交互实体
kill @e[tag=sd_breaking_furnace]
kill @e[tag=sd_breaking_interaction]
