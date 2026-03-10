# stardew:debug/dummy/summon.mcfunction
# 召唤稻草人DPS测试假人

# 在玩家面前2格处生成僵尸
summon minecraft:zombie ^ ^ ^2 {Tags:["sd_dummy_init","sd_dummy","sd_monster"],CustomName:'[{"text":"🎯 稻草人","color":"yellow","bold":true}]',CustomNameVisible:1b,NoAI:1b,PersistenceRequired:1b,DeathLootTable:"stardew:empty",Silent:1b,ArmorItems:[{},{},{},{id:"minecraft:carved_pumpkin",count:1}]}

# 初始化稻草人数据
execute as @e[tag=sd_dummy_init,limit=1] run function stardew:debug/dummy/init

# 提示消息
tellraw @s [{"text":"━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"gold","bold":true}]
tellraw @s [{"text":"🎯 稻草人已生成!","color":"yellow","bold":true}]
tellraw @s [{"text":"• 不会移动，不会反击","color":"gray"}]
tellraw @s [{"text":"• 血量会自动恢复","color":"gray"}]
tellraw @s [{"text":"• 攻击后会显示实时DPS","color":"gray"}]
tellraw @s [{"text":"• 使用 ","color":"gray"},{"text":"/function stardew:debug/dummy/remove","color":"aqua","clickEvent":{"action":"suggest_command","value":"/function stardew:debug/dummy/remove"}},{"text":" 移除","color":"gray"}]
tellraw @s [{"text":"━━━━━━━━━━━━━━━━━━━━━━━━━━━","color":"gold","bold":true}]

# 播放音效
playsound minecraft:entity.zombie_villager.converted player @s ~ ~ ~ 1 1.2
