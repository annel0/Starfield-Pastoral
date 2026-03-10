# stardew:debug/unlock_all_floors.mcfunction
# 解锁所有楼层（测试用）
# 使用方法: /function stardew:debug/unlock_all_floors

scoreboard players set @s sd_mine_deepest 100
tellraw @s {"text":"✓ 已解锁所有楼层 (1-100)！电梯现在可以前往任意楼层。","color":"green"}
playsound minecraft:entity.player.levelup master @s ~ ~ ~ 1 1.5
