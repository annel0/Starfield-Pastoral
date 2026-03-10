# 杂草被破坏时的掉落逻辑
# 50%掉落纤维，否则5%掉落混合种子

# 随机掉落纤维 (50%几率)
execute store result score #fiber_drop sd_temp run random value 1..100
execute if score #fiber_drop sd_temp matches 1..50 run loot spawn ~ ~ ~ loot stardew:items/resource/fiber

# 如果没掉纤维，5%几率掉混合种子
execute if score #fiber_drop sd_temp matches 51..100 store result score #seed_drop sd_temp run random value 1..100
execute if score #fiber_drop sd_temp matches 51..100 if score #seed_drop sd_temp matches 1..5 run loot spawn ~ ~ ~ loot stardew:items/seeds/seedbag/mixed_seeds

# 播放破坏音效
playsound minecraft:block.grass.break block @a ~ ~ ~ 1 1

# 杀死杂草显示实体
kill @e[type=item_display,tag=weed,distance=..1,limit=1,sort=nearest]

# 杀死自己(interaction)
kill @s
