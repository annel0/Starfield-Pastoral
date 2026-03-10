# data/stardew/function/status/apply_buff.mcfunction
# 应用 Buff 效果
# 使用 macro 调用: function stardew:status/apply_buff {type:"speed",duration:200,level:1}

# 速度效果
execute if data storage stardew:temp status{type:"speed"} run scoreboard players set @s sd_buff_speed 1
$execute if data storage stardew:temp status{type:"speed"} run scoreboard players set @s sd_speed_duration $(duration)
$execute if data storage stardew:temp status{type:"speed"} run scoreboard players set @s sd_speed_level $(level)

# 力量效果
execute if data storage stardew:temp status{type:"strength"} run scoreboard players set @s sd_buff_strength 1
$execute if data storage stardew:temp status{type:"strength"} run scoreboard players set @s sd_strength_duration $(duration)
$execute if data storage stardew:temp status{type:"strength"} run scoreboard players set @s sd_strength_level $(level)

# 再生效果
execute if data storage stardew:temp status{type:"regen"} run scoreboard players set @s sd_buff_regen 1
$execute if data storage stardew:temp status{type:"regen"} run scoreboard players set @s sd_regen_duration $(duration)
$execute if data storage stardew:temp status{type:"regen"} run scoreboard players set @s sd_regen_level $(level)

# 抗性效果
execute if data storage stardew:temp status{type:"resistance"} run scoreboard players set @s sd_buff_resistance 1
$execute if data storage stardew:temp status{type:"resistance"} run scoreboard players set @s sd_resistance_duration $(duration)
$execute if data storage stardew:temp status{type:"resistance"} run scoreboard players set @s sd_resistance_level $(level)

# 幸运效果
execute if data storage stardew:temp status{type:"luck"} run scoreboard players set @s sd_buff_luck 1
$execute if data storage stardew:temp status{type:"luck"} run scoreboard players set @s sd_luck_duration $(duration)
$execute if data storage stardew:temp status{type:"luck"} run scoreboard players set @s sd_luck_level $(level)

# 护盾效果
execute if data storage stardew:temp status{type:"shield"} run scoreboard players set @s sd_buff_shield 1
$execute if data storage stardew:temp status{type:"shield"} run scoreboard players set @s sd_shield_duration $(duration)
$execute if data storage stardew:temp status{type:"shield"} run scoreboard players set @s sd_shield_amount $(level)

# 效果提示音效
playsound minecraft:block.enchantment_table.use player @s ~ ~ ~ 0.5 1.5
