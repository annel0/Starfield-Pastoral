# stardew:debug/dummy/calculate_dps.mcfunction
# 计算并显示DPS（每秒调用一次）

# 将本秒的总伤害记录为DPS
scoreboard players operation @s sd_dummy_dps = @s sd_dummy_total_dmg

# 更新sidebar的DPS显示（只在有玩家在附近时显示）
execute if entity @a[distance=..15] run team modify sd_ui_5 suffix [{"text":"  ⚔ ","color":"gold"},{"text":"DPS: ","color":"gray"},{"score":{"name":"@e[tag=sd_dummy_active,limit=1,sort=nearest]","objective":"sd_dummy_dps"},"color":"red","bold":true},{"text":"/s","color":"gray"}]

# 如果DPS为0，显示未激活状态
execute unless score @s sd_dummy_dps matches 1.. run team modify sd_ui_5 suffix [{"text":"  ⚔ ","color":"gray"},{"text":"未激活","color":"dark_gray"}]

# 重置计数器
scoreboard players set @s sd_dummy_total_dmg 0
scoreboard players set @s sd_dummy_tick_timer 0
