# stardew:monsters/debug/clear_all_monsters.mcfunction
# 清理所有怪物

execute in stardew:mine run kill @e[tag=sd_monster]
tellraw @s {"text":"[DEBUG] 已清理所有怪物","color":"red"}
