# 召唤测试用高血量僵尸
# 血量: 10000

# 召唤僵尸（添加 sd_hp_10000 标签，让init_monster自动设置血量）
summon minecraft:zombie ~ ~ ~ {Tags:["sd_monster","sd_monster_init","sd_hp_10000","sd_test_zombie"],CustomName:'{"text":"测试僵尸 HP:10000","color":"red","bold":true}',CustomNameVisible:1b,DeathLootTable:"minecraft:empty"}

# 提示消息
tellraw @s {"text":"✔ 已召唤测试僵尸 (血量: 10000)","color":"green","bold":true}

# 粒子效果标记
particle minecraft:happy_villager ~ ~1 ~ 0.5 0.5 0.5 0.1 30 force
particle minecraft:firework ~ ~1 ~ 0.3 0.5 0.3 0.1 20 force
playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 1 0.8
playsound minecraft:block.anvil.land player @s ~ ~ ~ 0.5 2