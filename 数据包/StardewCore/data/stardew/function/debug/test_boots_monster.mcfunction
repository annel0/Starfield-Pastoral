# 召唤测试史莱姆
summon minecraft:slime ~ ~1 ~ {Size:2,Tags:["sd_mob_slime"],CustomName:'{"text":"测试史莱姆","color":"green"}'}

tellraw @s {"text":"✓ 已召唤测试史莱姆","color":"green"}
tellraw @s {"text":"让它攻击你，有50%概率触发粘液效果(5秒)","color":"gray"}
tellraw @s {"text":"观察是否因免疫而缩短持续时间","color":"gray"}
