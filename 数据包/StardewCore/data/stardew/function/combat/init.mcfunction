# 战斗系统初始化

# 玩家属性
scoreboard objectives add sd_health dummy "生命值"
scoreboard objectives add sd_max_health dummy "最大生命值"
scoreboard objectives add sd_armor dummy "护甲值"
scoreboard objectives add sd_defense dummy "防御力"

# 全局DPS统计系统
scoreboard objectives add sd_player_total_dmg dummy "玩家总伤害"
scoreboard objectives add sd_player_dps_timer dummy "DPS计时器"
scoreboard objectives add sd_player_dps dummy "玩家DPS"

# DPS滑动窗口 (5秒历史记录)
scoreboard objectives add sd_dmg_window_1 dummy "伤害窗口1秒"
scoreboard objectives add sd_dmg_window_2 dummy "伤害窗口2秒"
scoreboard objectives add sd_dmg_window_3 dummy "伤害窗口3秒"
scoreboard objectives add sd_dmg_window_4 dummy "伤害窗口4秒"
scoreboard objectives add sd_dmg_window_5 dummy "伤害窗口5秒"

# 战斗相关
scoreboard objectives add sd_attack_damage dummy "攻击伤害"
scoreboard objectives add sd_crit_chance dummy "暴击率"
scoreboard objectives add sd_awakening_crit_bonus dummy "觉醒暴击加成"
scoreboard objectives add sd_blocking dummy "格挡状态"
scoreboard objectives add sd_block_cooldown dummy "格挡冷却"
scoreboard objectives add sd_invincible dummy "无敌帧"
scoreboard objectives add sd_attack_cooldown dummy "攻击冷却"
scoreboard objectives add sd_skill_cooldown dummy "主技能冷却"
scoreboard objectives add sd_skill_2_cooldown dummy "特殊技能冷却"
scoreboard objectives add sd_dragon_combo_cooldown dummy "龙牙连击冷却"
scoreboard objectives add sd_flame_slash_cooldown dummy "火焰斩冷却"
scoreboard objectives add sd_regen_timer dummy "持续回血计时器"
scoreboard objectives add sd_regen_amount dummy "每tick回血量"

# 精准打击系统
scoreboard objectives add sd_precision_duration dummy "精准打击持续时间"

# 燃烧系统
scoreboard objectives add sd_burning_timer dummy "燃烧计时器"
scoreboard objectives add sd_burning_damage dummy "燃烧伤害"

# 中毒系统
scoreboard objectives add sd_poison_timer dummy "中毒计时器"
scoreboard objectives add sd_poison_damage dummy "中毒伤害"

# 星辰护盾系统
scoreboard objectives add sd_shield_count dummy "护盾球数量"
scoreboard objectives add sd_shield_timer dummy "护盾持续时间"
scoreboard objectives add sd_shield_reflect dummy "护盾反伤百分比"
scoreboard objectives add sd_shield_rotation dummy "护盾旋转角度"
scoreboard objectives add sd_shield_id dummy "护盾球绑定ID"

# 连击技能
scoreboard objectives add sd_rapid_strike_count dummy "连击剩余次数"
scoreboard objectives add sd_rapid_strike_timer dummy "连击攻击间隔"

# 星流连斩技能
scoreboard objectives add sd_star_flurry_combo dummy "星流连斩连击次数"
scoreboard objectives add sd_star_flurry_timer dummy "星流连斩持续时间"

# 觉醒技能
scoreboard objectives add sd_awakening_timer dummy "觉醒持续时间"
scoreboard objectives add sd_dodge_chance dummy "闪避率"

# 伤害显示系统
function stardew:combat/damage_display/init

# 节奏打击技能
scoreboard objectives add sd_rhythm_strike_timer dummy "节奏打击窗口计时器"
scoreboard objectives add sd_rhythm_strike_phase dummy "节奏打击阶段(1=冷却/2=窗口)"

# 龙牙连击技能
scoreboard objectives add sd_dragon_combo_timer dummy "龙牙连击窗口计时器"
scoreboard objectives add sd_dragon_combo_phase dummy "龙牙连击阶段(1=冷却/2=窗口)"

# 龙牙狂怒技能
scoreboard objectives add sd_fury_timer dummy "龙牙狂怒持续时间"

# 泰坦之怒技能
scoreboard objectives add sd_wrath_timer dummy "泰坦之怒持续时间"

# 蓄力重击技能
scoreboard objectives add sd_heavy_charge_time dummy "蓄力重击蓄力时间"
scoreboard objectives add sd_heavy_charge_ready dummy "蓄力重击是否蓄满"

# 暴击涌动技能
scoreboard objectives add sd_surge_timer dummy "暴击涌动持续时间"

