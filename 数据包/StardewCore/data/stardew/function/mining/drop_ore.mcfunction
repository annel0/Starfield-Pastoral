# stardew:mining/drop_ore.mcfunction
# 矿物掉落逻辑 - 宏函数
# 执行位置: 破碎的矿石位置
# 参数:
# $(ore_type) - 矿石类型
# $(cmd) - CMD值
# $(name) - 显示名称
# $(price) - 售价

# ==========================================
# 平缓掉落公式:
# base = 1 + floor(level / 5)  → 0-4级=1个, 5-9级=2个, 10级=3个
# bonus = random(0-99) < (level * 10) → 每级增加10%额外掉落概率
# final = base + bonus (最大5个)
# ==========================================

# 1. 计算基础掉落数 (1 + floor(lvl/5))
scoreboard players operation #drop_count sd_temp_val = #mining_lvl sd_temp_val
scoreboard players operation #drop_count sd_temp_val /= #5 sd_const
scoreboard players add #drop_count sd_temp_val 1

# 2. 计算额外掉落概率 (level * 10%)
scoreboard players operation #bonus_threshold sd_temp_val = #mining_lvl sd_temp_val
scoreboard players operation #bonus_threshold sd_temp_val *= #10 sd_const

# 3. 随机判定额外掉落 (0-99随机数)
execute store result score #random sd_temp_val run random value 0..99

# 如果随机数 < 阈值，则+1个
execute if score #random sd_temp_val < #bonus_threshold sd_temp_val run scoreboard players add #drop_count sd_temp_val 1

# 4. 再次判定第二次额外掉落 (10级时有100%+100%=最多5个)
execute store result score #random2 sd_temp_val run random value 0..99
execute if score #random2 sd_temp_val < #bonus_threshold sd_temp_val run scoreboard players add #drop_count sd_temp_val 1

# 5. 限制最大5个，最少1个
execute if score #drop_count sd_temp_val matches ..0 run scoreboard players set #drop_count sd_temp_val 1
execute if score #drop_count sd_temp_val matches 6.. run scoreboard players set #drop_count sd_temp_val 5

# 生成物品 (通过循环生成count次)
$execute if score #drop_count sd_temp_val matches 1.. run summon minecraft:item ~ ~1 ~ {Item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":$(cmd),"minecraft:max_stack_size":99,"minecraft:custom_name":'{"text":"$(name)","color":"white","italic":false}',"minecraft:custom_data":{stardew_item:1,item_type:"resource",resource_type:"$(ore_type)",sd_price:$(price)}}}}
$execute if score #drop_count sd_temp_val matches 2.. run summon minecraft:item ~ ~1 ~ {Item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":$(cmd),"minecraft:max_stack_size":99,"minecraft:custom_name":'{"text":"$(name)","color":"white","italic":false}',"minecraft:custom_data":{stardew_item:1,item_type:"resource",resource_type:"$(ore_type)",sd_price:$(price)}}}}
$execute if score #drop_count sd_temp_val matches 3.. run summon minecraft:item ~ ~1 ~ {Item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":$(cmd),"minecraft:max_stack_size":99,"minecraft:custom_name":'{"text":"$(name)","color":"white","italic":false}',"minecraft:custom_data":{stardew_item:1,item_type:"resource",resource_type:"$(ore_type)",sd_price:$(price)}}}}
$execute if score #drop_count sd_temp_val matches 4.. run summon minecraft:item ~ ~1 ~ {Item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":$(cmd),"minecraft:max_stack_size":99,"minecraft:custom_name":'{"text":"$(name)","color":"white","italic":false}',"minecraft:custom_data":{stardew_item:1,item_type:"resource",resource_type:"$(ore_type)",sd_price:$(price)}}}}
$execute if score #drop_count sd_temp_val matches 5.. run summon minecraft:item ~ ~1 ~ {Item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":$(cmd),"minecraft:max_stack_size":99,"minecraft:custom_name":'{"text":"$(name)","color":"white","italic":false}',"minecraft:custom_data":{stardew_item:1,item_type:"resource",resource_type:"$(ore_type)",sd_price:$(price)}}}}
