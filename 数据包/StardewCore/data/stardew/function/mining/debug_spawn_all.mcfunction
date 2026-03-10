# stardew:mining/debug_spawn_all.mcfunction
# 一键生成所有矿石/宝石实体的debug胡萝卜钓竿
# 用于开发测试

# === 石头生成器 (4个主题) ===
loot give @s loot stardew:items/debug_mining/stone_theme1
loot give @s loot stardew:items/debug_mining/stone_theme2
loot give @s loot stardew:items/debug_mining/stone_theme3
loot give @s loot stardew:items/debug_mining/stone_theme4

# === 煤矿生成器 (4个主题) ===
loot give @s loot stardew:items/debug_mining/coal_theme1
loot give @s loot stardew:items/debug_mining/coal_theme2
loot give @s loot stardew:items/debug_mining/coal_theme3
loot give @s loot stardew:items/debug_mining/coal_theme4

# === 铜矿生成器 (4个主题) ===
loot give @s loot stardew:items/debug_mining/copper_theme1
loot give @s loot stardew:items/debug_mining/copper_theme2
loot give @s loot stardew:items/debug_mining/copper_theme3
loot give @s loot stardew:items/debug_mining/copper_theme4

# === 铁矿生成器 (4个主题) ===
loot give @s loot stardew:items/debug_mining/iron_theme1
loot give @s loot stardew:items/debug_mining/iron_theme2
loot give @s loot stardew:items/debug_mining/iron_theme3
loot give @s loot stardew:items/debug_mining/iron_theme4

# === 金矿生成器 (4个主题) ===
loot give @s loot stardew:items/debug_mining/gold_theme1
loot give @s loot stardew:items/debug_mining/gold_theme2
loot give @s loot stardew:items/debug_mining/gold_theme3
loot give @s loot stardew:items/debug_mining/gold_theme4

# === 钻石矿生成器 (4个主题) ===
loot give @s loot stardew:items/debug_mining/diamond_theme1
loot give @s loot stardew:items/debug_mining/diamond_theme2
loot give @s loot stardew:items/debug_mining/diamond_theme3
loot give @s loot stardew:items/debug_mining/diamond_theme4

# === 宝石矿生成器 (7种) ===
loot give @s loot stardew:items/debug_mining/gem_quartz
loot give @s loot stardew:items/debug_mining/gem_earth_crystal
loot give @s loot stardew:items/debug_mining/gem_frozen_tear
loot give @s loot stardew:items/debug_mining/gem_jade
loot give @s loot stardew:items/debug_mining/gem_ruby
loot give @s loot stardew:items/debug_mining/gem_amethyst
loot give @s loot stardew:items/debug_mining/gem_prismatic_shard

tellraw @s [{"text":"✓ 已给予全部31个挖矿debug工具!","color":"green"}]
tellraw @s {"text":"右键地面生成对应矿石实体。","color":"yellow"}