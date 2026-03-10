# 护盾结束

# 移除标签
tag @s remove sd_has_shield

# 移除所有属于这个玩家的护盾球
execute as @e[tag=sd_shield_orb] if score @s sd_shield_id = @p sd_shield_id run function stardew:combat/weapon/astral_aegis_orb_vanish

# 重置护盾计数
scoreboard players set @s sd_shield_count 0
scoreboard players set @s sd_shield_timer 0

# 音效
playsound minecraft:block.enchantment_table.use player @s ~ ~ ~ 0.8 0.8

# 粒子效果
particle minecraft:enchant ~ ~1 ~ 0.5 0.5 0.5 1 20 force

# 提示（已删除actionbar占用）
