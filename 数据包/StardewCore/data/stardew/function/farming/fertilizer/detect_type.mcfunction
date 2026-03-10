# data/stardew/functions/farming/fertilizer/detect_type.mcfunction
# 检测肥料类型并储存到临时记分板

# 重置临时分数
scoreboard players set @s sd_temp_fert_type 0
scoreboard players set @s sd_temp_fert_level 0

# 品质肥料 (quality)
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4001] run scoreboard players set @s sd_temp_fert_type 1
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4001] run scoreboard players set @s sd_temp_fert_level 1
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4002] run scoreboard players set @s sd_temp_fert_type 1
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4002] run scoreboard players set @s sd_temp_fert_level 2
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4003] run scoreboard players set @s sd_temp_fert_type 1
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4003] run scoreboard players set @s sd_temp_fert_level 3

# 生长激素 (speed)
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4004] run scoreboard players set @s sd_temp_fert_type 2
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4004] run scoreboard players set @s sd_temp_fert_level 1
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4005] run scoreboard players set @s sd_temp_fert_type 2
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4005] run scoreboard players set @s sd_temp_fert_level 2
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4006] run scoreboard players set @s sd_temp_fert_type 2
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4006] run scoreboard players set @s sd_temp_fert_level 3

# 保湿土壤 (retaining)
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4007] run scoreboard players set @s sd_temp_fert_type 3
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4007] run scoreboard players set @s sd_temp_fert_level 1
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4008] run scoreboard players set @s sd_temp_fert_type 3
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4008] run scoreboard players set @s sd_temp_fert_level 2
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4009] run scoreboard players set @s sd_temp_fert_type 3
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4009] run scoreboard players set @s sd_temp_fert_level 3

# 树肥 (tree) - 特殊处理,不需要施加到耕地
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4010] run function stardew:farming/fertilizer/apply_tree
execute if items entity @s weapon.mainhand carrot_on_a_stick[custom_model_data=4010] run return 1

# 如果检测到了有效肥料类型,执行射线检测
execute if score @s sd_temp_fert_type matches 1..3 run function stardew:farming/fertilizer/raycast_start
