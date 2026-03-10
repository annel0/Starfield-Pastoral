# data/stardew/functions/tree/fall_tree.mcfunction
# [执行者: 交互实体]

# 1. 特效 (根据年龄)
# 小树苗
execute if score @s sd_crop_age matches ..9 run playsound minecraft:block.grass.break block @a ~ ~ ~ 1 0.8
execute if score @s sd_crop_age matches ..9 run particle minecraft:poof ~ ~0.5 ~ 0.2 0.2 0.2 0.05 5

# 大树 (震动感)
execute if score @s sd_crop_age matches 10.. run playsound minecraft:entity.zombie.break_wooden_door block @a ~ ~ ~ 1 0.8
execute if score @s sd_crop_age matches 10.. run particle minecraft:explosion ~ ~1 ~ 0.5 1.0 0.5 0 1

# 2. 掉落逻辑
# 只有成熟树才掉木材
execute if score @s sd_crop_age matches 28.. if score @s sd_tree_type matches 1..3 run function stardew:tree/drop_wood_normal
execute if score @s sd_crop_age matches 28.. if score @s sd_tree_type matches 4 run function stardew:tree/drop_wood_hard

# 只有成熟树被砍倒时，顺带摇一次掉种子
execute if score @s sd_crop_age matches 28.. run function stardew:tree/handle_shake


# 3. 清理视觉实体 (Item Display)
# 精准清理 (针对正常树)
execute if score @s sd_tree_type matches 1 positioned ~ ~1.25 ~ run kill @e[type=item_display,tag=sd_tree_vis,distance=..0.5,limit=1]
execute if score @s sd_tree_type matches 2 positioned ~ ~1.125 ~ run kill @e[type=item_display,tag=sd_tree_vis,distance=..0.5,limit=1]
execute if score @s sd_tree_type matches 3 positioned ~ ~2.25 ~ run kill @e[type=item_display,tag=sd_tree_vis,distance=..0.5,limit=1]
execute if score @s sd_tree_type matches 4 positioned ~ ~2.0625 ~ run kill @e[type=item_display,tag=sd_tree_vis,distance=..0.5,limit=1]

# [温和版保底清理] 只清理距离最近的一个可视实体，避免误杀整片树林
execute if entity @e[type=item_display,tag=sd_tree_vis,distance=..2.0,limit=1,sort=nearest] run kill @e[type=item_display,tag=sd_tree_vis,distance=..2.0,limit=1,sort=nearest]

# 4. 清理物理碰撞
# 正常清理
execute if score @s sd_tree_type matches 1 run fill ~ ~ ~ ~ ~5 ~ minecraft:air replace minecraft:barrier
execute if score @s sd_tree_type matches 2 run fill ~ ~ ~ ~ ~3 ~ minecraft:air replace minecraft:barrier
execute if score @s sd_tree_type matches 3 run fill ~ ~ ~ ~ ~5 ~ minecraft:air replace minecraft:barrier
execute if score @s sd_tree_type matches 4 run fill ~-1 ~ ~-1 ~1 ~4 ~1 minecraft:air replace minecraft:barrier

# [核心修复] 保底清理 (清除脚下 5 格高的 barrier)
fill ~ ~ ~ ~ ~5 ~ minecraft:air replace minecraft:barrier

# 5. 清理头顶文字 (进度 Text Display)
# 5. 清理头顶文字 (只清除离这棵树最近的那一个)
execute at @s run kill @e[type=text_display,tag=sd_info_text,sort=nearest,limit=1,distance=..1.2]


kill @s
