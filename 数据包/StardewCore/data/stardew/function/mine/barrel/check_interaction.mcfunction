# stardew:mine/barrel/check_interaction.mcfunction
# 检测玩家攻击木桶
# 执行者: interaction 实体 (@s)

# 左键攻击 - 破坏木桶
# 标记当前interaction为目标，然后切换到攻击者执行
execute if data entity @s attack run tag @s add sd_current_barrel_target
execute if data entity @s attack on attacker run function stardew:mine/barrel/break

# 清除交互状态(防止重复触发)
data remove entity @s attack
data remove entity @s interaction
