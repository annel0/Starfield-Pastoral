# 测试钓鱼加成 - 装备美人鱼靴
scoreboard players set @s sd_equip_boots 1
data modify storage stardew:equipment boots set value {defense:3,immunity:5,effects:{fishing:2}}

# 检查原始钓鱼等级
scoreboard players set #original_fishing sd_temp 0
scoreboard players operation #original_fishing sd_temp = @s sd_fishing_lvl

tellraw @s [{"text":"[测试] ","color":"green"},{"text":"已装备美人鱼靴 (钓鱼等级+2)","color":"yellow"}]
tellraw @s [{"text":"原始钓鱼等级: ","color":"gray"},{"score":{"name":"#original_fishing","objective":"sd_temp"},"color":"white"}]
tellraw @s [{"text":"当前钓鱼加成: ","color":"gray"},{"score":{"name":"@s","objective":"sd_fishing_bonus"},"color":"aqua"}]
tellraw @s [{"text":"提示: ","color":"gray"},{"text":"装备美人鱼靴后，钓鱼系统会自动获得+2级加成","color":"white"}]
