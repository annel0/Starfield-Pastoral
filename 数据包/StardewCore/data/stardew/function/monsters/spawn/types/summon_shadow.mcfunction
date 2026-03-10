# 生成凋灵骷髅（暗影兽）
# monster_hp: 怪物血量
# monster_atk: 怪物攻击力
# loot_table: 战利品表路径
# 注意：必须设置attack_damage>=5.0才会主动攻击
# 【关键】不手动给武器，让凋灵骷髅自然生成石剑
# active_effects: 抗火效果(防止被熔岩块伤害) - 无限时长(amplifier=0即I级)

$summon minecraft:wither_skeleton ~ ~ ~ {Tags:["sd_monster_init","sd_monster","sd_mob_shadow","sd_hp_$(monster_hp)","sd_atk_$(monster_atk)"],DeathLootTable:"$(loot_table)",CustomNameVisible:1b,Attributes:[{id:"minecraft:generic.attack_damage",base:8.0d}],active_effects:[{id:"minecraft:fire_resistance",amplifier:0,duration:-1,show_particles:0b}]}
