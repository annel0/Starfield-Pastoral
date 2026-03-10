# data/stardew/function/equipment/effects/apply_debuff_with_immunity.mcfunction
# 应用 Debuff 并根据免疫值减少持续时间
# 参数: type, duration, level, name

# 计算减免后的持续时间
# 实际持续时间 = 原始持续时间 * (immunity_reduction / 100)
$scoreboard players set #original_duration sd_temp $(duration)
execute store result score #immunity_reduction sd_temp run data get storage stardew:temp immunity_reduction 1
scoreboard players operation #final_duration sd_temp = #original_duration sd_temp
scoreboard players operation #final_duration sd_temp *= #immunity_reduction sd_temp
scoreboard players operation #final_duration sd_temp /= #100 sd_const

# 将计算后的持续时间存储
execute store result storage stardew:temp status.duration int 1 run scoreboard players get #final_duration sd_temp
$data merge storage stardew:temp {status:{type:"$(type)",level:$(level)}}

# 应用 Debuff
function stardew:status/apply_debuff with storage stardew:temp status

# 显示 Debuff 应用提示
# 计算实际秒数 (ticks / 20)
scoreboard players set #20 sd_const 20
scoreboard players operation #final_seconds sd_temp = #final_duration sd_temp
scoreboard players operation #final_seconds sd_temp /= #20 sd_const

# 显示效果信息
$tellraw @s [{"text":"[Debuff] ","color":"red","bold":true},{"text":"$(name) ","color":"yellow"},{"text":"等级 ","color":"gray"},{"text":"$(level)","color":"white"},{"text":" | ","color":"dark_gray"},{"text":"持续 ","color":"gray"},{"score":{"name":"#final_seconds","objective":"sd_temp"},"color":"white"},{"text":"秒","color":"gray"}]

# 如果免疫值减少了持续时间，显示原始时间
execute if score #immunity_reduction sd_temp matches ..99 run scoreboard players operation #original_seconds sd_temp = #original_duration sd_temp
execute if score #immunity_reduction sd_temp matches ..99 run scoreboard players operation #original_seconds sd_temp /= #20 sd_const
execute if score #immunity_reduction sd_temp matches ..99 run tellraw @s [{"text":"  ","color":"gray"},{"text":"↳ 免疫减少 ","color":"aqua"},{"score":{"name":"#original_seconds","objective":"sd_temp"},"color":"gray"},{"text":"秒 → ","color":"gray"},{"score":{"name":"#final_seconds","objective":"sd_temp"},"color":"white"},{"text":"秒","color":"gray"}]

# 清除临时数据
data remove storage stardew:temp status
