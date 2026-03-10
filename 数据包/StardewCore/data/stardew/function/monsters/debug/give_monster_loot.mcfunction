# stardew:monsters/debug/give_monster_loot.mcfunction
# 给予所有怪物战利品 (用于测试)

loot give @s loot stardew:items/monster_loot/slime
loot give @s loot stardew:items/monster_loot/bug_meat
loot give @s loot stardew:items/monster_loot/bat_wing
loot give @s loot stardew:items/monster_loot/solar_essence
loot give @s loot stardew:items/monster_loot/void_essence

tellraw @s {"text":"已获得所有怪物战利品！","color":"green"}
