# 生成恼鬼（乌贼小子替代）
# monster_hp: 怪物血量
# monster_atk: 怪物攻击力
# loot_table: 战利品表路径
# 优化：提升飞行速度和攻击伤害
# 注意：LifeTicks设置大数值保持存在
# 【关键】不手动给武器，让恼鬼自然持剑（恼鬼原版就带剑）
# active_effects: 抗火效果(防止被熔岩块伤害) - 无限时长(amplifier=0即I级)

$summon minecraft:vex ~ ~ ~ {Tags:["sd_monster_init","sd_monster","sd_mob_squid","sd_hp_$(monster_hp)","sd_atk_$(monster_atk)"],DeathLootTable:"$(loot_table)",CustomNameVisible:1b,LifeTicks:999999,Attributes:[{id:"minecraft:generic.attack_damage",base:7.0d},{id:"minecraft:generic.movement_speed",base:0.5d},{id:"minecraft:generic.flying_speed",base:0.7d}],active_effects:[{id:"minecraft:fire_resistance",amplifier:0,duration:-1,show_particles:0b}]}
