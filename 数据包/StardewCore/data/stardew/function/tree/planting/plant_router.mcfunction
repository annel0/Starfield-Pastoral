# data/stardew/functions/tree/planting/plant_router.mcfunction
# [执行位置: 泥土方块内部]

# 分发到具体树种的种植逻辑
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2501}}}] run function stardew:tree/planting/plant_oak
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2502}}}] run function stardew:tree/planting/plant_maple
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2503}}}] run function stardew:tree/planting/plant_pine
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2504}}}] run function stardew:tree/planting/plant_mahogany