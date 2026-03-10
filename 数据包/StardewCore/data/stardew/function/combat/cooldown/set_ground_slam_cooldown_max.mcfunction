# 设置震地重击技能冷却时间最大值（根据等级显示不同标题）

# 移除旧bossbar
bossbar remove stardew:ground_slam_cooldown

# 创建冷却bossbar - 根据等级显示不同标题
execute if score #skill_level sd_temp matches 1 run bossbar add stardew:ground_slam_cooldown {"text":"🔨 震地重击 I - 冷却中","color":"gray","bold":true}
execute if score #skill_level sd_temp matches 2 run bossbar add stardew:ground_slam_cooldown {"text":"🔨 震地重击 II - 冷却中","color":"gray","bold":true}
execute if score #skill_level sd_temp matches 3 run bossbar add stardew:ground_slam_cooldown {"text":"🔨 震地重击 III - 冷却中","color":"gray","bold":true}
execute if score #skill_level sd_temp matches 4 run bossbar add stardew:ground_slam_cooldown {"text":"🔨 震地重击 IV - 冷却中","color":"gray","bold":true}
execute if score #skill_level sd_temp matches 5.. run bossbar add stardew:ground_slam_cooldown {"text":"🔨 震地重击 V - 冷却中","color":"gray","bold":true}

bossbar set stardew:ground_slam_cooldown color white
execute store result bossbar stardew:ground_slam_cooldown max run scoreboard players get @s sd_skill_cooldown
execute store result bossbar stardew:ground_slam_cooldown value run scoreboard players get @s sd_skill_cooldown
bossbar set stardew:ground_slam_cooldown players @s
bossbar set stardew:ground_slam_cooldown visible false
