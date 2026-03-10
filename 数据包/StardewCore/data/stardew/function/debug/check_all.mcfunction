# 全面检查作物数据
tellraw @a [{"text":"====== 作物全面检查 ======","color":"gold","bold":true}]

# 检查作物marker
execute as @e[type=marker,tag=sd_crop,distance=..5,limit=1] run tellraw @a [{"text":"[作物Marker] sd_crop_age: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_crop_age"},"color":"white"}]
execute as @e[type=marker,tag=sd_crop,distance=..5,limit=1] run tellraw @a [{"text":"[作物Marker] sd_max_crop_age: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_max_crop_age"},"color":"white"}]
execute as @e[type=marker,tag=sd_crop,distance=..5,limit=1] run tellraw @a [{"text":"[作物Marker] sd_original_max_age: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_original_max_age"},"color":"white"}]
execute as @e[type=marker,tag=sd_crop,distance=..5,limit=1] run tellraw @a [{"text":"[作物Marker] sd_fertilizer_type: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_fertilizer_type"},"color":"white"}]
execute as @e[type=marker,tag=sd_crop,distance=..5,limit=1] run tellraw @a [{"text":"[作物Marker] sd_fertilizer_level: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_fertilizer_level"},"color":"white"}]

tellraw @a [{"text":""}]

# 检查text_display
execute as @e[type=text_display,tag=sd_info_text,distance=..5,limit=1] run tellraw @a [{"text":"[Text Display] sd_crop_age: ","color":"aqua"},{"score":{"name":"@s","objective":"sd_crop_age"},"color":"white"}]
execute as @e[type=text_display,tag=sd_info_text,distance=..5,limit=1] run tellraw @a [{"text":"[Text Display] sd_max_crop_age: ","color":"aqua"},{"score":{"name":"@s","objective":"sd_max_crop_age"},"color":"white"}]

tellraw @a [{"text":"============================","color":"gold"}]
