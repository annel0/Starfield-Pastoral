# 史诗宝箱 - 播放粒子效果、给予物品、显示消息
# 在动画展示时调用（第6tick）

# 粒子效果 - 紫色 enchant 粒子 + 强烈闪光
execute at @s run particle minecraft:enchant ^ ^1.5 ^1.5 0.6 0.7 0.6 1.5 100 force @a[distance=..20]
execute at @s run particle minecraft:glow ^ ^1.5 ^1.5 0.5 0.6 0.5 0.15 40 force @a[distance=..20]
execute at @s run particle minecraft:end_rod ^ ^1.5 ^1.5 0.4 0.5 0.4 0.1 35 force @a[distance=..20]
execute at @s run particle minecraft:firework ^ ^1.5 ^1.5 0.45 0.55 0.45 0.05 30 force @a[distance=..20]
execute at @s run particle minecraft:totem_of_undying ^ ^1.5 ^1.5 0.4 0.5 0.4 0.08 25 force @a[distance=..20]

# 音效
execute at @s run playsound minecraft:entity.experience_orb.pickup master @a[distance=..20] ~ ~ ~ 1.0 1.7
execute at @s run playsound minecraft:block.amethyst_block.chime master @a[distance=..20] ~ ~ ~ 0.8 0.5

# 给予物品
execute at @s run summon item ~ ~1 ~ {Item:{id:"minecraft:paper",count:1},PickupDelay:0,Tags:["sd_treasure_loot"]}
execute at @s run data modify entity @e[type=item,tag=sd_treasure_loot,limit=1,sort=nearest] Item set from storage stardew:treasure temp_item

# 显示消息
function stardew:fishing/treasure_chest/show_message_epic
