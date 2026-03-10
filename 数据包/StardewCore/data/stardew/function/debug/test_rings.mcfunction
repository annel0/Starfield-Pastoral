# 测试所有戒指 - 使用summon方式生成物品
# 使用方法: /function stardew:debug/test_rings

# 战斗戒指 (10个)
loot spawn ~ ~ ~ loot stardew:items/rings/warrior_ring
loot spawn ~ ~ ~ loot stardew:items/rings/vampire_ring
loot spawn ~ ~ ~ loot stardew:items/rings/savage_ring
loot spawn ~ ~ ~ loot stardew:items/rings/ring_of_yoba
loot spawn ~ ~ ~ loot stardew:items/rings/sturdy_ring
loot spawn ~ ~ ~ loot stardew:items/rings/burglars_ring
loot spawn ~ ~ ~ loot stardew:items/rings/slime_charmer_ring
loot spawn ~ ~ ~ loot stardew:items/rings/napalm_ring
loot spawn ~ ~ ~ loot stardew:items/rings/crabshell_ring
loot spawn ~ ~ ~ loot stardew:items/rings/immunity_band

# 采集戒指 (7个)
loot spawn ~ ~ ~ loot stardew:items/rings/iridium_band
loot spawn ~ ~ ~ loot stardew:items/rings/glowstone_ring
loot spawn ~ ~ ~ loot stardew:items/rings/magnet_ring
loot spawn ~ ~ ~ loot stardew:items/rings/small_magnet_ring
loot spawn ~ ~ ~ loot stardew:items/rings/small_glow_ring
loot spawn ~ ~ ~ loot stardew:items/rings/lucky_ring
loot spawn ~ ~ ~ loot stardew:items/rings/hot_java_ring

# 宝石戒指 (6个)
loot spawn ~ ~ ~ loot stardew:items/rings/amethyst_ring
loot spawn ~ ~ ~ loot stardew:items/rings/topaz_ring
loot spawn ~ ~ ~ loot stardew:items/rings/aquamarine_ring
loot spawn ~ ~ ~ loot stardew:items/rings/jade_ring
loot spawn ~ ~ ~ loot stardew:items/rings/emerald_ring
loot spawn ~ ~ ~ loot stardew:items/rings/ruby_ring

# 特殊戒指 (4个)
loot spawn ~ ~ ~ loot stardew:items/rings/phoenix_ring
loot spawn ~ ~ ~ loot stardew:items/rings/thorns_ring
loot spawn ~ ~ ~ loot stardew:items/rings/wedding_ring
loot spawn ~ ~ ~ loot stardew:items/rings/bomb_ring

tellraw @s {"text":"✅ 已生成全部27种戒指！","color":"green"}
