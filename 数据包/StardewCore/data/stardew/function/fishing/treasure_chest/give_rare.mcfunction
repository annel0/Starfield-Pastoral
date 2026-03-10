# 稀有宝箱 - 播放粒子效果、给予物品、显示消息
# 在动画展示时调用（第6tick）

# 粒子效果 - 蓝色 scrape 粒子 + 水花
execute at @s run particle minecraft:scrape ^ ^1.5 ^1.5 0.5 0.6 0.5 0.2 50 force @a[distance=..20]
execute at @s run particle minecraft:falling_water ^ ^1.5 ^1.5 0.35 0.45 0.35 0.1 30 force @a[distance=..20]
execute at @s run particle minecraft:glow ^ ^1.5 ^1.5 0.35 0.45 0.35 0.08 25 force @a[distance=..20]
execute at @s run particle minecraft:end_rod ^ ^1.5 ^1.5 0.3 0.4 0.3 0.05 20 force @a[distance=..20]

# 音效
execute at @s run playsound minecraft:entity.experience_orb.pickup master @a[distance=..20] ~ ~ ~ 0.8 1.5

# 给予物品
execute at @s run summon item ~ ~1 ~ {Item:{id:"minecraft:paper",count:1},PickupDelay:0,Tags:["sd_treasure_loot"]}
execute at @s run data modify entity @e[type=item,tag=sd_treasure_loot,limit=1,sort=nearest] Item set from storage stardew:treasure temp_item

# 显示消息
function stardew:fishing/treasure_chest/show_message_rare
