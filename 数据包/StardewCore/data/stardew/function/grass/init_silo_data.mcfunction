# 初始化玩家的筒仓数据
# 执行者: 玩家 (@s)

# 如果玩家还没有筒仓数据，初始化为0
execute unless score @s sd_silo_count matches 0.. run scoreboard players set @s sd_silo_count 0
execute unless score @s sd_hay_stored matches 0.. run scoreboard players set @s sd_hay_stored 0
execute unless score @s sd_hay_capacity matches 0.. run scoreboard players set @s sd_hay_capacity 0