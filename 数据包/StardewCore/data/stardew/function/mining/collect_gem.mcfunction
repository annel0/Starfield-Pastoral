# stardew:mining/collect_gem.mcfunction
# 玩家右键收集宝石矿（类似觅食）
# 执行者: 玩家 (@s)
# 上下文: 已确认右键了 interaction 实体 (tag=sd_current_interaction)

# 1. 播放音效
playsound minecraft:entity.item.pickup master @s ~ ~ ~ 1 1.2

# 1.5 清除屏障方块
execute at @e[tag=sd_current_interaction,limit=1] run setblock ~ ~ ~ minecraft:air

# 2. 在原位置生成掉落物 (使用 loot spawn)
execute at @e[tag=sd_current_interaction,limit=1] if entity @e[tag=sd_current_interaction,tag=sd_type_quartz,limit=1] run loot spawn ~ ~0.5 ~ loot stardew:items/gems/quartz_unknown
execute at @e[tag=sd_current_interaction,limit=1] if entity @e[tag=sd_current_interaction,tag=sd_type_earth_crystal,limit=1] run loot spawn ~ ~0.5 ~ loot stardew:items/gems/earth_crystal_unknown
execute at @e[tag=sd_current_interaction,limit=1] if entity @e[tag=sd_current_interaction,tag=sd_type_frozen_tear,limit=1] run loot spawn ~ ~0.5 ~ loot stardew:items/gems/frozen_tear_unknown
execute at @e[tag=sd_current_interaction,limit=1] if entity @e[tag=sd_current_interaction,tag=sd_type_jade,limit=1] run loot spawn ~ ~0.5 ~ loot stardew:items/gems/jade_unknown
execute at @e[tag=sd_current_interaction,limit=1] if entity @e[tag=sd_current_interaction,tag=sd_type_ruby,limit=1] run loot spawn ~ ~0.5 ~ loot stardew:items/gems/ruby_unknown
execute at @e[tag=sd_current_interaction,limit=1] if entity @e[tag=sd_current_interaction,tag=sd_type_amethyst,limit=1] run loot spawn ~ ~0.5 ~ loot stardew:items/gems/amethyst_unknown
execute at @e[tag=sd_current_interaction,limit=1] if entity @e[tag=sd_current_interaction,tag=sd_type_prismatic_shard,limit=1] run loot spawn ~ ~0.5 ~ loot stardew:items/gems/prismatic_shard_unknown

# 3. 给予经验 (觅食经验? 还是挖矿经验? 暂时给挖矿经验)
scoreboard players add @s sd_mining_xp 7

# 3.5 矿洞梯子检测 (如果是矿洞中的宝石)
execute at @e[tag=sd_current_interaction,limit=1] if entity @e[tag=sd_current_interaction,tag=sd_mine_stone,limit=1] run function stardew:mine/ladder/try_spawn

# 4. 移除实体
# 移除关联的 item_display (距离最近的 sd_stone_display)
execute at @e[tag=sd_current_interaction,limit=1] run kill @e[type=item_display,tag=sd_stone_display,distance=..1,limit=1,sort=nearest]
# 移除 interaction
kill @e[tag=sd_current_interaction,limit=1]
