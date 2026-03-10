# data/stardew/functions/fishing/start_pull_phase.mcfunction
# [执行者: 玩家]

# 进入反抗期
scoreboard players set @s sd_fish_phase 2
scoreboard players set @s sd_fish_shake 0

# 持续时间：随机化挣扎时长,根据难度有不同的随机范围
# 低难度: 较长的挣扎期 (30-50 tick)
# 高难度: 较短的挣扎期 (15-35 tick)
execute if score @s sd_final_difficulty matches ..3 run execute store result score @s sd_fish_pull_time run random value 30..50
execute if score @s sd_final_difficulty matches 4..6 run execute store result score @s sd_fish_pull_time run random value 25..45
execute if score @s sd_final_difficulty matches 7..9 run execute store result score @s sd_fish_pull_time run random value 20..40
execute if score @s sd_final_difficulty matches 10..12 run execute store result score @s sd_fish_pull_time run random value 18..35
execute if score @s sd_final_difficulty matches 13.. run execute store result score @s sd_fish_pull_time run random value 15..30

# 声音 + 粒子：鱼疯狂扑腾
playsound minecraft:entity.salmon.flop player @s ~ ~ ~ 0.8 1.0
particle minecraft:splash ~ ~ ~ 0.5 0.2 0.5 0.1 20

# bossbar 变绿色 + 名字改成警告
bossbar set stardew:fishing color green
bossbar set stardew:fishing name {"text":"鱼正在拼命反抗！按住Shift对抗！","color":"yellow","bold":true}

# 适度提示（带冷却）
execute if score @s sd_fish_hint_cd matches ..0 run tellraw @s ["",{"text":"[钓鱼] ","color":"aqua"},{"text":"鱼突然发力向外逃！按住 ","color":"gray"},{"keybind":"key.sneak","color":"yellow"},{"text":" 与它对抗！","color":"gray"}]
execute if score @s sd_fish_hint_cd matches ..0 run scoreboard players set @s sd_fish_hint_cd 60
