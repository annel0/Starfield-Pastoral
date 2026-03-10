# stardew:monsters/debug/give_all_loot.mcfunction
# 给予所有怪物战利品物品 (用于测试)

# 基础战利品
loot give @s loot stardew:items/monster_loot/slime
loot give @s loot stardew:items/monster_loot/bug_meat
loot give @s loot stardew:items/monster_loot/bat_wing
loot give @s loot stardew:items/monster_loot/solar_essence
loot give @s loot stardew:items/monster_loot/void_essence

# 矮人卷轴
loot give @s loot stardew:items/monster_loot/dwarf_scroll_1
loot give @s loot stardew:items/monster_loot/dwarf_scroll_2
loot give @s loot stardew:items/monster_loot/dwarf_scroll_3
loot give @s loot stardew:items/monster_loot/dwarf_scroll_4

tellraw @s {"text":"已获得所有怪物战利品！","color":"green"}
