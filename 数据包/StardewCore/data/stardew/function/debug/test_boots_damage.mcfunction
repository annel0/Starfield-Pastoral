# 模拟受到10点伤害（测试防御）
tellraw @s {"text":"=== 伤害测试 ===","color":"red"}
tellraw @s {"text":"模拟受到10点伤害...","color":"gray"}

# 设置测试伤害
scoreboard players set #damage_taken sd_temp 10
scoreboard players set #original_damage sd_temp 10

# 应用靴子防御
scoreboard players set #boots_defense sd_temp 0
execute if score @s sd_equip_boots matches 1.. run function stardew:equipment/effects/apply_boots_defense

# 计算减免后的伤害
execute if score #boots_defense sd_temp matches 1.. run scoreboard players operation #damage_taken sd_temp -= #boots_defense sd_temp

# 确保伤害不为负数
execute if score #damage_taken sd_temp matches ..0 run scoreboard players set #damage_taken sd_temp 0

# 显示结果
tellraw @s [{"text":"原始伤害: ","color":"yellow"},{"score":{"name":"#original_damage","objective":"sd_temp"},"color":"white"}]
tellraw @s [{"text":"防御减免: ","color":"yellow"},{"score":{"name":"#boots_defense","objective":"sd_temp"},"color":"white"}]
tellraw @s [{"text":"实际伤害: ","color":"yellow"},{"score":{"name":"#damage_taken","objective":"sd_temp"},"color":"white"}]

# 显示当前生命值
tellraw @s [{"text":"当前生命值: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_health"},"color":"white"},{"text":"/","color":"gray"},{"score":{"name":"@s","objective":"sd_max_health"},"color":"white"}]

# 实际扣除我们系统的生命值 (sd_health)
scoreboard players operation @s sd_health -= #damage_taken sd_temp

# 显示扣血后的生命值
tellraw @s [{"text":"扣血后生命值: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_health"},"color":"red"}]

# 伤害反馈效果
particle minecraft:damage_indicator ~ ~1 ~ 0.3 0.5 0.3 0.1 10
playsound minecraft:entity.player.hurt player @a ~ ~ ~ 1 1

# 检查是否昏倒
execute if score @s sd_health matches ..0 run function stardew:combat/player_faint
