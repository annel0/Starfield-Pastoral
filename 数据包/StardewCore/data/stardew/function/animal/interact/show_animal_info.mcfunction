# 显示动物信息
# 在潜行右键时抚摸，单纯右键时显示信息

# 获取动物类型名称
execute if score @s stardew.animal.type matches 101 run data modify storage stardew:temp animal_name set value '{"text":"鸡","color":"gold"}'
execute if score @s stardew.animal.type matches 102 run data modify storage stardew:temp animal_name set value '{"text":"鸭","color":"gold"}'
execute if score @s stardew.animal.type matches 103 run data modify storage stardew:temp animal_name set value '{"text":"兔","color":"gold"}'
execute if score @s stardew.animal.type matches 104 run data modify storage stardew:temp animal_name set value '{"text":"恐龙","color":"gold"}'
execute if score @s stardew.animal.type matches 105 run data modify storage stardew:temp animal_name set value '{"text":"虚空鸡","color":"gold"}'
execute if score @s stardew.animal.type matches 106 run data modify storage stardew:temp animal_name set value '{"text":"金鸡","color":"gold"}'
execute if score @s stardew.animal.type matches 201 run data modify storage stardew:temp animal_name set value '{"text":"牛","color":"gold"}'
execute if score @s stardew.animal.type matches 202 run data modify storage stardew:temp animal_name set value '{"text":"山羊","color":"gold"}'
execute if score @s stardew.animal.type matches 203 run data modify storage stardew:temp animal_name set value '{"text":"羊","color":"gold"}'
execute if score @s stardew.animal.type matches 204 run data modify storage stardew:temp animal_name set value '{"text":"猪","color":"gold"}'
execute if score @s stardew.animal.type matches 205 run data modify storage stardew:temp animal_name set value '{"text":"鸵鸟","color":"gold"}'

# 计算好感度等级（0-5颗心）
scoreboard players operation #hearts stardew.animal.temp = @s stardew.animal.friendship
scoreboard players operation #hearts stardew.animal.temp /= #200 stardew.animal.const

# 计算心情描述
execute if score @s stardew.animal.mood matches 200.. run data modify storage stardew:temp mood_text set value '{"text":"非常开心","color":"green"}'
execute if score @s stardew.animal.mood matches 100..199 run data modify storage stardew:temp mood_text set value '{"text":"开心","color":"yellow"}'
execute if score @s stardew.animal.mood matches 30..99 run data modify storage stardew:temp mood_text set value '{"text":"还不错","color":"gold"}'
execute if score @s stardew.animal.mood matches 0..29 run data modify storage stardew:temp mood_text set value '{"text":"很难过","color":"red"}'

# 计算年龄描述（根据动物类型判断成熟天数）
# 先设置默认为幼年
data modify storage stardew:temp age_text set value '{"text":"幼年","color":"aqua"}'

# 鸡舍动物：鸡(101)/鸭(102)/兔(103) - 5天成熟
execute if score @s stardew.animal.type matches 101..103 if score @s stardew.animal.age matches 5.. run data modify storage stardew:temp age_text set value '{"text":"成年","color":"white"}'

# 恐龙(104) - 6天成熟
execute if score @s stardew.animal.type matches 104 if score @s stardew.animal.age matches 6.. run data modify storage stardew:temp age_text set value '{"text":"成年","color":"white"}'

# 虚空鸡(105)/金鸡(106) - 5天成熟
execute if score @s stardew.animal.type matches 105..106 if score @s stardew.animal.age matches 5.. run data modify storage stardew:temp age_text set value '{"text":"成年","color":"white"}'

# 畜棚动物：牛(201)/山羊(202) - 5天成熟
execute if score @s stardew.animal.type matches 201..202 if score @s stardew.animal.age matches 5.. run data modify storage stardew:temp age_text set value '{"text":"成年","color":"white"}'

