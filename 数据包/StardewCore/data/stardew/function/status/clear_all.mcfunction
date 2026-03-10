# data/stardew/function/status/clear_all.mcfunction
# 清除所有状态效果

# 清除所有 Debuff
function stardew:status/debuff/slime_remove
function stardew:status/debuff/frozen_remove
function stardew:status/debuff/hunger_remove
function stardew:status/debuff/poison_remove
function stardew:status/debuff/weakness_remove

# 清除所有 Buff
function stardew:status/buff/speed_remove
function stardew:status/buff/strength_remove
function stardew:status/buff/regen_remove
function stardew:status/buff/resistance_remove
function stardew:status/buff/luck_remove
function stardew:status/buff/shield_remove

# 提示
particle minecraft:explosion ~ ~1 ~ 0 0 0 0 1
playsound minecraft:block.beacon.deactivate player @s ~ ~ ~ 0.5 1
