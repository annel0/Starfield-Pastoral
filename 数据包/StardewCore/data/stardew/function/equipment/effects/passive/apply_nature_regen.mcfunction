# 精灵祝福 - 自然回复
# 每10秒回复5点生命和能量

# 初始化计时器
execute unless score @s sd_nature_regen_timer matches 0.. run scoreboard players set @s sd_nature_regen_timer 0

# 计时增加
scoreboard players add @s sd_nature_regen_timer 1

# 每200 ticks (10秒) 触发一次回复
execute if score @s sd_nature_regen_timer matches 200.. run function stardew:equipment/effects/passive/nature_regen_heal