# 羊(203) - 3天成熟
execute if score @s stardew.animal.type matches 203 if score @s stardew.animal.age matches 3.. run data modify storage stardew:temp age_text set value '{"text":"成年","color":"white"}'

# 猪(204) - 10天成熟
execute if score @s stardew.animal.type matches 204 if score @s stardew.animal.age matches 10.. run data modify storage stardew:temp age_text set value '{"text":"成年","color":"white"}'

# 鸵鸟(205) - 9.5天成熟（取整为10天）
execute if score @s stardew.animal.type matches 205 if score @s stardew.animal.age matches 10.. run data modify storage stardew:temp age_text set value '{"text":"成年","color":"white"}'

# 计算今日状态图标
execute if score @s stardew.animal.friendship_today matches 1 run data modify storage stardew:temp pet_icon set value '{"text":"✓","color":"green"}'
execute if score @s stardew.animal.friendship_today matches 0 run data modify storage stardew:temp pet_icon set value '{"text":"○","color":"gray"}'
execute if score @s stardew.animal.fed_today matches 1 run data modify storage stardew:temp feed_icon set value '{"text":"✓","color":"green"}'
execute if score @s stardew.animal.fed_today matches 0 run data modify storage stardew:temp feed_icon set value '{"text":"○","color":"gray"}'

# 压缩显示（10行以内）
tellraw @p[distance=..5] [{"text":"━━━━━━━━━━━━━━━━━━━━","color":"gray"}]
tellraw @p[distance=..5] [{"text":"🐾 ","color":"gold"},{"nbt":"animal_name","storage":"stardew:temp","interpret":true},{"text":" | ","color":"dark_gray"},{"nbt":"age_text","storage":"stardew:temp","interpret":true},{"text":"("},{"score":{"name":"@s","objective":"stardew.animal.age"}},{"text":"天) | 🏠","color":"gray"},{"score":{"name":"@s","objective":"stardew.animal.building_id"},"color":"aqua"}]

# 好感度（星星+数值）
execute if score #hearts stardew.animal.temp matches 0 run tellraw @p[distance=..5] [{"text":"❤ ☆☆☆☆☆ ","color":"red"},{"score":{"name":"@s","objective":"stardew.animal.friendship"}},{"text":"/1000","color":"dark_gray"}]
execute if score #hearts stardew.animal.temp matches 1 run tellraw @p[distance=..5] [{"text":"❤ ★☆☆☆☆ ","color":"red"},{"score":{"name":"@s","objective":"stardew.animal.friendship"}},{"text":"/1000","color":"dark_gray"}]
execute if score #hearts stardew.animal.temp matches 2 run tellraw @p[distance=..5] [{"text":"❤ ★★☆☆☆ ","color":"red"},{"score":{"name":"@s","objective":"stardew.animal.friendship"}},{"text":"/1000","color":"dark_gray"}]
execute if score #hearts stardew.animal.temp matches 3 run tellraw @p[distance=..5] [{"text":"❤ ★★★☆☆ ","color":"red"},{"score":{"name":"@s","objective":"stardew.animal.friendship"}},{"text":"/1000","color":"dark_gray"}]
execute if score #hearts stardew.animal.temp matches 4 run tellraw @p[distance=..5] [{"text":"❤ ★★★★☆ ","color":"red"},{"score":{"name":"@s","objective":"stardew.animal.friendship"}},{"text":"/1000","color":"dark_gray"}]
execute if score #hearts stardew.animal.temp matches 5.. run tellraw @p[distance=..5] [{"text":"❤ ★★★★★ ","color":"red"},{"score":{"name":"@s","objective":"stardew.animal.friendship"}},{"text":"/1000","color":"dark_gray"}]

# 心情（文本+数值）
tellraw @p[distance=..5] [{"text":"☺ ","color":"yellow"},{"nbt":"mood_text","storage":"stardew:temp","interpret":true},{"text":" "},{"score":{"name":"@s","objective":"stardew.animal.mood"}},{"text":"/255","color":"dark_gray"}]

