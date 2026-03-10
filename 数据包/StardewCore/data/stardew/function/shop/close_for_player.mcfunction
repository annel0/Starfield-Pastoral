# 关闭商店 - 玩家个体处理

# 传送回主世界spawn点
execute in minecraft:overworld run tp @s 0 100 0

# 重置商店状态
scoreboard players set @s sd_in_shop 0
scoreboard players set @s sd_shop_season 0
scoreboard players set @s sd_shop_page 0