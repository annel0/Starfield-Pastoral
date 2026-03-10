# data/stardew/function/utility/sprinkler/break_sprinkler.mcfunction
# 拆除洒水器 - 当玩家用镐子左键交互实体时
# 执行者: 玩家 (@s)
# 上下文: 从check_interaction 通过 execute on attacker 调用

# 1. 检查玩家是否持有镐子
execute unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=201] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=202] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=203] unless items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=204] run return 0

# 2. 找到对应的视觉实体和交互实体并标记
execute as @e[type=item_display,tag=sd_sprinkler_visual,distance=..2,limit=1,sort=nearest] at @s run tag @s add sd_breaking_sprinkler
execute as @e[type=interaction,tag=sd_sprinkler_interaction,distance=..2,limit=1,sort=nearest] at @s run tag @s add sd_breaking_interaction

# 3. 根据类型掉落对应洒水器物品(在视觉实体位置)
execute as @e[tag=sd_breaking_sprinkler,limit=1] store result score #sprinkler_type sd_temp run scoreboard players get @e[tag=sd_breaking_interaction,limit=1] sd_sprinkler_type
execute as @e[tag=sd_breaking_sprinkler,type=item_display,limit=1] if score #sprinkler_type sd_temp matches 1 at @s run loot spawn ~ ~ ~ loot stardew:items/utility/sprinkler_basic
execute as @e[tag=sd_breaking_sprinkler,type=item_display,limit=1] if score #sprinkler_type sd_temp matches 2 at @s run loot spawn ~ ~ ~ loot stardew:items/utility/sprinkler_quality
execute as @e[tag=sd_breaking_sprinkler,type=item_display,limit=1] if score #sprinkler_type sd_temp matches 3 at @s run loot spawn ~ ~ ~ loot stardew:items/utility/sprinkler_diamond

# 4. 删除屏障方块 (使用交互实体的位置，它就在屏障方块上)
execute as @e[tag=sd_breaking_interaction,limit=1] at @s align xyz run setblock ~ ~ ~ minecraft:air

# 5. 播放破坏音效和粒子效果
execute as @e[tag=sd_breaking_sprinkler,limit=1] at @s run playsound minecraft:block.metal.break block @a ~ ~ ~ 1 1.0
execute as @e[tag=sd_breaking_sprinkler,limit=1] at @s run particle minecraft:block{block_state:"minecraft:iron_block"} ~ ~0.5 ~ 0.3 0.3 0.3 0 30

# 6. 删除视觉实体和交互实体
kill @e[tag=sd_breaking_sprinkler]
kill @e[tag=sd_breaking_interaction]
