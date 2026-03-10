# stardew:debug/dummy/init.mcfunction
# 初始化稻草人假人数据

# 设置超高血量（让它看起来"无敌"）
scoreboard players set @s sd_monster_hp 999999
scoreboard players set @s sd_monster_max_hp 999999

# 设置攻击力为0（不会造成伤害）
scoreboard players set @s sd_monster_damage 0

# 初始化DPS追踪数据
scoreboard players set @s sd_dummy_total_dmg 0
scoreboard players set @s sd_dummy_tick_timer 0
scoreboard players set @s sd_dummy_dps 0
scoreboard players set @s sd_dummy_last_hp 999999

# 标记为已初始化
tag @s remove sd_dummy_init
tag @s add sd_dummy_active

# 给予发光效果便于识别
effect give @s minecraft:glowing infinite 0 true