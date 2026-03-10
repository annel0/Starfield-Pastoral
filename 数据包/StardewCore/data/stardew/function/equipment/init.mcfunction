# data/stardew/function/equipment/init.mcfunction
# 装备系统初始化 (在 combat/init.mcfunction 中调用)

# ===== 装备槽位状态 (0=空 1=已装备) =====
scoreboard objectives add sd_equip_boots dummy "鞋子槽位"
scoreboard objectives add sd_equip_ring1 dummy "戒指槽位1"
scoreboard objectives add sd_equip_ring2 dummy "戒指槽位2"
scoreboard objectives add sd_equip_ring3 dummy "戒指槽位3"
scoreboard objectives add sd_equip_ring4 dummy "戒指槽位4"

# ===== 装备CMD存储 (用于菜单显示) =====
scoreboard objectives add sd_equip_boots_cmd dummy "鞋子CMD"
scoreboard objectives add sd_equip_ring1_cmd dummy "戒指1CMD"
scoreboard objectives add sd_equip_ring2_cmd dummy "戒指2CMD"
scoreboard objectives add sd_equip_ring3_cmd dummy "戒指3CMD"
scoreboard objectives add sd_equip_ring4_cmd dummy "戒指4CMD"

# ===== 槽位解锁状态 (0=锁定 1=解锁) =====
scoreboard objectives add sd_unlock_ring3 dummy "戒指槽位3解锁"
scoreboard objectives add sd_unlock_ring4 dummy "戒指槽位4解锁"

# ===== 装备属性 =====
scoreboard objectives add sd_defense dummy "防御值"
scoreboard objectives add sd_immunity dummy "免疫值"
scoreboard objectives add sd_attack_bonus dummy "攻击加成%"
scoreboard objectives add sd_crit_chance dummy "暴击率%"
scoreboard objectives add sd_crit_power dummy "暴击伤害%"
scoreboard objectives add sd_weapon_speed dummy "武器速度%"
scoreboard objectives add sd_knockback dummy "击退%"
scoreboard objectives add sd_luck dummy "幸运值"

# ===== 戒指效果等级 =====
scoreboard objectives add sd_glow_level dummy "发光等级"
scoreboard objectives add sd_magnet_level dummy "磁力等级"
scoreboard objectives add sd_ring_life_steal dummy "戒指吸血"
scoreboard objectives add sd_ring_energy_steal dummy "戒指回能"
scoreboard objectives add sd_ring_thorns dummy "戒指反伤x100"
scoreboard objectives add sd_ring_protection dummy "戒指无敌延长x100"

# ===== 靴子被动效果 =====
scoreboard objectives add sd_nature_regen_timer dummy "精灵祝福计时器"
scoreboard objectives add sd_fishing_bonus dummy "钓鱼加成"

# ===== 光源系统追踪 =====
scoreboard objectives add stardew.temp dummy "临时计算"
scoreboard objectives add stardew.light.last_x dummy "上次X坐标"
scoreboard objectives add stardew.light.last_y dummy "上次Y坐标"
scoreboard objectives add stardew.light.last_z dummy "上次Z坐标"

# ===== 装备交互检测 =====
scoreboard objectives add sd_equip_use minecraft.used:minecraft.carrot_on_a_stick "使用胡萝卜吊杆"

# ===== 初始化 storage =====
# 注意：实际玩家数据在玩家首次装备时按需创建
data merge storage stardew:equipment {players:{}}

tellraw @a [{"text":"[装备系统] ","color":"green"},{"text":"初始化完成","color":"yellow"}]
