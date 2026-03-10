# 生成幻翼（蝙蝠）
# monster_hp: 怪物血量
# monster_atk: 怪物攻击力
# 优化：提升飞行速度，限制体型避免往天花板飞
# active_effects: 抗火效果(防止被熔岩块伤害) - 无限时长(amplifier=0即I级)

$summon minecraft:phantom ~ ~ ~ {Tags:["sd_monster_init","sd_monster","sd_mob_bat","sd_hp_$(monster_hp)","sd_atk_$(monster_atk)"],DeathLootTable:"$(loot_table)",CustomNameVisible:1b,Size:0,Attributes:[{id:"minecraft:generic.attack_damage",base:6.0d},{id:"minecraft:generic.movement_speed",base:0.5d},{id:"minecraft:generic.flying_speed",base:0.8d}],active_effects:[{id:"minecraft:fire_resistance",amplifier:0,duration:-1,show_particles:0b}]}
