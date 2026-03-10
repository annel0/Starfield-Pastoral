# data/stardew/function/status/tick.mcfunction
# 状态效果系统主 tick 函数

# ========== Debuff 处理 ==========
# 粘液效果
execute as @a[scores={sd_debuff_slime=1..,sd_slime_duration=1..}] run function stardew:status/debuff/slime_tick

# 冰冻效果
execute as @a[scores={sd_debuff_frozen=1..,sd_frozen_duration=1..}] run function stardew:status/debuff/frozen_tick

# 饥饿效果
execute as @a[scores={sd_debuff_hunger=1..,sd_hunger_duration=1..}] run function stardew:status/debuff/hunger_tick

# 中毒效果
execute as @a[scores={sd_debuff_poison=1..,sd_poison_duration=1..}] run function stardew:status/debuff/poison_tick

# 虚弱效果
execute as @a[scores={sd_debuff_weakness=1..,sd_weakness_duration=1..}] run function stardew:status/debuff/weakness_tick

# ========== Buff 处理 ==========
# 速度效果
execute as @a[scores={sd_buff_speed=1..,sd_speed_duration=1..}] run function stardew:status/buff/speed_tick

# 力量效果
execute as @a[scores={sd_buff_strength=1..,sd_strength_duration=1..}] run function stardew:status/buff/strength_tick

# 再生效果
execute as @a[scores={sd_buff_regen=1..,sd_regen_duration=1..}] run function stardew:status/buff/regen_tick

# 抗性效果
execute as @a[scores={sd_buff_resistance=1..,sd_resistance_duration=1..}] run function stardew:status/buff/resistance_tick

# 幸运效果
execute as @a[scores={sd_buff_luck=1..,sd_luck_duration=1..}] run function stardew:status/buff/luck_tick

# 护盾效果
execute as @a[scores={sd_buff_shield=1..,sd_shield_duration=1..}] run function stardew:status/buff/shield_tick
