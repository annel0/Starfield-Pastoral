# stardew:monsters/spawn/types/summon_slime.mcfunction
# 宏函数：实际生成史莱姆

$summon minecraft:slime ~ ~ ~ {Size:$(monster_size),Tags:["sd_monster_init","sd_monster","sd_mob_slime","$(monster_tier)","sd_hp_$(monster_hp)","sd_atk_$(monster_atk)"],DeathLootTable:"$(loot_table)",CustomNameVisible:1b}

