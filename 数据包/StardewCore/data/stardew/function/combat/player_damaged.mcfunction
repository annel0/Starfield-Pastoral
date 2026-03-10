# 玩家受到伤害（advancement触发）

# 检查是否有无敌帧
execute if score @s sd_invincible matches 1.. run return run advancement revoke @s only stardew:combat/entity_hurt_player

# 【闪避判定】优先级最高
execute if score @s sd_dodge_chance matches 1.. run function stardew:combat/check_dodge

# 查找最近的怪物获取其攻击力
execute as @e[tag=sd_monster,distance=..10,sort=nearest,limit=1] run scoreboard players operation #monster_damage sd_temp = @s sd_monster_damage
execute unless score #monster_damage sd_temp matches 1.. run scoreboard players set #monster_damage sd_temp 5

# 初始化伤害
scoreboard players operation #damage_taken sd_temp = #monster_damage sd_temp

# 【星辰护盾】如果有护盾，完全抵挡这次伤害
execute if entity @s[tag=sd_has_shield] if score @s sd_shield_count matches 1.. run return run function stardew:combat/weapon/astral_aegis_block_damage

# 如果正在格挡，减伤50%
execute if entity @s[tag=sd_blocking] run function stardew:combat/block_damage

# 【黑曜护甲】检查是否持有黑曜石利刃（被动减伤15%）
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"obsidian_armor"} run scoreboard players set #obsidian_reduction sd_temp 85
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"obsidian_armor"} run scoreboard players operation #damage_taken sd_temp *= #obsidian_reduction sd_temp
execute if data entity @s SelectedItem.components."minecraft:custom_data"{weapon_special_2:"obsidian_armor"} run scoreboard players operation #damage_taken sd_temp /= #100 sd_const

# 护甲减免计算：实际伤害 = 怪物伤害 - (玩家护甲 / 2)
scoreboard players operation #armor_reduction sd_temp = @s sd_armor
scoreboard players operation #armor_reduction sd_temp /= #2 sd_const
scoreboard players operation #damage_taken sd_temp -= #armor_reduction sd_temp

# 【靴子防御】从装备存储读取 defense 值并减少伤害
# Defense 每点减少 0.5 点伤害 (defense=5 减少 2.5 点)
scoreboard players set #boots_defense sd_temp 0
execute if score @s sd_equip_boots matches 1.. run function stardew:equipment/effects/apply_boots_defense
execute if score #boots_defense sd_temp matches 1.. run scoreboard players operation #damage_taken sd_temp -= #boots_defense sd_temp

# 【戒指防御】应用百分比减伤 damage = damage * 100 / (100 + defense)
# sd_defense: 5 = 约4.8%减伤, 10 = 约9.1%减伤, 20 = 约16.7%减伤
execute if score @s sd_defense matches 1.. run scoreboard players set #defense_divisor sd_temp 100
execute if score @s sd_defense matches 1.. run scoreboard players operation #defense_divisor sd_temp += @s sd_defense
execute if score @s sd_defense matches 1.. run scoreboard players set #100 sd_const 100
execute if score @s sd_defense matches 1.. run scoreboard players operation #damage_taken sd_temp *= #100 sd_const
execute if score @s sd_defense matches 1.. run scoreboard players operation #damage_taken sd_temp /= #defense_divisor sd_temp

# 最小伤害为1
execute if score #damage_taken sd_temp matches ..0 run scoreboard players set #damage_taken sd_temp 1

# 扣除生命值
scoreboard players operation @s sd_health -= #damage_taken sd_temp

