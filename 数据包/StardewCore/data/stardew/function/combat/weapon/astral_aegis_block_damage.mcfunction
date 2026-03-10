# 星辰护盾抵挡伤害 - 固定伤害+回血机制

# 消耗一个护盾球
scoreboard players remove @s sd_shield_count 1

# 读取武器配置的固定伤害值（默认银河剑30，无限之刃40）
execute store result score #fixed_damage sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_shield_damage
execute unless score #fixed_damage sd_temp matches 1.. run scoreboard players set #fixed_damage sd_temp 30

# 读取回血量（默认银河剑4点，无限之刃8点）
execute store result score #heal_amount sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_shield_heal
execute unless score #heal_amount sd_temp matches 1.. run scoreboard players set #heal_amount sd_temp 4

# 对最近的攻击者造成固定伤害
execute as @e[tag=sd_monster,distance=..10,sort=nearest,limit=1] run scoreboard players operation @s sd_monster_hp -= #fixed_damage sd_temp

# 显示伤害数字（在被攻击的怪物位置）
execute as @e[tag=sd_monster,distance=..10,sort=nearest,limit=1] at @s run function stardew:combat/weapon/astral_aegis_show_damage

# 恢复玩家生命值（使用自定义sd_health系统）
scoreboard players operation @s sd_health += #heal_amount sd_temp
# 不超过最大生命值
execute if score @s sd_health > @s sd_max_health run scoreboard players operation @s sd_health = @s sd_max_health

# 找到一个护盾球并发射到攻击者位置
execute as @e[tag=sd_shield_orb,limit=1,sort=nearest] if score @s sd_shield_id = @p sd_shield_id run tag @s add sd_shield_launch

# 给护盾球添加朝向攻击者的motion（发射效果）
execute as @e[tag=sd_shield_launch] at @s facing entity @e[tag=sd_monster,distance=..10,sort=nearest,limit=1] eyes run function stardew:combat/weapon/astral_aegis_launch

# 视觉效果
particle minecraft:enchanted_hit ~ ~1 ~ 0.8 0.8 0.8 2 50 force
particle minecraft:end_rod ~ ~1 ~ 0.5 0.5 0.5 0.3 30 force
particle minecraft:flash ~ ~1 ~ 0 0 0 0 2 force
particle minecraft:explosion ~ ~1 ~ 0.3 0.3 0.3 0 3 force
particle minecraft:soul_fire_flame ~ ~1 ~ 0.4 0.4 0.4 0.1 15 force
# 回血特效（绿色爱心）
particle minecraft:heart ~ ~1.5 ~ 0.3 0.3 0.3 0.1 3 force

# 音效
playsound minecraft:item.shield.block player @a ~ ~ ~ 1.5 1.5
playsound minecraft:block.enchantment_table.use player @a ~ ~ ~ 1.2 2
playsound minecraft:entity.lightning_bolt.impact player @a ~ ~ ~ 0.5 2
playsound minecraft:entity.generic.explode player @a ~ ~ ~ 0.3 1.8
# 回血音效
playsound minecraft:entity.player.levelup player @s ~ ~ ~ 0.3 2

# 提示（已删除actionbar占用）

# 如果护盾耗尽，强制结束并进入冷却
execute if score @s sd_shield_count matches ..0 run bossbar set stardew:astral_aegis_duration visible false
execute if score @s sd_shield_count matches ..0 store result bossbar stardew:astral_aegis_cooldown value run scoreboard players get @s sd_skill_cooldown
execute if score @s sd_shield_count matches ..0 run bossbar set stardew:astral_aegis_cooldown visible true
execute if score @s sd_shield_count matches ..0 run function stardew:combat/weapon/astral_aegis_end

# 给予短暂无敌帧
scoreboard players set @s sd_invincible 10

# 撤销advancement防止实际扣血
advancement revoke @s only stardew:combat/entity_hurt_player
