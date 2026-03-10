# data/stardew/function/status/apply_debuff_with_message.mcfunction
# 应用 Debuff 并显示提示信息
# 参数: type, duration, level, name

# 将参数存储到 temp storage
$data merge storage stardew:temp {status:{type:"$(type)",duration:$(duration),level:$(level)}}

# 应用 Debuff
function stardew:status/apply_debuff with storage stardew:temp status

# 计算秒数
$scoreboard players set #duration_ticks sd_temp $(duration)
scoreboard players set #20 sd_const 20
scoreboard players operation #duration_seconds sd_temp = #duration_ticks sd_temp
scoreboard players operation #duration_seconds sd_temp /= #20 sd_const

# 显示效果信息
$tellraw @s [{"text":"[Debuff] ","color":"red","bold":true},{"text":"$(name) ","color":"yellow"},{"text":"等级 ","color":"gray"},{"text":"$(level)","color":"white"},{"text":" | ","color":"dark_gray"},{"text":"持续 ","color":"gray"},{"score":{"name":"#duration_seconds","objective":"sd_temp"},"color":"white"},{"text":"秒","color":"gray"}]

# 清除临时数据
data remove storage stardew:temp status
