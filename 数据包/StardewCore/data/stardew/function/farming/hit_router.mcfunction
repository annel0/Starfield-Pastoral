# data/stardew/functions/farming/hit_router.mcfunction
# [最终的 Item Use 调度中心]

# 1. 种子 -> 直接调用对应的种植函数
# (注意：CMD 已根据 loot_table 映射表更新)

# --- 春季种子 (2100-2199) ---
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2101}}}] run function stardew:crops/planting/parsnip/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2102}}}] run function stardew:crops/planting/garlic/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2103}}}] run function stardew:crops/planting/potato/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2104}}}] run function stardew:crops/planting/tulip/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2105}}}] run function stardew:crops/planting/kale/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2106}}}] run function stardew:crops/planting/blue_jazz/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2107}}}] run function stardew:crops/planting/green_bean/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2109}}}] run function stardew:crops/planting/strawberry/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2110}}}] run function stardew:crops/planting/coffee_bean/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2111}}}] run function stardew:crops/planting/carrot/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2112}}}] run function stardew:crops/planting/rhubarb/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2199}}}] run function stardew:crops/planting/cauliflower/plant

# --- 夏季种子 (2200-2299) ---
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2100}}}] run function stardew:crops/planting/wheat/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2200}}}] run function stardew:crops/planting/tomato/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2202}}}] run function stardew:crops/planting/radish/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2203}}}] run function stardew:crops/planting/red_cabbage/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2204}}}] run function stardew:crops/planting/poppy/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2205}}}] run function stardew:crops/planting/summer_spangle/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2212}}}] run function stardew:crops/planting/summer_squash/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2207}}}] run function stardew:crops/planting/hot_pepper/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2208}}}] run function stardew:crops/planting/blueberry/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2209}}}] run function stardew:crops/planting/corn/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2210}}}] run function stardew:crops/planting/hops/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2211}}}] run function stardew:crops/planting/melon/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2299}}}] run function stardew:crops/planting/starfruit/plant

# --- 秋季种子 (2300-2399) ---
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2301}}}] run function stardew:crops/planting/bok_choy/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2302}}}] run function stardew:crops/planting/eggplant/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2303}}}] run function stardew:crops/planting/yam/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2304}}}] run function stardew:crops/planting/amaranth/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2305}}}] run function stardew:crops/planting/sunflower/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2306}}}] run function stardew:crops/planting/fairy_rose/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2307}}}] run function stardew:crops/planting/cranberry/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2308}}}] run function stardew:crops/planting/grape/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2309}}}] run function stardew:crops/planting/artichoke/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2311}}}] run function stardew:crops/planting/broccoli/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2399}}}] run function stardew:crops/planting/pumpkin/plant

# --- 冬季/特殊种子 (2400-2499) ---
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2400}}}] run function stardew:crops/planting/powder_melon/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2401}}}] run function stardew:crops/planting/winter_root/plant
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":2499}}}] run function stardew:crops/planting/ancient_fruit/plant

# 2. 水壶 -> 浇水 (新增蓄力系统)
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":301}}}] run function stardew:tools/watering_can/hit_router
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":302}}}] run function stardew:tools/watering_can/hit_router
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":303}}}] run function stardew:tools/watering_can/hit_router
execute if entity @s[nbt={SelectedItem:{components:{"minecraft:custom_model_data":304}}}] run function stardew:tools/watering_can/hit_router