# 怪物属性
scoreboard objectives add sd_monster_hp dummy "怪物血量"
scoreboard objectives add sd_monster_max_hp dummy "怪物最大血量"
scoreboard objectives add sd_monster_damage dummy "怪物攻击力"

# 战斗经验
scoreboard objectives add sd_combat_xp dummy "战斗经验"
scoreboard objectives add sd_combat_level dummy "战斗等级"

# 检测右键使用胡萝卜钓竿
scoreboard objectives add sd_use_carrot minecraft.used:minecraft.carrot_on_a_stick "使用胡萝卜钓竿"

# 常量
scoreboard players set #0 sd_const 0
scoreboard players set #2 sd_const 2
scoreboard players set #3 sd_const 3
scoreboard players set #5 sd_const 5
scoreboard players set #10 sd_const 10
scoreboard players set #10 sd_const 10
scoreboard players set #20 sd_const 20
scoreboard players set #60 sd_const 60
scoreboard players set #100 sd_const 100
scoreboard players set #150 sd_const 150

# 各技能独立冷却 Bossbar
# 格挡技能
bossbar add stardew:block_cooldown {"text":"🛡 格挡 - 冷却中","color":"gray","bold":true}
bossbar set stardew:block_cooldown color white
bossbar set stardew:block_cooldown style notched_6
bossbar set stardew:block_cooldown visible false

# 突刺技能
bossbar add stardew:dash_strike_cooldown {"text":"⚡ 突刺 - 冷却中","color":"gray","bold":true}
bossbar set stardew:dash_strike_cooldown color white
bossbar set stardew:dash_strike_cooldown style notched_10
bossbar set stardew:dash_strike_cooldown visible false

# 旋风斩技能
bossbar add stardew:whirlwind_cooldown {"text":"🌪 旋风斩 - 冷却中","color":"gray","bold":true}
bossbar set stardew:whirlwind_cooldown color white
bossbar set stardew:whirlwind_cooldown style notched_10
bossbar set stardew:whirlwind_cooldown visible false

# 森林赐福技能
bossbar add stardew:forest_blessing_cooldown {"text":"🌿 森林赐福 - 冷却中","color":"gray","bold":true}
bossbar set stardew:forest_blessing_cooldown color white
bossbar set stardew:forest_blessing_cooldown style notched_10
bossbar set stardew:forest_blessing_cooldown visible false

# 连击技能
bossbar add stardew:rapid_strike_cooldown {"text":"⚔ 连击 - 冷却中","color":"gray","bold":true}
bossbar set stardew:rapid_strike_cooldown color white
bossbar set stardew:rapid_strike_cooldown style notched_10
bossbar set stardew:rapid_strike_cooldown visible false

# 火焰斩技能
bossbar remove stardew:flame_slash_cooldown
bossbar add stardew:flame_slash_cooldown {"text":"🔥 火焰斩 - 冷却中","color":"gray","bold":true}
bossbar set stardew:flame_slash_cooldown color white
bossbar set stardew:flame_slash_cooldown style progress
bossbar set stardew:flame_slash_cooldown visible false

# 生命汲取技能（被动，无需冷却bossbar）

# 星辰冲击技能
bossbar add stardew:stellar_impact_cooldown {"text":"⭐ 星辰冲击 - 冷却中","color":"gray","bold":true}
bossbar set stardew:stellar_impact_cooldown color white
bossbar set stardew:stellar_impact_cooldown style notched_10
bossbar set stardew:stellar_impact_cooldown visible false

# 星辰护盾技能
bossbar add stardew:astral_aegis_cooldown {"text":"🛡 星辰护盾 - 冷却中","color":"gray","bold":true}
bossbar set stardew:astral_aegis_cooldown color white
bossbar set stardew:astral_aegis_cooldown style notched_10
bossbar set stardew:astral_aegis_cooldown visible false

# 重击蓄力技能
bossbar add stardew:heavy_charge_cooldown {"text":"� 蓄力重击 - 冷却中","color":"gray","bold":true}
bossbar set stardew:heavy_charge_cooldown color white
bossbar set stardew:heavy_charge_cooldown style notched_10
bossbar set stardew:heavy_charge_cooldown visible false

# 通用技能冷却（保留以防兼容性）
bossbar add stardew:skill_cooldown {"text":"⚔ 技能 - 冷却中","color":"gray","bold":true}
bossbar set stardew:skill_cooldown color white
bossbar set stardew:skill_cooldown style notched_10
bossbar set stardew:skill_cooldown visible false

bossbar add stardew:skill_2_cooldown {"text":"✨ 特殊技能 - 冷却中","color":"gray","bold":true}
bossbar set stardew:skill_2_cooldown color white
bossbar set stardew:skill_2_cooldown style notched_10
bossbar set stardew:skill_2_cooldown visible false

# ===== 装备系统初始化 =====
function stardew:equipment/init