# data/stardew/function/utility/keg/collect_product.mcfunction
# 收取小桶产物 - 玩家右键已完成的小桶
# 执行者: 玩家 (@s)

# 1. 找到被标记的小桶
execute as @e[type=interaction,tag=sd_interacting_keg,distance=..5,limit=1] run tag @s add sd_collecting_keg
execute as @e[type=item_display,tag=sd_keg_visual,distance=..5,limit=1,sort=nearest] run tag @s add sd_collecting_visual

# 2. 根据类型给予产物（28种作物）
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=1}] run loot give @s loot stardew:items/artisan/wine/strawberry
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=2}] run loot give @s loot stardew:items/artisan/wine/blueberry
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=3}] run loot give @s loot stardew:items/artisan/wine/cranberry
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=4}] run loot give @s loot stardew:items/artisan/wine/grape
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=5}] run loot give @s loot stardew:items/artisan/wine/ancient_fruit
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=6}] run loot give @s loot stardew:items/artisan/wine/hot_pepper
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=7}] run loot give @s loot stardew:items/artisan/wine/starfruit
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=8}] run loot give @s loot stardew:items/artisan/wine/rhubarb
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=9}] run loot give @s loot stardew:items/artisan/wine/melon
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=10}] run loot give @s loot stardew:items/artisan/juice/pumpkin
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=11}] run loot give @s loot stardew:items/artisan/wine/beer
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=12}] run loot give @s loot stardew:items/artisan/wine/pale_ale
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=13}] run loot give @s loot stardew:items/artisan/juice/coffee
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=14}] run loot give @s loot stardew:items/artisan/juice/tomato
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=15}] run loot give @s loot stardew:items/artisan/juice/corn
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=16}] run loot give @s loot stardew:items/artisan/juice/potato
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=17}] run loot give @s loot stardew:items/artisan/juice/cauliflower
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=18}] run loot give @s loot stardew:items/artisan/juice/parsnip
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=19}] run loot give @s loot stardew:items/artisan/juice/green_bean
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=20}] run loot give @s loot stardew:items/artisan/juice/kale
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=21}] run loot give @s loot stardew:items/artisan/juice/garlic
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=22}] run loot give @s loot stardew:items/artisan/juice/red_cabbage
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=23}] run loot give @s loot stardew:items/artisan/juice/amaranth
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=24}] run loot give @s loot stardew:items/artisan/juice/bok_choy
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=25}] run loot give @s loot stardew:items/artisan/juice/radish
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=26}] run loot give @s loot stardew:items/artisan/juice/yam
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=27}] run loot give @s loot stardew:items/artisan/juice/artichoke
execute if entity @e[tag=sd_collecting_keg,scores={sd_keg_type=28}] run loot give @s loot stardew:items/artisan/juice/eggplant

# 3. 重置小桶状态
scoreboard players set @e[tag=sd_collecting_keg] sd_keg_state 0
scoreboard players set @e[tag=sd_collecting_keg] sd_keg_type 0
scoreboard players set @e[tag=sd_collecting_keg] sd_keg_timer 0
scoreboard players set @e[tag=sd_collecting_keg] sd_keg_max_time 0
scoreboard players reset @e[tag=sd_collecting_keg] sd_anim_tick
scoreboard players reset @e[tag=sd_collecting_keg] sd_anim_phase

# 4. 视觉实体保持不变，恢复默认 scale（保持旋转）
execute as @e[tag=sd_collecting_visual] run function stardew:utility/apply_rotation

# 5. 移除产物展示实体和时间文本显示
execute as @e[tag=sd_collecting_visual] run scoreboard players operation #collect_id sd_keg_id = @s sd_keg_id
kill @e[type=item_display,tag=sd_keg_product,distance=..5,sort=nearest,limit=1]
execute as @e[type=text_display,tag=sd_keg_time] if score @s sd_keg_id = #collect_id sd_keg_id run kill @s

# 6. 播放音效和粒子
playsound minecraft:entity.item.pickup player @a[distance=..8] ~ ~ ~ 1 1.2
particle minecraft:happy_villager ~ ~0.8 ~ 0.3 0.3 0.3 0 10

# 7. 清除标记
tag @e[tag=sd_collecting_keg] remove sd_collecting_keg
tag @e[tag=sd_collecting_visual] remove sd_collecting_visual
