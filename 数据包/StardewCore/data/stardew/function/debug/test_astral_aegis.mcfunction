# 快速测试银河之剑和无限之刃
tellraw @s [{"text":"[测试] ","color":"gold"},{"text":"给予银河之剑和无限之刃...","color":"white"}]

loot give @s loot stardew:items/weapon/galaxy_sword
loot give @s loot stardew:items/weapon/infinity_blade

tellraw @s [{"text":"[测试] ","color":"gold"},{"text":"已给予！右键使用星辰护盾技能","color":"green"}]
