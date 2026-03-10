# data/stardew/functions/tree/main_handler.mcfunction
# [执行者: 交互实体 (树)]

# 1. 处理左键攻击
# 检测 attack NBT
# 1.1 如果树上有提取器，且玩家拿镐子，则拆除提取器
execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=201] as @e[type=interaction,tag=sd_tree,tag=sd_has_tapper,limit=1,sort=nearest] run function stardew:utility/tapper/break_tapper
execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=201] run data remove entity @s attack
execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=201] run data remove entity @s interaction
execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=201] run return 1

execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=202] as @e[type=interaction,tag=sd_tree,tag=sd_has_tapper,limit=1,sort=nearest] run function stardew:utility/tapper/break_tapper
execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=202] run data remove entity @s attack
execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=202] run data remove entity @s interaction
execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=202] run return 1

execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=203] as @e[type=interaction,tag=sd_tree,tag=sd_has_tapper,limit=1,sort=nearest] run function stardew:utility/tapper/break_tapper
execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=203] run data remove entity @s attack
execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=203] run data remove entity @s interaction
execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=203] run return 1

execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=204] as @e[type=interaction,tag=sd_tree,tag=sd_has_tapper,limit=1,sort=nearest] run function stardew:utility/tapper/break_tapper
execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=204] run data remove entity @s attack
execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=204] run data remove entity @s interaction
execute if data entity @s attack if entity @s[tag=sd_has_tapper] on attacker if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=204] run return 1

# 1.2 否则正常砍树
execute if data entity @s attack run function stardew:tree/handle_chop

# 2. 处理右键交互
# 2.1 如果手持树液提取器，则放置提取器
execute if data entity @s interaction on target if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=3003] run tag @s add sd_placing_tapper
execute if data entity @s interaction if entity @a[tag=sd_placing_tapper,distance=..6] run function stardew:utility/tapper/place_tapper
execute if data entity @s interaction if entity @a[tag=sd_placing_tapper,distance=..6] run data remove entity @s interaction
execute if data entity @s interaction if entity @a[tag=sd_placing_tapper,distance=..6] run data remove entity @s attack
execute if data entity @s interaction if entity @a[tag=sd_placing_tapper,distance=..6] run return 1

# 2.2 如果树上有完成的提取器，收取产物
execute if data entity @s interaction if score @s sd_tapper_state matches 2 on target run tag @s add sd_collecting_tapper_player
execute if data entity @s interaction if score @s sd_tapper_state matches 2 run function stardew:utility/tapper/collect_product
execute if data entity @s interaction if score @s sd_tapper_state matches 2 run data remove entity @s interaction
execute if data entity @s interaction if score @s sd_tapper_state matches 2 run data remove entity @s attack
execute if data entity @s interaction if score @s sd_tapper_state matches 2 run return 1

# 2.3 否则正常摇树
execute if data entity @s interaction run function stardew:tree/handle_shake

# 3. 清理状态 (必须清理，否则信号会残留)
data remove entity @s interaction
data remove entity @s attack