# 今日状态（抚摸+喂食）
tellraw @p[distance=..5] [{"nbt":"pet_icon","storage":"stardew:temp","interpret":true},{"text":" 抚摸 | ","color":"gray"},{"nbt":"feed_icon","storage":"stardew:temp","interpret":true},{"text":" 喂食","color":"gray"}]

# 计算出售价格（按星露谷物语公式）
# 售价 = 购买价格 × ((好感度 / 1000) + 0.3)
# 我们用分数计算避免小数
scoreboard players operation #sell_price stardew.animal.temp = @s stardew.animal.type
execute if score #sell_price stardew.animal.temp matches 101 run scoreboard players set #base_price stardew.animal.temp 800
execute if score #sell_price stardew.animal.temp matches 102 run scoreboard players set #base_price stardew.animal.temp 1200
execute if score #sell_price stardew.animal.temp matches 103 run scoreboard players set #base_price stardew.animal.temp 8000
execute if score #sell_price stardew.animal.temp matches 104 run scoreboard players set #base_price stardew.animal.temp 1300
execute if score #sell_price stardew.animal.temp matches 105 run scoreboard players set #base_price stardew.animal.temp 1040
execute if score #sell_price stardew.animal.temp matches 106 run scoreboard players set #base_price stardew.animal.temp 100000
execute if score #sell_price stardew.animal.temp matches 201 run scoreboard players set #base_price stardew.animal.temp 1500
execute if score #sell_price stardew.animal.temp matches 202 run scoreboard players set #base_price stardew.animal.temp 4000
execute if score #sell_price stardew.animal.temp matches 203 run scoreboard players set #base_price stardew.animal.temp 8000
execute if score #sell_price stardew.animal.temp matches 204 run scoreboard players set #base_price stardew.animal.temp 16000
execute if score #sell_price stardew.animal.temp matches 205 run scoreboard players set #base_price stardew.animal.temp 20000

# 计算: 售价 = 基础价格 * ((好感度/1000) + 0.3)
# = 基础价格 * (好感度/1000 + 300/1000)
# = 基础价格 * (好感度 + 300) / 1000
scoreboard players set #1000 stardew.animal.const 1000
scoreboard players set #300 stardew.animal.const 300
scoreboard players operation #friendship_bonus stardew.animal.temp = @s stardew.animal.friendship
scoreboard players operation #friendship_bonus stardew.animal.temp += #300 stardew.animal.const
scoreboard players operation #sell_price stardew.animal.temp = #base_price stardew.animal.temp
scoreboard players operation #sell_price stardew.animal.temp *= #friendship_bonus stardew.animal.temp
scoreboard players operation #sell_price stardew.animal.temp /= #1000 stardew.animal.const

# 显示出售按钮（可点击）
tellraw @p[distance=..5] [{"text":"💰 售价: ","color":"gold"},{"score":{"name":"#sell_price","objective":"stardew.animal.temp"},"color":"yellow"},{"text":"G ","color":"gold"},{"text":"[出售]","color":"red","bold":true,"clickEvent":{"action":"run_command","value":"/function stardew:animal/interact/sell_animal"},"hoverEvent":{"action":"show_text","contents":"点击出售"}}]
tellraw @p[distance=..5] [{"text":"💡 Shift+右键抚摸","color":"dark_gray","italic":true}]
tellraw @p[distance=..5] [{"text":"━━━━━━━━━━━━━━━━━━━━","color":"gray"}]

# 播放音效
playsound minecraft:entity.experience_orb.pickup player @p[distance=..5] ~ ~ ~ 0.5 1.5

# 保存动物ID到玩家scoreboard（用于出售确认）
scoreboard players operation @p[distance=..5] stardew.animal.selected_id = @s stardew.animal.id
