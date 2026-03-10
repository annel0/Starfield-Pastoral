# data/stardew/functions/control/sneak_update.mcfunction
# 执行者：玩家 @s
# 作用：根据 sneak_time 的变化，判断这一 tick 是否处于蹲下状态

# 1. 默认认为没蹲
scoreboard players set @s sd_is_sneaking 0

# 2. 如果本 tick 的 sneak_time > 上一 tick ，说明正在/仍在蹲
execute if score @s sd_sneak_time > @s sd_sneak_last run scoreboard players set @s sd_is_sneaking 1

# 3. 更新“上一 tick”的值
scoreboard players operation @s sd_sneak_last = @s sd_sneak_time
