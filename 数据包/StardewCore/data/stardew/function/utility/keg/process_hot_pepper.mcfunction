# process_hot_pepper.mcfunction - CMD: 2704,2705,2706
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2704]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2704] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2705]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2705] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2706]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2706] 1
data modify storage stardew:keg processing set value {output_cmd:107,type:6,time:4800}
function stardew:utility/keg/start_process with storage stardew:keg processing
