# 测试粘液Debuff（测试免疫）
tellraw @s {"text":"=== Debuff测试 ===","color":"dark_green"}
tellraw @s {"text":"应用粘液效果(原始10秒=200 ticks)...","color":"gray"}

# 设置原始持续时间
scoreboard players set #original_duration sd_temp 200

# 应用免疫计算
execute if score @s sd_equip_boots matches 1.. run function stardew:equipment/effects/apply_boots_immunity
execute unless score @s sd_equip_boots matches 1.. run data merge storage stardew:temp {immunity_reduction:100}

# 计算减免后的持续时间
execute store result score #immunity_reduction sd_temp run data get storage stardew:temp immunity_reduction 1
scoreboard players operation #final_duration sd_temp = #original_duration sd_temp
scoreboard players operation #final_duration sd_temp *= #immunity_reduction sd_temp
scoreboard players set #100 sd_const 100
scoreboard players operation #final_duration sd_temp /= #100 sd_const

# 显示结果
tellraw @s [{"text":"原始持续: ","color":"yellow"},{"score":{"name":"#original_duration","objective":"sd_temp"},"color":"white"},{"text":" ticks (10秒)","color":"gray"}]
tellraw @s [{"text":"免疫减免: ","color":"yellow"},{"score":{"name":"#immunity_reduction","objective":"sd_temp"},"color":"white"},{"text":"%","color":"gray"}]
tellraw @s [{"text":"实际持续: ","color":"yellow"},{"score":{"name":"#final_duration","objective":"sd_temp"},"color":"white"},{"text":" ticks","color":"gray"}]

# 应用Debuff
function stardew:equipment/effects/apply_debuff_with_immunity {type:"slime",duration:200,level:1,name:"粘液"}

tellraw @s {"text":"✓ 查看上方的效果提示信息","color":"green"}
