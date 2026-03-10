# data/stardew/functions/farming/wither_logic.mcfunction

# 春(1)，杀死 !season_1
execute if score Global sd_season matches 1 as @e[type=marker,tag=sd_crop,tag=!season_1] run function stardew:farming/wither_die

# 夏(2)，杀死 !season_2
execute if score Global sd_season matches 2 as @e[type=marker,tag=sd_crop,tag=!season_2] run function stardew:farming/wither_die

# 秋(3)，杀死 !season_3
execute if score Global sd_season matches 3 as @e[type=marker,tag=sd_crop,tag=!season_3] run function stardew:farming/wither_die

# 冬(4)，杀死 !season_4
execute if score Global sd_season matches 4 as @e[type=marker,tag=sd_crop,tag=!season_4] run function stardew:farming/wither_die