# 【戒指反伤】对攻击者造成反伤 (thorns: 10 = 10%反伤)
execute if score @s sd_ring_thorns matches 1.. run scoreboard players operation #thorns_damage sd_temp = #damage_taken sd_temp
execute if score @s sd_ring_thorns matches 1.. run scoreboard players operation #thorns_damage sd_temp *= @s sd_ring_thorns
execute if score @s sd_ring_thorns matches 1.. run scoreboard players set #100 sd_const 100
execute if score @s sd_ring_thorns matches 1.. run scoreboard players operation #thorns_damage sd_temp /= #100 sd_const
execute if score @s sd_ring_thorns matches 1.. run execute as @e[tag=sd_monster,distance=..10,sort=nearest,limit=1] run scoreboard players operation @s sd_health -= #thorns_damage sd_temp
execute if score @s sd_ring_thorns matches 1.. run tellraw @s [{"text":"⚡ ","color":"light_purple"},{"text":"反伤!","color":"gold"},{"text":" -","color":"gray"},{"score":{"name":"#thorns_damage","objective":"sd_temp"},"color":"red"},{"text":"❤","color":"red"}]

# 受伤反馈
particle minecraft:damage_indicator ~ ~1 ~ 0.3 0.5 0.3 0.1 10
playsound minecraft:entity.player.hurt player @a ~ ~ ~ 1 1
# 受伤提示已删除（不占用actionbar）
title @s title {"text":""}

# 无敌帧（0.5秒）+ 【戒指保护】延长无敌时间
scoreboard players set @s sd_invincible 10
execute if score @s sd_ring_protection matches 1.. run scoreboard players operation @s sd_invincible += @s sd_ring_protection

# 检测是否昏倒
execute if score @s sd_health matches ..0 run function stardew:combat/player_faint

# ========== 怪物 Debuff 系统 ==========
# 根据最近的怪物类型应用对应的 Debuff 效果

# 【靴子免疫】计算免疫减免比例
execute if score @s sd_equip_boots matches 1.. run function stardew:equipment/effects/apply_boots_immunity
execute unless score @s sd_equip_boots matches 1.. run data merge storage stardew:temp {immunity_reduction:100}

# 史莱姆 → 粘液效果 (50%概率, 5秒, 等级1) - 只检测最近的怪物
execute if entity @e[tag=sd_mob_slime,distance=..10,sort=nearest,limit=1] if predicate stardew:random_50 run function stardew:equipment/effects/apply_debuff_with_immunity {type:"slime",duration:100,level:1,name:"粘液"}

# 蜘蛛/洞穴蜘蛛 → 中毒效果 (40%概率, 6秒, 等级1-2) - 只检测最近的怪物
execute if entity @e[tag=sd_mob_spider,distance=..10,sort=nearest,limit=1] if predicate stardew:random_50 run function stardew:equipment/effects/apply_debuff_with_immunity {type:"poison",duration:120,level:1,name:"中毒"}
execute if entity @e[type=minecraft:cave_spider,distance=..10,sort=nearest,limit=1] if predicate stardew:random_50 run function stardew:equipment/effects/apply_debuff_with_immunity {type:"poison",duration:120,level:2,name:"中毒"}

# 流浪者 → 冰冻效果 (30%概率, 4秒, 等级1) - 只检测最近的怪物
execute if entity @e[tag=sd_mob_stray,distance=..10,sort=nearest,limit=1] if predicate stardew:random_30 run function stardew:equipment/effects/apply_debuff_with_immunity {type:"frozen",duration:80,level:1,name:"冰冻"}

# 骷髅 → 饥饿效果 (20%概率, 8秒, 等级1) - 只检测最近的怪物
execute if entity @e[tag=sd_mob_skeleton,distance=..10,sort=nearest,limit=1] if predicate stardew:random_20 run function stardew:equipment/effects/apply_debuff_with_immunity {type:"hunger",duration:160,level:1,name:"饥饿"}

# 暗影生物 → 虚弱效果 (40%概率, 6秒, 等级1) - 只检测最近的怪物
execute if entity @e[tag=sd_mob_shadow,distance=..10,sort=nearest,limit=1] if predicate stardew:random_50 run function stardew:equipment/effects/apply_debuff_with_immunity {type:"weakness",duration:120,level:1,name:"虚弱"}

# 清除临时storage
data remove storage stardew:temp status
data remove storage stardew:temp immunity_reduction

# 重置advancement
advancement revoke @s only stardew:combat/entity_hurt_player
