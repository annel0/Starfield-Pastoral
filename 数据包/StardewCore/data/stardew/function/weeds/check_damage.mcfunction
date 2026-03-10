# 检测杂草是否被攻击（剑/斧子/镐子）或被镰刀右键
# 执行者: interaction实体 (@s, tag=weed_hitbox)

# 左键攻击 - 破坏杂草（剑/斧/镐）
execute if data entity @s attack run execute on attacker run function stardew:weeds/on_weed_attack

# 右键交互 - 检测是否拿着镰刀，如果是则触发镰刀收割
execute if data entity @s interaction run execute on target run function stardew:weeds/check_scythe_interaction

# 清除所有交互数据
data remove entity @s attack
data remove entity @s interaction
