# data/stardew/function/menu/npc/render_abigail.mcfunction
# 渲染阿比盖尔的NPC卡片 - 超级简化版
# 执行者: item_display 实体（基础卡片按钮）

# 1. 获取玩家的友谊值、对话状态、送礼状态
execute on target run scoreboard players operation #friendship stardew.temp = @s stardew.friendship.abigail
execute on target run scoreboard players operation #talked stardew.temp = @s stardew.talked.abigail
execute on target run scoreboard players operation #gifted stardew.temp = @s stardew.gifted.abigail

# 2. 确保分数有效（初始化为0）
execute unless score #friendship stardew.temp matches -2147483648..2147483647 run scoreboard players set #friendship stardew.temp 0
execute unless score #talked stardew.temp matches -2147483648..2147483647 run scoreboard players set #talked stardew.temp 0
execute unless score #gifted stardew.temp matches -2147483648..2147483647 run scoreboard players set #gifted stardew.temp 0

# 3. 计算满心数
scoreboard players operation #hearts stardew.temp = #friendship stardew.temp
scoreboard players operation #hearts stardew.temp /= #250 stardew.const
execute if score #hearts stardew.temp matches ..0 run scoreboard players set #hearts stardew.temp 0
execute if score #hearts stardew.temp matches 11.. run scoreboard players set #hearts stardew.temp 10

# 4. 显示基础卡片 - 使用现有的空白按钮
data merge entity @s {item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":2}}}

# 5. 向玩家发送好感度信息（聊天栏显示）
execute on target run tellraw @s [{"text":"====== 阿比盖尔 ======","color":"light_purple","bold":true}]
execute on target run tellraw @s [{"text":"好感度: ","color":"gray"},{"score":{"name":"#friendship","objective":"stardew.temp"},"color":"yellow"},{"text":"/2500","color":"gray"},{"text":" (","color":"gray"},{"score":{"name":"#hearts","objective":"stardew.temp"},"color":"red"},{"text":"❤","color":"red"},{"text":")","color":"gray"}]
execute on target if score #talked stardew.temp matches 1.. run tellraw @s [{"text":"今日对话: ","color":"gray"},{"text":"✓ 已对话","color":"green"}]
execute on target if score #talked stardew.temp matches 0 run tellraw @s [{"text":"今日对话: ","color":"gray"},{"text":"✗ 未对话","color":"red"}]
execute on target run tellraw @s [{"text":"本周送礼: ","color":"gray"},{"score":{"name":"#gifted","objective":"stardew.temp"},"color":"gold"},{"text":"/2","color":"gray"}]
execute on target run tellraw @s [{"text":"====================","color":"light_purple"}]

