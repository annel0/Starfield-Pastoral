# 应用钓鱼等级加成
# fishing值直接加到sd_fishing_lvl分数上

# 读取钓鱼加成数值
execute store result score #fishing_bonus sd_temp run data get storage stardew:temp boots_effects.fishing 1

# 加到钓鱼等级上（需要在钓鱼系统中处理，这里只是标记）
# 钓鱼系统应该读取这个加成并应用到最终等级
scoreboard players operation @s sd_fishing_bonus = #fishing_bonus sd_temp
