# 蓄力重击技能 (Heavy Charge)
# 需要蓄力满后右键释放

# 1. 检查技能冷却
execute if score @s sd_skill_cooldown matches 1.. run return 0

# 2. 检查攻击冷却
execute if score @s sd_attack_cooldown matches 1.. run return 0

# 3. 检查是否蓄力完成
execute unless score @s sd_heavy_charge_ready matches 1 run return 0

# 4. 检查目标
execute positioned ~ ~1.5 ~ positioned ^ ^ ^3 run tag @e[type=!#minecraft:non_attackable,type=!minecraft:player,type=!minecraft:item,distance=..3,sort=nearest,limit=1] add sd_heavy_charge_check

# 5. 无目标则返回
execute unless entity @e[tag=sd_heavy_charge_check] run return 0
tag @e[tag=sd_heavy_charge_check] remove sd_heavy_charge_check

# 6. 清除蓄力状态
scoreboard players set @s sd_heavy_charge_time 0
scoreboard players set @s sd_heavy_charge_ready 0
scoreboard players set @s sd_charge_ready 0
scoreboard players set @s sd_special_type 0

# 7. 触发技能冷却（10秒）
scoreboard players set @s sd_skill_cooldown 200
function stardew:combat/cooldown/set_heavy_charge_cooldown_max

# 8. 标记正在使用蓄力重击冷却
tag @s add sd_using_heavy_charge

# 9. 执行攻击
function stardew:combat/weapon/heavy_charge_attack
