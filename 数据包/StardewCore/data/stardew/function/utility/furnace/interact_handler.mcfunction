# data/stardew/function/utility/furnace/interact_handler.mcfunction
# 处理玩家与熔炉的右键交互
# 执行者: interaction 实体 (@s)

# DEBUG: 测试
#tellraw @a {"text":"[DEBUG] 熔炉interact_handler被调用","color":"yellow"}

# 0. 标记当前熔炉
tag @s add sd_interacting_furnace

# 1. 如果熔炉已完成（状态为2），收取产物（任意右键）
execute if score @s sd_furnace_state matches 2 on target run function stardew:utility/furnace/collect_product
execute if score @s sd_furnace_state matches 2 run tag @s remove sd_interacting_furnace
execute if score @s sd_furnace_state matches 2 run return 1

# 2. 如果熔炉空闲，检查玩家手持物品
# 铜粒 (CMD 7003) - 熔炼铜锭 (30分钟)
execute if score @s sd_furnace_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=7003] run function stardew:utility/furnace/smelt_copper

# 铁粒 (CMD 7004) - 熔炼铁锭 (120分钟)
execute if score @s sd_furnace_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=7004] run function stardew:utility/furnace/smelt_iron

# 金粒 (CMD 7005) - 熔炼金锭 (300分钟，5小时)
execute if score @s sd_furnace_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=7005] run function stardew:utility/furnace/smelt_gold

# 钻石 (CMD 7006) - 熔炼钻石锭 (480分钟，8小时)
execute if score @s sd_furnace_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=7006] run function stardew:utility/furnace/smelt_diamond

# 石英 (CMD 7101) - 熔炼精炼石英 (90分钟，1.5小时)
execute if score @s sd_furnace_state matches 0 on target if items entity @s weapon.mainhand minecraft:paper[custom_model_data=7101] run function stardew:utility/furnace/smelt_quartz

# 3. 清除标记
tag @s remove sd_interacting_furnace
