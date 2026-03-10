# 恢复玩家状态到满值
scoreboard players operation @s sd_health = @s sd_max_health
scoreboard players operation @s sd_energy = @s sd_max_energy

tellraw @s {"text":"✓ 生命值和能量值已恢复至满值！","color":"green"}
tellraw @s [{"text":"生命值: ","color":"red"},{"score":{"name":"@s","objective":"sd_health"},"color":"white"}]
tellraw @s [{"text":"能量值: ","color":"gold"},{"score":{"name":"@s","objective":"sd_energy"},"color":"white"}]

# 播放治疗音效
playsound minecraft:entity.player.levelup player @s ~ ~ ~ 0.5 2
particle minecraft:heart ~ ~1 ~ 0.5 0.5 0.5 0.1 10
