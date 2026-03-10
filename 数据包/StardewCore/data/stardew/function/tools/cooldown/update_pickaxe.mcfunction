# data/stardew/functions/tools/cooldown/update_pickaxe.mcfunction
# 更新镐子冷却倒计时

# 显示 Boss 血条
bossbar set stardew:pickaxe_cooldown players @s
bossbar set stardew:pickaxe_cooldown visible true

# 递减冷却时间
scoreboard players remove @s sd_pickaxe_cd 1

# 更新 Boss 血条值
execute store result bossbar stardew:pickaxe_cooldown value run scoreboard players get @s sd_pickaxe_cd

# 冷却结束处理
execute if score @s sd_pickaxe_cd matches ..0 run function stardew:tools/cooldown/end_pickaxe
