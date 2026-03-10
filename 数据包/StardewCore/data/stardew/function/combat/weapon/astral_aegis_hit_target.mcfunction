# 护盾球击中目标

# 爆炸效果
particle minecraft:explosion ~ ~ ~ 0.3 0.3 0.3 0 3 force
particle minecraft:flash ~ ~ ~ 0 0 0 0 1 force
particle minecraft:end_rod ~ ~ ~ 0.3 0.3 0.3 0.2 15 force

# 音效
playsound minecraft:entity.generic.explode player @a ~ ~ ~ 1 1.5
playsound minecraft:block.enchantment_table.use player @a ~ ~ ~ 1 2

# 移除护盾球
function stardew:combat/weapon/astral_aegis_orb_vanish
