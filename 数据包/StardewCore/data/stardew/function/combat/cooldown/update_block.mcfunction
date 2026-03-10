# 更新格挡冷却

# 如果还在格挡中，不更新冷却bossbar（等格挡结束后才开始冷却倒计时）
execute if entity @s[tag=sd_blocking] run return 0

# 减少冷却时间
scoreboard players remove @s sd_block_cooldown 1

# 更新冷却bossbar
execute store result bossbar stardew:block_cooldown value run scoreboard players get @s sd_block_cooldown

# 冷却结束
execute if score @s sd_block_cooldown matches ..0 run function stardew:combat/cooldown/end_block
