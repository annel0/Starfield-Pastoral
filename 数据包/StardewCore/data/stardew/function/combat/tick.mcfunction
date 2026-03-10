# 战斗系统主循环

# [DEBUG] Tick测试计数器
scoreboard players add #tick_test sd_temp 1

# 玩家永久获得抗性提升 V（完全免疫原版伤害）
effect give @a minecraft:resistance infinite 4 true

# 初始化玩家血量（第一次进入）
execute as @a unless score @s sd_health matches 1.. run scoreboard players set @s sd_health 270
execute as @a unless score @s sd_max_health matches 1.. run scoreboard players set @s sd_max_health 270
execute as @a unless score @s sd_armor matches 0.. run scoreboard players set @s sd_armor 0

# 检测右键使用武器（格挡/特殊技能）
execute as @a[scores={sd_use_carrot=1..}] at @s run function stardew:combat/weapon_right_click

# 减少格挡/无敌帧/格挡冷却计时
execute as @a[scores={sd_blocking=1..}] run scoreboard players remove @s sd_blocking 1
execute as @a[tag=sd_blocking] run function stardew:combat/update_block_duration
execute as @a[scores={sd_blocking=..0}] run tag @s remove sd_blocking
execute as @a[scores={sd_invincible=1..}] run scoreboard players remove @s sd_invincible 1
execute as @a[scores={sd_block_cooldown=1..}] run function stardew:combat/cooldown/update_block

# 【新增】更新攻击冷却计时器
function stardew:combat/update_attack_cooldown

# 【新增】更新技能冷却计时器（根据武器技能类型路由到对应bossbar）
execute as @a[scores={sd_skill_cooldown=1..}] run function stardew:combat/cooldown/route_skill
execute as @a[scores={sd_skill_2_cooldown=1..}] run function stardew:combat/cooldown/route_skill_2

# 【新增】更新独立冷却系统
execute as @a[scores={sd_dragon_combo_cooldown=1..}] run function stardew:combat/cooldown/update_dragon_combo
execute as @a[scores={sd_flame_slash_cooldown=1..}] run function stardew:combat/cooldown/update_flame_slash

# 【新增】精准打击持续时间
execute as @a[tag=sd_precision_active] run function stardew:combat/update_precision_strike

# 【新增】持续回血系统
execute as @a[scores={sd_regen_timer=1..}] run function stardew:combat/process_regen

# 【新增】燃烧系统
execute as @e[type=!player,type=!item,type=!item_display,type=!text_display,scores={sd_burning_timer=1..}] at @s run function stardew:combat/process_burning

# 【新增】中毒系统
execute as @e[type=!player,type=!item,type=!item_display,type=!text_display,tag=sd_poisoned,scores={sd_poison_timer=1..}] at @s run function stardew:combat/weapon/poison_tick

# 【新增】灼烧DOT系统
execute as @e[type=!player,type=!item,type=!item_display,type=!text_display,tag=sd_burning,scores={sd_burning_timer=1..}] at @s run function stardew:combat/weapon/burning_tick

# 【新增】银河觉醒持续效果
execute as @a[tag=sd_galaxy_awakened] run function stardew:combat/weapon/galaxy_awakening_tick

# 【新增】龙牙狂怒持续效果
execute as @a[tag=sd_dragon_fury] run function stardew:combat/weapon/dragon_fury_tick

# 【新增】泰坦之怒持续效果
execute as @a[tag=sd_titan_wrath] run function stardew:combat/weapon/titan_wrath_tick

# 【新增】无限觉醒持续效果
execute as @a[tag=sd_infinity_awakened] run function stardew:combat/weapon/infinity_awakening_tick

# 【新增】伤害数字显示动画
execute as @e[type=text_display,tag=sd_damage_display] at @s run function stardew:combat/damage_display/tick

# 【新增】暴击涌动持续效果
execute as @a[tag=sd_crit_surge_active] run function stardew:combat/weapon/critical_surge_tick

# 【新增】星辰护盾更新系统
execute as @a[tag=sd_has_shield] run function stardew:combat/weapon/astral_aegis_update

# 【新增】飞行中的护盾球追踪
execute as @e[tag=sd_shield_flying] at @s run function stardew:combat/weapon/astral_aegis_flying

# 【新增】节奏打击状态机更新
execute as @a at @s run function stardew:combat/weapon/rhythm_strike_tick

# 【新增】龙牙连击状态机更新
execute as @a at @s run function stardew:combat/weapon/dragon_combo_tick

# 【新增】武器蓄力系统更新
execute as @a at @s run function stardew:combat/weapon_charge_system

# 怪物初始化（必须在其他逻辑之前）
execute as @e[tag=sd_monster_init] run function stardew:combat/init_monster

# 【DPS测试】更新稻草人假人
execute as @e[tag=sd_dummy_active] at @s run function stardew:debug/dummy/tick

# 怪物血量显示（在怪物头上显示血量）
execute as @e[tag=sd_monster] at @s run function stardew:combat/display_monster_hp

# 【关键】持续刷新怪物的抗性和生命恢复（防止被正常途径杀死）
# 修改：检查effect是否即将过期，只在快过期时刷新（减少AI干扰）
execute as @e[tag=sd_monster] run effect give @s minecraft:resistance 30 255 true
execute as @e[tag=sd_monster] run effect give @s minecraft:regeneration 30 255 true

# 检测怪物死亡
execute as @e[tag=sd_monster] if score @s sd_monster_hp matches ..0 run function stardew:combat/monster_death
