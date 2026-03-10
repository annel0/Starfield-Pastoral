# stardew:debug/dummy/tick.mcfunction
# 每tick更新稻草人状态

# 自动回血到满血（让它看起来无限血量）
execute if score @s sd_monster_hp < @s sd_monster_max_hp run scoreboard players set @s sd_monster_hp 999999
