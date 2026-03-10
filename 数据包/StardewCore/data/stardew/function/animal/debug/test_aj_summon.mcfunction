# ================================================================
# 测试直接调用 Animated Java 的 sheep summon
# ================================================================

tellraw @s [{"text":"[AJ测试] 直接测试 animated_java:sheep/summon...","color":"yellow"}]

# 检查调用前的实体数量
execute store result score #before_count stardew.animal.temp if entity @e[tag=aj.new,distance=..5]
tellraw @s [{"text":"[AJ测试] 调用前 aj.new 实体数量: ","color":"gray"},{"score":{"name":"#before_count","objective":"stardew.animal.temp"},"color":"white"}]

# 直接调用 AJ 的 summon
function animated_java:sheep/summon

# 检查调用后的实体数量
execute store result score #after_count stardew.animal.temp if entity @e[tag=aj.new,distance=..5]
tellraw @s [{"text":"[AJ测试] 调用后 aj.new 实体数量: ","color":"gray"},{"score":{"name":"#after_count","objective":"stardew.animal.temp"},"color":"gold"}]

# 检查是否有 aj.sheep.root
execute store result score #sheep_root_count stardew.animal.temp if entity @e[tag=aj.sheep.root,distance=..5]
tellraw @s [{"text":"[AJ测试] aj.sheep.root 实体数量: ","color":"gray"},{"score":{"name":"#sheep_root_count","objective":"stardew.animal.temp"},"color":"aqua"}]

# 列出附近所有的 aj 实体
tellraw @s [{"text":"[AJ测试] 附近的 AJ 实体:","color":"yellow"}]
execute as @e[tag=aj.new,distance=..5] run tellraw @a[distance=..10] [{"text":"  - ","color":"gray"},{"selector":"@s","color":"white"}]

tellraw @s [{"text":"[AJ测试] 完成！","color":"green"}]
