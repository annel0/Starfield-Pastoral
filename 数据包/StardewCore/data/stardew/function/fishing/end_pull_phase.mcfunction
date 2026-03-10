# data/stardew/functions/fishing/end_pull_phase.mcfunction
# [执行者: 玩家]

# 回到平静期
scoreboard players set @s sd_fish_phase 1
scoreboard players set @s sd_fish_pull_time 0
scoreboard players set @s sd_fish_shake 0

# 先把 bossbar 名字改成“安静下来”
bossbar set stardew:fishing name {"text":"鱼暂时安静了下来……","color":"green"}
bossbar set stardew:fishing color green

# 音效稍微轻一点
playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 0.6 1.2

# 安静提示（冷却防刷）
execute if score @s sd_fish_hint_cd matches ..0 run tellraw @s ["",{"text":"[钓鱼] ","color":"aqua"},{"text":"鱼的力道慢了下来，小心别太早松懈。","color":"gray"}]
execute if score @s sd_fish_hint_cd matches ..0 run scoreboard players set @s sd_fish_hint_cd 80

# 安排一次简易“绿白闪烁”
schedule function stardew:fishing/bossbar_flash_1 3t
