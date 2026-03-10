# data/stardew/function/equipment/effects/apply_boots_defense.mcfunction
# 读取靴子的 defense 值并应用防御

# 从 storage 读取 defense 值
execute store result score #boots_defense sd_temp run data get storage stardew:equipment boots.defense 1

# 检查是否有额外防御加成 (例如: 龙鳞靴 defense_bonus: 1)
execute store result score #defense_bonus sd_temp run data get storage stardew:equipment boots.effects.defense_bonus 1
execute if score #defense_bonus sd_temp matches 1.. run scoreboard players operation #boots_defense sd_temp += #defense_bonus sd_temp

# Defense 每点减免 1 点伤害 (例如: defense=5, bonus=1 → 减少 6 点伤害)
# 不需要除以2了
