# data/stardew/functions/economy/sell_logic.mcfunction
# [执行者: 玩家]

# 1. 初始化变量
scoreboard players set @s sd_sell_price 0

# 2. 读取价格
execute store result score @s sd_sell_price run data get entity @s SelectedItem.components."minecraft:custom_data".sd_price

# 3. 检查价格有效性
execute if score @s sd_sell_price matches ..0 run tellraw @s {"text":"无法出售：该物品没有设定价格 (sd_price)","color":"red"}
execute if score @s sd_sell_price matches ..0 run return 1

# 4. 读取数量 & 计算总价
execute store result score @s sd_const run data get entity @s SelectedItem.count
scoreboard players operation @s sd_sell_price *= @s sd_const

# 5. 加钱
scoreboard players operation @s sd_gold += @s sd_sell_price

# 6. 成功反馈
playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 1 1
particle minecraft:happy_villager ~ ~1 ~ 0.5 0.5 0.5 0 10
tellraw @s [{"text":"[出货] ","color":"green"},{"text":"出售成功！获得 ","color":"gray"},{"score":{"name":"@s","objective":"sd_sell_price"},"color":"gold"},{"text":" G","color":"gold"}]

# 7. 移除物品
item replace entity @s weapon.mainhand with air