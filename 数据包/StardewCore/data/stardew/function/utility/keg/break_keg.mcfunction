# data/stardew/function/utility/keg/break_keg.mcfunction
# 拆除小桶 - 当玩家用镐子左键交互实体时
# 执行者: 玩家 (@s)
# 上下文: 由 check_interaction 通过 execute on attacker 调用

# 1. 检查玩家是否持有镐子
execute unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=201] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=202] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=203] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=204] run return 0

# 2. 找到对应的视觉实体和交互实体并标记
execute as @e[type=item_display,tag=sd_keg_visual,distance=..2,limit=1,sort=nearest] at @s run tag @s add sd_breaking_keg
execute as @e[type=interaction,tag=sd_keg_interaction,distance=..2,limit=1,sort=nearest] at @s run tag @s add sd_breaking_interaction

# 3. 在视觉实体位置掉落小桶物品
execute as @e[tag=sd_breaking_keg,type=item_display,limit=1] at @s run loot spawn ~ ~ ~ loot stardew:items/utility/keg

# 4. 删除屏障方块 (使用交互实体的位置，它就在屏障方块上)
execute as @e[tag=sd_breaking_interaction,limit=1] at @s align xyz run setblock ~ ~ ~ minecraft:air

# 5. 删除光源方块（如果小桶正在工作）- 基于视觉实体位置
execute as @e[tag=sd_breaking_keg,limit=1] at @s align xyz positioned ~ ~1 ~ run setblock ~ ~ ~ minecraft:air

# 6. 删除产物展示和时间文本（如果存在）- 使用ID匹配，避免误删其他小桶的实体
execute as @e[tag=sd_breaking_keg,limit=1] run scoreboard players operation #breaking_id sd_keg_id = @s sd_keg_id
execute as @e[type=item_display,tag=sd_keg_product] if score @s sd_keg_id = #breaking_id sd_keg_id run kill @s
execute as @e[type=text_display,tag=sd_keg_time] if score @s sd_keg_id = #breaking_id sd_keg_id run kill @s

# 7. 播放破坏音效和粒子效果(小桶用木桶音效和橡木原木粒子)
execute as @e[tag=sd_breaking_keg,limit=1] at @s run playsound minecraft:block.wood.break block @a ~ ~ ~ 1 0.8
execute as @e[tag=sd_breaking_keg,limit=1] at @s run particle minecraft:block{block_state:"minecraft:oak_log"} ~ ~0.5 ~ 0.3 0.3 0.3 0 30

# 8. 删除视觉实体和交互实体
kill @e[tag=sd_breaking_keg]
kill @e[tag=sd_breaking_interaction]
