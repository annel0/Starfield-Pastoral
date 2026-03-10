# 生成僵尸（石魔替代）
# monster_hp: 怪物血量
# monster_atk: 怪物攻击力
# 注意：必须设置attack_damage>=5.0才会主动攻击
# active_effects: 抗火效果(防止被熔岩块伤害) - 无限时长(amplifier=0即I级)

$summon minecraft:zombie ~ ~ ~ {Tags:["sd_monster_init","sd_monster","sd_mob_golem","sd_hp_$(monster_hp)","sd_atk_$(monster_atk)"],DeathLootTable:"stardew:monsters/golem",CustomNameVisible:1b,IsBaby:0b,CanBreakDoors:0b,Attributes:[{id:"generic.attack_damage",base:7.0d}],active_effects:[{id:"minecraft:fire_resistance",amplifier:0,duration:-1,show_particles:0b}]}
