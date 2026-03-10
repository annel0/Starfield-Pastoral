# stardew:monsters/drops/give_xp.mcfunction
# 根据怪物类型给予战斗经验（基于官方wiki数据）

# 初始化经验值为0
scoreboard players set #monster_xp sd_temp 0

# 调用经验值表获取该怪物的经验值
function stardew:combat/xp/monster_xp_table

# 给予附近玩家经验（假定最近的玩家是击杀者）
execute as @a[distance=..15] run scoreboard players operation @s sd_combat_xp += #monster_xp sd_temp

# 显示经验提示
execute as @a[distance=..15] run tellraw @s [{"text":"⚔ ","color":"gold"},{"text":"战斗 +","color":"gold"},{"score":{"name":"#monster_xp","objective":"sd_temp"},"color":"yellow"},{"text":" XP","color":"gold"}]

# 播放经验获得音效
execute as @a[distance=..15] at @s run playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 0.5 1.5

