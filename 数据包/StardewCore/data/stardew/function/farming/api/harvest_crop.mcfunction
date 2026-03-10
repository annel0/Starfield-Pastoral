# data/stardew/functions/farming/api/harvest_crop.mcfunction
# 宏参数: {id: "spring/parsnip", mature: 4, regrow: 0}

# 1. 成熟检测 (如果年龄小于成熟年龄，退出)
# 注意: mature=4 表示需要 4 天成熟，即 age=3 时成熟 (0->1->2->3 共4天)
# 所以判断: age < mature 时未成熟
$execute unless score @s sd_crop_age matches $(mature).. run return 0

# 1.5 成功收割标记：给附近玩家的 sd_temp +1（用于镰刀能量消耗判断）
execute as @p[distance=..6] run scoreboard players add @s sd_temp 1

# 2. 掉落战利品 (调用 Fishing 里的通用随机掉落 API)
# 先生成随机数 (决定金银星)
function stardew:farming/rng_generate
# 调用掉落 (注意路径拼接)
$function stardew:farming/api/drop_quality {id:"crops/$(id)"}

# 3. 视觉特效 (通用)
particle minecraft:wax_on ~ ~0.5 ~ 0.2 0.2 0.2 1 10
playsound minecraft:entity.experience_orb.pickup block @a ~ ~ ~ 1 1.2

# 4. 增加经验值 (基于作物类型从经验表获取)
# 从 id 参数提取作物名 (如 "spring/parsnip" -> "parsnip")
$data modify storage stardew:farming crop set value "$(id)"
# 移除路径前缀 (处理 "spring/", "summer/", "fall/" 等)
execute store success score #has_slash sd_temp run data modify storage stardew:farming crop set string storage stardew:farming crop 7
execute if score #has_slash sd_temp matches 0 run data modify storage stardew:farming crop set string storage stardew:farming crop 0
# 调用经验表给附近玩家增加经验
execute as @p[distance=..6,limit=1] run function stardew:farming/xp/crop_xp_table

# ==================================================
# 5. 核心分支：再生 vs 死亡
# ==================================================

# --- 分支 A: 再生作物 (regrow > 0) ---
# 如果参数 regrow 大于 0，说明是再生作物
$scoreboard players set @s sd_temp_regrow $(regrow)

# 修改年龄
execute if score @s sd_temp_regrow matches 1.. run scoreboard players operation @s sd_crop_age = @s sd_temp_regrow
# [重要] 立即刷新视觉模型 (否则要等下一天长大了才会变样子)
execute if score @s sd_temp_regrow matches 1.. run function stardew:farming/visual_update_router


# --- 分支 B: 一次性作物 (regrow == 0) ---
# 如果 regrow 为 0，说明收割即死 (调用 dead_harvest 清理实体)
execute if score @s sd_temp_regrow matches 0 run function stardew:farming/dead_harvest