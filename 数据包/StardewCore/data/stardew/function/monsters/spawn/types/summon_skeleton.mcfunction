# stardew:monsters/spawn/types/summon_skeleton.mcfunction
# 骷髅需要保留攻击伤害属性才能用弓箭
# 设置为5.0保证能正常射击（实际伤害由自定义系统控制）
$summon minecraft:skeleton ~ ~ ~ {Tags:["sd_monster_init","sd_mob_skeleton","sd_tier_2","sd_hp_$(monster_hp)","sd_atk_$(monster_atk)"],DeathLootTable:"$(loot_table)",CustomNameVisible:1b,Attributes:[{id:"minecraft:generic.attack_damage",base:5.0d}]}


