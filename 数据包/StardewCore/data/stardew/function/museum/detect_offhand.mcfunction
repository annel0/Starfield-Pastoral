# stardew:museum/detect_offhand  
# 检测玩家副手是否有unknown物品并尝试鉴定
# 每tick由main调用

# 检查宝石
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"quartz_unknown"}] run function stardew:museum/identify/gem/quartz
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"earth_crystal_unknown"}] run function stardew:museum/identify/gem/earth_crystal
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"frozen_tear_unknown"}] run function stardew:museum/identify/gem/frozen_tear
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"jade_unknown"}] run function stardew:museum/identify/gem/jade
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"ruby_unknown"}] run function stardew:museum/identify/gem/ruby
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"amethyst_unknown"}] run function stardew:museum/identify/gem/amethyst
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"prismatic_shard_unknown"}] run function stardew:museum/identify/gem/prismatic_shard
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"topaz_unknown"}] run function stardew:museum/identify/gem/topaz
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"aquamarine_unknown"}] run function stardew:museum/identify/gem/aquamarine
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"emerald_unknown"}] run function stardew:museum/identify/gem/emerald
execute if items entity @s weapon.offhand paper[custom_data~{gem_type:"fire_quartz_unknown"}] run function stardew:museum/identify/gem/fire_quartz

# 检查古物 - 由于数量太多,分批调用
function stardew:museum/detect_artifacts
