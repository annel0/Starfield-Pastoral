# process_blueberry.mcfunction - CMD: 2804,2805,2806
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2804]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2804] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2805]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2805] 1
execute store result score #material_quality sd_temp if items entity @s weapon.mainhand minecraft:paper[custom_model_data=2806]
execute if score #material_quality sd_temp matches 1.. run clear @s minecraft:paper[custom_model_data=2806] 1
data modify storage stardew:keg processing set value {output_cmd:103,type:2,time:4800}
function stardew:utility/keg/start_process with storage stardew:keg processing
