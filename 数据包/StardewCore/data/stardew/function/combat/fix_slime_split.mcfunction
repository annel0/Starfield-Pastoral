# combat/fix_slime_split.mcfunction
# 修复史莱姆分裂后小崽继承标签但没有初始化的问题
# 每tick由tick.mcfunction调用，清理所有继承了标签但没有初始化记分板的怪物

# 移除所有继承了sd_monster标签但没有sd_monster_hp记分板的怪物的标签
execute as @e[tag=sd_monster] unless score @s sd_monster_hp matches -2147483648..2147483647 run tag @s remove sd_monster
execute as @e[tag=sd_monster] unless score @s sd_monster_hp matches -2147483648..2147483647 run tag @s remove sd_monster_slime
