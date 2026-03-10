# stardew:monsters/init.mcfunction
# 怪物系统初始化

# 创建记分板
scoreboard objectives add sd_monster_kill minecraft.custom:minecraft.mob_kills "怪物击杀数"
scoreboard objectives add sd_combat_xp dummy "战斗经验"
scoreboard objectives add sd_combat_level dummy "战斗等级"
scoreboard objectives add sd_monster_hp dummy "怪物当前血量"
scoreboard objectives add sd_monster_max_hp dummy "怪物最大血量"

# 初始化计分板
execute as @a unless score @s sd_combat_level matches 0.. run scoreboard players set @s sd_combat_level 0
execute as @a unless score @s sd_combat_xp matches 0.. run scoreboard players set @s sd_combat_xp 0

tellraw @a {"text":"[怪物系统] 已初始化 (v2.0)","color":"green"}
