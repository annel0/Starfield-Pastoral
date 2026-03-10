# stardew:museum/check_identify
# 检查具体是什么unknown物品并尝试鉴定

# 检查是宝石还是古物
execute if items entity @s weapon.offhand paper[custom_data~{item_type:"gem"}] run function stardew:museum/identify_gems
execute if items entity @s weapon.offhand paper[custom_data~{item_type:"artifact"}] run function stardew:museum/identify_artifacts
