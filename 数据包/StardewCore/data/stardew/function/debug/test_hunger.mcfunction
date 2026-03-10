# 测试饥饿效果
tellraw @s {"text":"=== 饥饿效果测试 ===","color":"dark_red"}
tellraw @s {"text":"应用饥饿(level 2, 10秒)...","color":"gray"}

# 显示测试前状态
tellraw @s [{"text":"测试前能量值: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_energy"},"color":"white"}]

# 计算免疫减免比例（使用靴子的免疫系统）
execute if score @s sd_equip_boots matches 1.. run function stardew:equipment/effects/apply_boots_immunity
execute unless score @s sd_equip_boots matches 1.. run data merge storage stardew:temp {immunity_reduction:100}

# 应用饥饿Debuff（带免疫计算和提示信息）
function stardew:equipment/effects/apply_debuff_with_immunity {type:"hunger",duration:200,level:2,name:"饥饿"}

tellraw @s {"text":"✓ 查看上方的效果提示信息","color":"green"}
tellraw @s {"text":"每2秒扣除10点sd_energy，持续10秒","color":"gray"}
tellraw @s {"text":"观察你的能量条是否每2秒减少10点","color":"yellow"}
