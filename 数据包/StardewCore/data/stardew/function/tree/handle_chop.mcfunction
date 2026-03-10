# data/stardew/functions/tree/handle_chop.mcfunction
# [执行者: 交互实体 (树)]

# 1. 寻找攻击者
# 给最近的玩家打个临时标签
tag @p[distance=..6] add current_attacker

# 2. [核心修复] 检查冷却状态
# 先重置跳过标记
tag @s remove skip_logic

# 如果玩家冷却中 (sd_axe_cd >= 1)，给树打上 skip_logic 标记
execute if entity @p[tag=current_attacker,scores={sd_axe_cd=1..}] run tag @s add skip_logic

# 3. 处理冷却情况
# A. 播放反馈 (粒子和音效，不使用文字)
execute if entity @s[tag=skip_logic] at @s run particle minecraft:angry_villager ~ ~1 ~ 0.2 0.2 0.2 0 3 force @a
execute if entity @s[tag=skip_logic] at @s run playsound minecraft:block.note_block.bass player @a ~ ~ ~ 0.5 0.5

# B. 清理玩家标签 (无论是否冷却都要清理，但在 return 前清理)
# 这里我们只在冷却分支里清理一次，非冷却分支下面会清理
execute if entity @s[tag=skip_logic] run tag @p[tag=current_attacker] remove current_attacker

# C. [关键] 终止函数
execute if entity @s[tag=skip_logic] run return 0


# --- 以下是正常砍树逻辑 (只有没冷却才会执行到这里) ---

# 4. 标记自己
tag @s add current_target_tree
scoreboard players set @s sd_axe_dmg 0

# 5. 计算伤害 (并设置冷却)
# 此时 current_attacker 标签还在
execute as @p[tag=current_attacker] run function stardew:tree/calc_player_damage

# 6. 清理标签
tag @s remove current_target_tree
tag @p[tag=current_attacker] remove current_attacker

# 7. 判定无效攻击 (伤害为0)
# 只有当玩家手里拿的不是斧头时，伤害才会是 0
execute if score @s sd_axe_dmg matches 0 run playsound minecraft:block.wood.hit block @a ~ ~ ~ 0.5 0.5
execute if score @s sd_axe_dmg matches 0 run tellraw @p[distance=..6] {"text":"你需要一把斧头！","color":"red"}
execute if score @s sd_axe_dmg matches 0 run return 1

# 7.5 有效攻击：扣除2点能量
execute as @p[distance=..6] run scoreboard players set #energy_cost sd_temp 2
execute as @p[distance=..6] run function stardew:energy/consume

# 8. 扣血
scoreboard players operation @s sd_tree_hp -= @s sd_axe_dmg

# 9. 视觉反馈 (成功砍到)
playsound minecraft:item.axe.strip block @a ~ ~ ~ 1 0.8
particle minecraft:block{block_state:"minecraft:oak_log"} ~ ~1.5 ~ 0.3 0.5 0.3 1 10

# 10. 死亡判定
# 10.1 如果树上有提取器，先清理提取器实体（必须在fall_tree之前，因为fall_tree会kill @s）
execute if score @s sd_tree_hp matches ..0 if entity @s[tag=sd_has_tapper] run function stardew:utility/tapper/cleanup_tapper
# 10.2 树倒下
execute if score @s sd_tree_hp matches ..0 run function stardew:tree/fall_tree