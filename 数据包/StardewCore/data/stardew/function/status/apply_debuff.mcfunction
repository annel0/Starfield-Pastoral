# data/stardew/function/status/apply_debuff.mcfunction
# 应用 Debuff 效果
# 使用 macro 调用: function stardew:status/apply_debuff {type:"slime",duration:200,level:1}

# 粘液效果
execute if data storage stardew:temp status{type:"slime"} run scoreboard players set @s sd_debuff_slime 1
$execute if data storage stardew:temp status{type:"slime"} run scoreboard players set @s sd_slime_duration $(duration)
$execute if data storage stardew:temp status{type:"slime"} run scoreboard players set @s sd_slime_level $(level)

# 冰冻效果
execute if data storage stardew:temp status{type:"frozen"} run scoreboard players set @s sd_debuff_frozen 1
$execute if data storage stardew:temp status{type:"frozen"} run scoreboard players set @s sd_frozen_duration $(duration)
$execute if data storage stardew:temp status{type:"frozen"} run scoreboard players set @s sd_frozen_level $(level)

# 饥饿效果
execute if data storage stardew:temp status{type:"hunger"} run scoreboard players set @s sd_debuff_hunger 1
$execute if data storage stardew:temp status{type:"hunger"} run scoreboard players set @s sd_hunger_duration $(duration)
$execute if data storage stardew:temp status{type:"hunger"} run scoreboard players set @s sd_hunger_level $(level)
execute if data storage stardew:temp status{type:"hunger"} run scoreboard players set @s sd_hunger_timer 0

# 中毒效果
execute if data storage stardew:temp status{type:"poison"} run scoreboard players set @s sd_debuff_poison 1
$execute if data storage stardew:temp status{type:"poison"} run scoreboard players set @s sd_poison_duration $(duration)
$execute if data storage stardew:temp status{type:"poison"} run scoreboard players set @s sd_poison_level $(level)
execute if data storage stardew:temp status{type:"poison"} run scoreboard players set @s sd_poison_tick_timer 0

# 虚弱效果
execute if data storage stardew:temp status{type:"weakness"} run scoreboard players set @s sd_debuff_weakness 1
$execute if data storage stardew:temp status{type:"weakness"} run scoreboard players set @s sd_weakness_duration $(duration)
$execute if data storage stardew:temp status{type:"weakness"} run scoreboard players set @s sd_weakness_level $(level)

# 效果提示音效
playsound minecraft:entity.witch.hurt player @s ~ ~ ~ 0.5 0.8
