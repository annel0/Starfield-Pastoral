# Alex NPC交互处理
# 当玩家与Alex交互时调用

# 增加友谊值 (+20 for daily talk)
execute unless score @s stardew_daily_talk matches 1.. run scoreboard players add @s stardew_friendship 20
execute unless score @s stardew_daily_talk matches 1.. run scoreboard players set @s stardew_daily_talk 1

# 计算友谊心数等级 (每250分一颗心)
scoreboard players operation @s stardew_heart_level = @s stardew_friendship
scoreboard players operation @s stardew_heart_level /= #250 stardew_const

# 检测是否持有礼物
execute if data entity @s SelectedItem run function stardew:npc/alex/check_gift

# 根据友谊等级选择对话内容
execute if score @s stardew_heart_level matches ..2 run function stardew:npc/alex/dialogue_low
execute if score @s stardew_heart_level matches 3..6 run function stardew:npc/alex/dialogue_mid
execute if score @s stardew_heart_level matches 7.. run function stardew:npc/alex/dialogue_high

# 播放对话动画
execute as @n[tag=npc_alex,distance=..10] run function animated_java:chicken/animations/idle/play

# 显示友谊信息 (调试用)
tellraw @s[tag=debug] [{"text":"Alex友谊值: ","color":"gray"},{"score":{"name":"@s","objective":"stardew_friendship"},"color":"yellow"},{"text":" (","color":"gray"},{"score":{"name":"@s","objective":"stardew_heart_level"},"color":"red"},{"text":"♥)","color":"red"}]