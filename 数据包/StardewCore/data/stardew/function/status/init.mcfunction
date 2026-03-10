# data/stardew/function/status/init.mcfunction
# 状态效果系统初始化

# ========== Debuff 负面效果 ==========
# 粘液效果 (史莱姆)
scoreboard objectives add sd_debuff_slime dummy "粘液效果"
scoreboard objectives add sd_slime_duration dummy "粘液持续时间"
scoreboard objectives add sd_slime_level dummy "粘液等级"

# 冰冻效果 (冰霜怪物)
scoreboard objectives add sd_debuff_frozen dummy "冰冻效果"
scoreboard objectives add sd_frozen_duration dummy "冰冻持续时间"
scoreboard objectives add sd_frozen_level dummy "冰冻等级"

# 饥饿效果 (流浪者) - 扣除能量值
scoreboard objectives add sd_debuff_hunger dummy "饥饿效果"
scoreboard objectives add sd_hunger_duration dummy "饥饿持续时间"
scoreboard objectives add sd_hunger_level dummy "饥饿等级"
scoreboard objectives add sd_hunger_timer dummy "饥饿效果计时器"

# 中毒效果 (蜘蛛/洞穴虫)
scoreboard objectives add sd_debuff_poison dummy "中毒效果"
scoreboard objectives add sd_poison_duration dummy "中毒持续时间"
scoreboard objectives add sd_poison_level dummy "中毒等级"
scoreboard objectives add sd_poison_tick_timer dummy "中毒tick计时器"

# 虚弱效果 (暗影生物)
scoreboard objectives add sd_debuff_weakness dummy "虚弱效果"
scoreboard objectives add sd_weakness_duration dummy "虚弱持续时间"
scoreboard objectives add sd_weakness_level dummy "虚弱等级"

# 燃烧效果 (烈焰人) - 使用现有的 sd_burning_timer
# scoreboard objectives add sd_debuff_burning dummy "燃烧效果"
# scoreboard objectives add sd_burning_duration dummy "燃烧持续时间"

# ========== Buff 正面效果 ==========
# 速度提升
scoreboard objectives add sd_buff_speed dummy "速度效果"
scoreboard objectives add sd_speed_duration dummy "速度持续时间"
scoreboard objectives add sd_speed_level dummy "速度等级"

# 力量提升
scoreboard objectives add sd_buff_strength dummy "力量效果"
scoreboard objectives add sd_strength_duration dummy "力量持续时间"
scoreboard objectives add sd_strength_level dummy "力量等级"

# 再生效果 (使用现有的 sd_regen_timer)
scoreboard objectives add sd_buff_regen dummy "再生效果"
scoreboard objectives add sd_regen_duration dummy "再生持续时间"
scoreboard objectives add sd_regen_level dummy "再生等级"

# 抗性提升 (伤害减免)
scoreboard objectives add sd_buff_resistance dummy "抗性效果"
scoreboard objectives add sd_resistance_duration dummy "抗性持续时间"
scoreboard objectives add sd_resistance_level dummy "抗性等级"

# 幸运效果
scoreboard objectives add sd_buff_luck dummy "幸运效果"
scoreboard objectives add sd_luck_duration dummy "幸运持续时间"
scoreboard objectives add sd_luck_level dummy "幸运等级"

# 护盾效果 (吸收伤害)
scoreboard objectives add sd_buff_shield dummy "护盾效果"
scoreboard objectives add sd_shield_duration dummy "护盾持续时间"
scoreboard objectives add sd_shield_amount dummy "护盾吸收量"

# ========== 状态管理 ==========
scoreboard objectives add sd_status_immunity dummy "状态免疫力"
scoreboard objectives add sd_status_resistance dummy "状态抗性"

tellraw @a {"text":"[状态系统] 初始化完成","color":"green"}
