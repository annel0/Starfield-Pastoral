# 普通宝箱 - 播放粒子效果、给予物品、显示消息
# 在动画展示时调用（第6tick）

# 粒子效果 - 绿色 happy_villager 粒子环绕
execute at @s run particle minecraft:happy_villager ^ ^1.5 ^1.5 0.4 0.5 0.4 0.1 35 force @a[distance=..20]
execute at @s run particle minecraft:glow ^ ^1.5 ^1.5 0.3 0.4 0.3 0.05 20 force @a[distance=..20]
execute at @s run particle minecraft:end_rod ^ ^1.5 ^1.5 0.25 0.35 0.25 0.03 10 force @a[distance=..20]

# 音效
execute at @s run playsound minecraft:entity.experience_orb.pickup master @a[distance=..20] ~ ~ ~ 0.6 1.3

# 给予物品（从storage中召唤掉落物）
execute at @s run summon item ~ ~1 ~ {Item:{id:"minecraft:paper",count:1},PickupDelay:0,Tags:["sd_treasure_loot"]}
execute at @s run data modify entity @e[type=item,tag=sd_treasure_loot,limit=1,sort=nearest] Item set from storage stardew:treasure temp_item

# 显示消息（使用storage中的物品名称）
function stardew:fishing/treasure_chest/show_message_common
