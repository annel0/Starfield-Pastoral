# stardew:combat/xp/monster_xp_table.mcfunction
# 基于官方wiki的怪物经验值表
# 来源: https://stardewvalleywiki.com/Combat

# ===== 史莱姆系列 =====
# 绿史莱姆 (Green Slime) - 3 XP
execute if entity @s[tag=sd_mob_slime,tag=sd_tier_1] run scoreboard players set #monster_xp sd_temp 3

# 蓝史莱姆 (Blue/Frost Slime) - 5 XP (相当于普通史莱姆的强化版)
execute if entity @s[tag=sd_mob_slime,tag=sd_tier_2] run scoreboard players set #monster_xp sd_temp 5

# 红史莱姆 (Red Slime) - 6 XP
execute if entity @s[tag=sd_mob_slime,tag=sd_tier_3] run scoreboard players set #monster_xp sd_temp 6

# 紫史莱姆 (Purple Slime) - 10 XP
execute if entity @s[tag=sd_mob_slime,tag=sd_tier_4] run scoreboard players set #monster_xp sd_temp 10

# 虎纹史莱姆 (Tiger Slime) - 20 XP
execute if entity @s[tag=sd_mob_slime,tag=sd_tier_5] run scoreboard players set #monster_xp sd_temp 20

# ===== 蝙蝠系列 =====
# 蝙蝠 (Bat) - 5 XP
execute if entity @s[tag=sd_mob_bat,tag=!sd_tier_2] run scoreboard players set #monster_xp sd_temp 5

# 霜冻蝙蝠 (Frost Bat) - 7 XP
execute if entity @s[tag=sd_mob_bat,tag=sd_tier_2] run scoreboard players set #monster_xp sd_temp 7

# 熔岩蝙蝠 (Lava Bat) - 10 XP
execute if entity @s[tag=sd_mob_bat,tag=sd_tier_3] run scoreboard players set #monster_xp sd_temp 10

# 恶魔蝙蝠 (Iridium Bat) - 15 XP
execute if entity @s[tag=sd_mob_bat,tag=sd_tier_4] run scoreboard players set #monster_xp sd_temp 15

# ===== 虫系怪物 =====
# 洞穴苍蝇 (Cave Fly) - 3 XP
execute if entity @s[tag=sd_mob_fly] run scoreboard players set #monster_xp sd_temp 3

# 蛴螬 (Grub) - 2 XP
execute if entity @s[tag=sd_mob_grub] run scoreboard players set #monster_xp sd_temp 2

# 虫子 (Bug) - 5 XP
execute if entity @s[tag=sd_mob_bug] run scoreboard players set #monster_xp sd_temp 5

# 变异虫 (Mutant Fly) - 10 XP
execute if entity @s[tag=sd_mob_bug,tag=sd_tier_2] run scoreboard players set #monster_xp sd_temp 10

# ===== 灰尘精灵 =====
# 灰尘精灵 (Dust Sprite) - 3 XP
execute if entity @s[tag=sd_mob_dust_sprite] run scoreboard players set #monster_xp sd_temp 3

# ===== 骷髅系列 =====
# 骷髅 (Skeleton) - 15 XP
execute if entity @s[tag=sd_mob_skeleton,tag=!sd_tier_3] run scoreboard players set #monster_xp sd_temp 15

# 骷髅法师 (Skeleton Mage) - 20 XP
execute if entity @s[tag=sd_mob_skeleton,tag=sd_tier_3] run scoreboard players set #monster_xp sd_temp 20

# ===== 幽灵系列 =====
# 幽灵 (Ghost) - 15 XP
execute if entity @s[tag=sd_mob_ghost,tag=!sd_tier_2] run scoreboard players set #monster_xp sd_temp 15

# 碳幽灵 (Carbon Ghost) - 20 XP
execute if entity @s[tag=sd_mob_ghost,tag=sd_tier_2] run scoreboard players set #monster_xp sd_temp 20

# ===== 木乃伊 =====
# 木乃伊 (Mummy) - 20 XP
execute if entity @s[tag=sd_mob_mummy] run scoreboard players set #monster_xp sd_temp 20

# ===== 蛇系列 =====
# 蛇 (Serpent) - 10 XP
execute if entity @s[tag=sd_mob_serpent] run scoreboard players set #monster_xp sd_temp 10

# ===== 蟹系列 =====
# 岩石蟹 (Rock Crab) - 5 XP
execute if entity @s[tag=sd_mob_crab,tag=!sd_tier_2] run scoreboard players set #monster_xp sd_temp 5

# 熔岩蟹 (Lava Crab) - 8 XP
execute if entity @s[tag=sd_mob_crab,tag=sd_tier_2] run scoreboard players set #monster_xp sd_temp 8

# 铱星蟹 (Iridium Crab) - 12 XP
execute if entity @s[tag=sd_mob_crab,tag=sd_tier_3] run scoreboard players set #monster_xp sd_temp 12

# ===== 石魔系列 =====
# 石魔 (Stone Golem) - 10 XP
execute if entity @s[tag=sd_mob_golem,tag=!sd_tier_2] run scoreboard players set #monster_xp sd_temp 10

# 荒野石魔 (Wilderness Golem) - 15 XP
execute if entity @s[tag=sd_mob_golem,tag=sd_tier_2] run scoreboard players set #monster_xp sd_temp 15

# ===== 影子系列 =====
# 影子怪 (Shadow Brute) - 12 XP
execute if entity @s[tag=sd_mob_shadow,tag=!sd_tier_2] run scoreboard players set #monster_xp sd_temp 12

# 影子萨满 (Shadow Shaman) - 15 XP
execute if entity @s[tag=sd_mob_shadow,tag=sd_tier_2] run scoreboard players set #monster_xp sd_temp 15

# ===== 其他怪物 =====
# Duggy - 5 XP
execute if entity @s[tag=sd_mob_duggy] run scoreboard players set #monster_xp sd_temp 5

# 金属头 (Metal Head) - 15 XP
execute if entity @s[tag=sd_mob_metal_head] run scoreboard players set #monster_xp sd_temp 15

# 鱿鱼小子 (Squid Kid) - 10 XP
execute if entity @s[tag=sd_mob_squid] run scoreboard players set #monster_xp sd_temp 10

# ===== 默认经验值 =====
# 如果没有匹配到任何怪物类型，给予基础经验 3 XP
execute unless score #monster_xp sd_temp matches 1.. run scoreboard players set #monster_xp sd_temp 3
