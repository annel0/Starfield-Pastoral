# 生成溺尸（幽灵）
# monster_hp: 怪物血量
# monster_atk: 怪物攻击力
# 优化：提升移动速度，增加攻击伤害属性

$summon minecraft:drowned ~ ~ ~ {Tags:["sd_monster_init","sd_monster","sd_mob_ghost","sd_hp_$(monster_hp)","sd_atk_$(monster_atk)"],DeathLootTable:"stardew:monsters/ghost",CustomNameVisible:1b,Attributes:[{id:"minecraft:generic.attack_damage",base:6.0d},{id:"minecraft:generic.movement_speed",base:0.35d}]}
