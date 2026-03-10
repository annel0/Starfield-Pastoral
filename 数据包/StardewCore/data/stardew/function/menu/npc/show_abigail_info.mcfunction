# data/stardew/function/menu/npc/show_abigail_info.mcfunction
# 显示阿比盖尔的详细好感度信息
# 执行者: 玩家 (@s)

# 1. 获取好感度数据
scoreboard players operation #friendship stardew.temp = @s stardew.friendship.abigail
scoreboard players operation #talked stardew.temp = @s stardew.talked.abigail
scoreboard players operation #gifted stardew.temp = @s stardew.gifted.abigail

# 2. 确保分数有效
execute unless score #friendship stardew.temp matches -2147483648..2147483647 run scoreboard players set #friendship stardew.temp 0
execute unless score #talked stardew.temp matches -2147483648..2147483647 run scoreboard players set #talked stardew.temp 0
execute unless score #gifted stardew.temp matches -2147483648..2147483647 run scoreboard players set #gifted stardew.temp 0

# 3. 计算满心数
scoreboard players operation #hearts stardew.temp = #friendship stardew.temp
scoreboard players operation #hearts stardew.temp /= #250 stardew.const
execute if score #hearts stardew.temp matches ..0 run scoreboard players set #hearts stardew.temp 0
execute if score #hearts stardew.temp matches 11.. run scoreboard players set #hearts stardew.temp 10

# 4. 显示详细信息
tellraw @s [{"text":"\n========================================","color":"light_purple","bold":true}]
tellraw @s [{"text":"          阿比盖尔 (Abigail)          ","color":"light_purple","bold":true}]
tellraw @s [{"text":"========================================","color":"light_purple","bold":true}]

# 显示心数（可视化）
execute if score #hearts stardew.temp matches 0 run tellraw @s [{"text":"好感度: ","color":"gray"},{"text":"♡♡♡♡♡♡♡♡♡♡","color":"dark_gray"},{"text":" (0心)","color":"gray"}]
execute if score #hearts stardew.temp matches 1 run tellraw @s [{"text":"好感度: ","color":"gray"},{"text":"❤","color":"red"},{"text":"♡♡♡♡♡♡♡♡♡","color":"dark_gray"},{"text":" (1心)","color":"gray"}]
execute if score #hearts stardew.temp matches 2 run tellraw @s [{"text":"好感度: ","color":"gray"},{"text":"❤❤","color":"red"},{"text":"♡♡♡♡♡♡♡♡","color":"dark_gray"},{"text":" (2心)","color":"gray"}]
execute if score #hearts stardew.temp matches 3 run tellraw @s [{"text":"好感度: ","color":"gray"},{"text":"❤❤❤","color":"red"},{"text":"♡♡♡♡♡♡♡","color":"dark_gray"},{"text":" (3心)","color":"gray"}]
execute if score #hearts stardew.temp matches 4 run tellraw @s [{"text":"好感度: ","color":"gray"},{"text":"❤❤❤❤","color":"red"},{"text":"♡♡♡♡♡♡","color":"dark_gray"},{"text":" (4心)","color":"gray"}]
execute if score #hearts stardew.temp matches 5 run tellraw @s [{"text":"好感度: ","color":"gray"},{"text":"❤❤❤❤❤","color":"red"},{"text":"♡♡♡♡♡","color":"dark_gray"},{"text":" (5心)","color":"gray"}]
execute if score #hearts stardew.temp matches 6 run tellraw @s [{"text":"好感度: ","color":"gray"},{"text":"❤❤❤❤❤❤","color":"red"},{"text":"♡♡♡♡","color":"dark_gray"},{"text":" (6心)","color":"gray"}]
execute if score #hearts stardew.temp matches 7 run tellraw @s [{"text":"好感度: ","color":"gray"},{"text":"❤❤❤❤❤❤❤","color":"red"},{"text":"♡♡♡","color":"dark_gray"},{"text":" (7心)","color":"gray"}]
execute if score #hearts stardew.temp matches 8 run tellraw @s [{"text":"好感度: ","color":"gray"},{"text":"❤❤❤❤❤❤❤❤","color":"red"},{"text":"♡♡","color":"dark_gray"},{"text":" (8心)","color":"gray"}]
execute if score #hearts stardew.temp matches 9 run tellraw @s [{"text":"好感度: ","color":"gray"},{"text":"❤❤❤❤❤❤❤❤❤","color":"red"},{"text":"♡","color":"dark_gray"},{"text":" (9心)","color":"gray"}]
execute if score #hearts stardew.temp matches 10 run tellraw @s [{"text":"好感度: ","color":"gray"},{"text":"❤❤❤❤❤❤❤❤❤❤","color":"red"},{"text":" (10心)","color":"gray"}]

# 显示具体分数
tellraw @s [{"text":"分数: ","color":"gray"},{"score":{"name":"#friendship","objective":"stardew.temp"},"color":"yellow"},{"text":"/2500","color":"gray"}]

# 显示对话状态
tellraw @s ""
execute if score #talked stardew.temp matches 1.. run tellraw @s [{"text":"📝 今日对话: ","color":"gray"},{"text":"✓ 已对话","color":"green","bold":true}]
execute if score #talked stardew.temp matches 0 run tellraw @s [{"text":"📝 今日对话: ","color":"gray"},{"text":"✗ 未对话","color":"red","bold":true}]

# 显示送礼状态
execute if score #gifted stardew.temp matches 0 run tellraw @s [{"text":"🎁 本周送礼: ","color":"gray"},{"text":"□ □","color":"dark_gray"},{"text":" (0/2)","color":"gray"}]
execute if score #gifted stardew.temp matches 1 run tellraw @s [{"text":"🎁 本周送礼: ","color":"gray"},{"text":"■","color":"gold"},{"text":" □","color":"dark_gray"},{"text":" (1/2)","color":"gray"}]
execute if score #gifted stardew.temp matches 2.. run tellraw @s [{"text":"🎁 本周送礼: ","color":"gray"},{"text":"■ ■","color":"gold"},{"text":" (2/2)","color":"gray"}]

tellraw @s [{"text":"========================================","color":"light_purple","bold":true}]
tellraw @s ""

# 5. 播放音效
playsound ui.button.click player @s ~ ~ ~ 0.5 1.2
