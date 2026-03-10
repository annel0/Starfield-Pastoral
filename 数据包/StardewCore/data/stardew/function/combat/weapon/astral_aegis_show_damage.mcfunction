# 显示星辰护盾反击伤害
# 从 astral_aegis_block_damage 调用

execute store result storage stardew:temp damage int 1 run scoreboard players get #fixed_damage sd_temp
data modify storage stardew:temp icon set value "🛡"
data modify storage stardew:temp color set value "#9933FF"
function stardew:combat/damage_display/spawn_skill with storage stardew:temp
