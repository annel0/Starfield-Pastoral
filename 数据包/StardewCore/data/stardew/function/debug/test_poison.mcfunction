# 测试中毒效果
tellraw @s {"text":"=== 中毒效果测试 ===","color":"dark_green"}
tellraw @s {"text":"应用中毒(level 2, 10秒)...","color":"gray"}

# 显示测试前状态
tellraw @s [{"text":"测试前生命值: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_health"},"color":"white"}]

# 计算免疫减免比例（使用靴子的免疫系统）
execute if score @s sd_equip_boots matches 1.. run function stardew:equipment/effects/apply_boots_immunity
execute unless score @s sd_equip_boots matches 1.. run data merge storage stardew:temp {immunity_reduction:100}

# 应用中毒Debuff（带免疫计算和提示信息）
function stardew:equipment/effects/apply_debuff_with_immunity {type:"poison",duration:200,level:2,name:"中毒"}

tellraw @s {"text":"✓ 查看上方的效果提示信息","color":"green"}
tellraw @s {"text":"每1秒扣除5点sd_health，持续10秒","color":"gray"}
tellraw @s {"text":"观察你的生命值是否每秒减少5点","color":"yellow"}
