# stardew:monsters/spawn/types/summon_stray.mcfunction
# 流浪者需要保留攻击伤害属性才能用弓箭
# 【关键】不手动给武器，让流浪者自然生成弓
# active_effects: 抗火效果(防止被熔岩块伤害) - 无限时长(amplifier=0即I级)
$summon minecraft:stray ~ ~ ~ {Tags:["sd_monster_init","sd_monster","sd_mob_stray","sd_tier_3","sd_hp_$(monster_hp)","sd_atk_$(monster_atk)"],DeathLootTable:"$(loot_table)",CustomNameVisible:1b,Attributes:[{id:"minecraft:generic.attack_damage",base:5.0d}],active_effects:[{id:"minecraft:fire_resistance",amplifier:0,duration:-1,show_particles:0b}]}


