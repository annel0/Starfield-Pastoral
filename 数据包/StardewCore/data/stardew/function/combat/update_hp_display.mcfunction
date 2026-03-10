# 更新怪物名字显示血量
# 宏参数: name, hp, max_hp

# 根据血量百分比设置不同颜色
# >75% = 绿色, 50-75% = 黄色, 25-50% = 金色, <25% = 红色

$execute if score #hp_percent sd_temp matches 76.. run data merge entity @s {CustomName:'[$(name),{"text":" [","color":"gray"},{"text":"$(hp)","color":"green"},{"text":"/","color":"gray"},{"text":"$(max_hp)","color":"green"},{"text":"]","color":"gray"}]'}

$execute if score #hp_percent sd_temp matches 51..75 run data merge entity @s {CustomName:'[$(name),{"text":" [","color":"gray"},{"text":"$(hp)","color":"yellow"},{"text":"/","color":"gray"},{"text":"$(max_hp)","color":"yellow"},{"text":"]","color":"gray"}]'}

$execute if score #hp_percent sd_temp matches 26..50 run data merge entity @s {CustomName:'[$(name),{"text":" [","color":"gray"},{"text":"$(hp)","color":"gold"},{"text":"/","color":"gray"},{"text":"$(max_hp)","color":"gold"},{"text":"]","color":"gray"}]'}

$execute if score #hp_percent sd_temp matches ..25 run data merge entity @s {CustomName:'[$(name),{"text":" [","color":"gray"},{"text":"$(hp)","color":"red","bold":true},{"text":"/","color":"gray"},{"text":"$(max_hp)","color":"red"},{"text":"]","color":"gray"}]'}
