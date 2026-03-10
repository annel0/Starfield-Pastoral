# 召唤所有武器的掉落物（用于测试伤害显示）
# 所有武器在玩家脚下生成

tellraw @s {"text":"=== 开始召唤所有武器 ===","color":"gold","bold":true}

# 按字母顺序生成所有武器
loot spawn ~ ~ ~ loot stardew:items/weapon/bone_sword
loot spawn ~ ~ ~ loot stardew:items/weapon/burglars_shank
loot spawn ~ ~ ~ loot stardew:items/weapon/claymore
loot spawn ~ ~ ~ loot stardew:items/weapon/crystal_dagger
loot spawn ~ ~ ~ loot stardew:items/weapon/cutlass
loot spawn ~ ~ ~ loot stardew:items/weapon/dragontooth_club
loot spawn ~ ~ ~ loot stardew:items/weapon/dragontooth_shiv
loot spawn ~ ~ ~ loot stardew:items/weapon/dwarf_hammer
loot spawn ~ ~ ~ loot stardew:items/weapon/femur
loot spawn ~ ~ ~ loot stardew:items/weapon/forest_sword
loot spawn ~ ~ ~ loot stardew:items/weapon/galaxy_dagger
loot spawn ~ ~ ~ loot stardew:items/weapon/galaxy_hammer
loot spawn ~ ~ ~ loot stardew:items/weapon/galaxy_sword
loot spawn ~ ~ ~ loot stardew:items/weapon/infinity_blade
loot spawn ~ ~ ~ loot stardew:items/weapon/infinity_dagger
loot spawn ~ ~ ~ loot stardew:items/weapon/infinity_gavel
loot spawn ~ ~ ~ loot stardew:items/weapon/iron_dirk
loot spawn ~ ~ ~ loot stardew:items/weapon/iron_edge
loot spawn ~ ~ ~ loot stardew:items/weapon/kudgel
loot spawn ~ ~ ~ loot stardew:items/weapon/lava_katana
loot spawn ~ ~ ~ loot stardew:items/weapon/neptunes_glaive
loot spawn ~ ~ ~ loot stardew:items/weapon/obsidian_edge
loot spawn ~ ~ ~ loot stardew:items/weapon/pirates_sword
loot spawn ~ ~ ~ loot stardew:items/weapon/rusty_sword
loot spawn ~ ~ ~ loot stardew:items/weapon/shadow_dagger
loot spawn ~ ~ ~ loot stardew:items/weapon/silver_saber
loot spawn ~ ~ ~ loot stardew:items/weapon/steel_falchion
loot spawn ~ ~ ~ loot stardew:items/weapon/steel_smallsword
loot spawn ~ ~ ~ loot stardew:items/weapon/tempered_broadsword
loot spawn ~ ~ ~ loot stardew:items/weapon/the_slammer
loot spawn ~ ~ ~ loot stardew:items/weapon/wicked_kris
loot spawn ~ ~ ~ loot stardew:items/weapon/wooden_blade
loot spawn ~ ~ ~ loot stardew:items/weapon/wood_club
loot spawn ~ ~ ~ loot stardew:items/weapon/wood_mallet

tellraw @s {"text":"✓ 已召唤全部34把武器！","color":"green"}
