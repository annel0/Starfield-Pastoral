# data/stardew/functions/debug/sync_fertilizer.mcfunction
# 同步所有已有作物的肥料数据

tellraw @s {"text":"开始同步作物肥料数据...","color":"yellow"}

execute as @e[type=marker,tag=sd_crop] at @s run function stardew:debug/sync_one_crop

tellraw @s {"text":"同步完成！","color":"green"}
