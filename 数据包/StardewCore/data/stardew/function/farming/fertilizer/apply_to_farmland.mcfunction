# data/stardew/functions/farming/fertilizer/apply_to_farmland.mcfunction
# 在耕地上施加肥料
# 执行位置: 已经在 ~0.5 ~1 ~0.5 (由check_farmland定位)
# 作物marker在 ~ ~0.375 ~ (即 ~0.5 ~1.375 ~0.5)

# 检查是否已经有作物
# 情况1: 已有作物 - 直接给作物施肥
execute positioned ~ ~0.375 ~ if entity @e[type=marker,tag=sd_crop,distance=..0.1,limit=1] as @e[type=marker,tag=sd_crop,distance=..0.1,limit=1] run function stardew:farming/fertilizer/apply_to_existing_crop

# 情况2: 没有作物 - 创建肥料marker等待作物种植
execute positioned ~ ~0.375 ~ unless entity @e[type=marker,tag=sd_crop,distance=..0.1,limit=1] run summon marker ~ ~ ~ {Tags:["sd_fertilizer_marker","sd_new_fertilizer"]}
execute positioned ~ ~0.375 ~ unless entity @e[type=marker,tag=sd_crop,distance=..0.1,limit=1] as @e[type=marker,tag=sd_new_fertilizer,distance=..0.1,limit=1] run function stardew:farming/fertilizer/set_marker_data

# 召唤视觉实体(除了树肥)
execute if score @p sd_temp_fert_type matches 1..3 run function stardew:farming/fertilizer/summon_visual

# 消耗一个肥料物品
execute as @p run function stardew:farming/fertilizer/consume_item

# 播放施肥音效
playsound minecraft:item.hoe.till player @a ~ ~ ~ 1 1.2

# 提示玩家
tellraw @p {"text":"施肥成功!","color":"green"}
