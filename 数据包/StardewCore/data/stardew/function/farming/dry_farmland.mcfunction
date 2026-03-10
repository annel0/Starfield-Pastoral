# data/stardew/functions/farming/dry_farmland.mcfunction
# 过夜耕地干涸处理 - 考虑保湿土壤
# 不需要执行者，直接处理全世界的作物和耕地

# ========== 阶段1：全部晾干 ==========
# Step 1: 暴力晒干所有耕地（包括空耕地和有作物的）
# 先positioned到耕地高度（~-0.375），然后fill
execute as @e[type=marker,tag=sd_crop] at @s positioned ~ ~-0.375 ~ run fill ~-8 ~ ~-8 ~8 ~ ~8 minecraft:farmland[moisture=0] replace minecraft:farmland

# Step 2: 重置所有作物的浇水标记为0
scoreboard players set @e[type=marker,tag=sd_crop] sd_watered 0

# ========== 阶段2：保湿土壤重新湿润 ==========
# Step 3: 保湿土壤生效（所有fill完成后再统一处理）
execute as @e[type=marker,tag=sd_crop] at @s run function stardew:farming/fertilizer/check_and_apply_retaining

# Step 4: 检查耕地湿度，湿润的作物标记为已浇水
execute as @e[type=marker,tag=sd_crop] at @s positioned ~ ~-0.375 ~ if block ~ ~ ~ minecraft:farmland[moisture=7] run scoreboard players set @s sd_watered 1
