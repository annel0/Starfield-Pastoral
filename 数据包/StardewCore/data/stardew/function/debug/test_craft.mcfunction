# data/stardew/function/debug/test_craft.mcfunction
# 测试合成检测

# 检测材料
execute store result score #StoneCount sd_temp run clear @s paper[custom_model_data=7001] 0
execute store result score #CopperCount sd_temp run clear @s paper[custom_model_data=7003] 0

tellraw @s [{"text":"[DEBUG] 石头数量: ","color":"gray"},{"score":{"name":"#StoneCount","objective":"sd_temp"},"color":"yellow"}]
tellraw @s [{"text":"[DEBUG] 铜矿数量: ","color":"gray"},{"score":{"name":"#CopperCount","objective":"sd_temp"},"color":"yellow"}]
