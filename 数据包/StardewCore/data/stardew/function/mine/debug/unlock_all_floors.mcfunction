# stardew:mine/debug/unlock_all_floors.mcfunction
# 调试: 解锁所有电梯楼层

scoreboard players set @s sd_mine_deepest 100

tellraw @s {"text":"[调试] 所有电梯楼层已解锁！","color":"green"}
