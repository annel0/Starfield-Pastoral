
# stardew:mining/on_mine_impl.mcfunction
# 挖掘逻辑实现
# 执行者: 玩家 (@s)
# 位置: 玩家位置
# 上下文: 已确认攻击了 interaction 实体 (tag=sd_stone)

# 1. 获取目标实体 (interaction)
# 使用 tag=sd_mining_target 标记当前正在处理的实体，处理完后移除标记
tag @e[type=interaction,tag=sd_stone,distance=..3,limit=1,sort=nearest] add sd_current_target

# 2. 验证工具：必须是镐子 (CMD 201-204)
# 检测手上是否持有镐子 (通过CMD检测)
execute store result score @s sd_temp run data get entity @s SelectedItem.components."minecraft:custom_model_data"
execute unless score @s sd_temp matches 201..204 run tellraw @s {"text":"需要使用镐子来挖掘！","color":"red"}
execute unless score @s sd_temp matches 201..204 run tag @e[tag=sd_current_target] remove sd_current_target
execute unless score @s sd_temp matches 201..204 run return 0

# 2.5 检测冷却时间
execute if score @s sd_pickaxe_cd matches 1.. run tellraw @s {"text":"⏰ 镐子冷却中...","color":"yellow"}
execute if score @s sd_pickaxe_cd matches 1.. run tag @e[tag=sd_current_target] remove sd_current_target
execute if score @s sd_pickaxe_cd matches 1.. run return 0

# 2.6 检测能量是否足够
execute if score @s sd_energy matches ..0 run tellraw @s {"text":"你太累了，无法挖掘！需要恢复能量。","color":"red"}
execute if score @s sd_energy matches ..0 run playsound minecraft:entity.player.breath master @s ~ ~ ~ 0.5 0.8
execute if score @s sd_energy matches ..0 run tag @e[tag=sd_current_target] remove sd_current_target
execute if score @s sd_energy matches ..0 run return 0

# 3. 获取镐子属性 (从物品的custom_data读取)
# 默认值
scoreboard players set @s sd_temp_pickaxe_tier 0
scoreboard players set @s sd_temp_pickaxe_dmg 0

# 从custom_data读取镐子伤害
execute store result score @s sd_temp_pickaxe_dmg run data get entity @s SelectedItem.components."minecraft:custom_data".pickaxe_damage
execute store result score @s sd_temp_pickaxe_tier run data get entity @s SelectedItem.components."minecraft:custom_data".tier

# 4. 玩家挖矿等级判定 (防止低等级用高等级镐子)
# 铜镐tier1需0级, 铁镐tier2需3级, 金镐tier3需6级, 钻石镐tier4需9级
execute if score @s sd_temp_pickaxe_tier matches 2 if score @s sd_mining_lvl matches ..2 run tellraw @s {"text":"你的挖矿等级不足以使用这把镐子！(需要等级 3)","color":"red"}
execute if score @s sd_temp_pickaxe_tier matches 2 if score @s sd_mining_lvl matches ..2 run effect give @s minecraft:mining_fatigue 1 4 true
execute if score @s sd_temp_pickaxe_tier matches 2 if score @s sd_mining_lvl matches ..2 run tag @e[tag=sd_current_target] remove sd_current_target
execute if score @s sd_temp_pickaxe_tier matches 2 if score @s sd_mining_lvl matches ..2 run return 0

execute if score @s sd_temp_pickaxe_tier matches 3 if score @s sd_mining_lvl matches ..5 run tellraw @s {"text":"你的挖矿等级不足以使用这把镐子！(需要等级 6)","color":"red"}
execute if score @s sd_temp_pickaxe_tier matches 3 if score @s sd_mining_lvl matches ..5 run effect give @s minecraft:mining_fatigue 1 4 true
execute if score @s sd_temp_pickaxe_tier matches 3 if score @s sd_mining_lvl matches ..5 run tag @e[tag=sd_current_target] remove sd_current_target
execute if score @s sd_temp_pickaxe_tier matches 3 if score @s sd_mining_lvl matches ..5 run return 0

execute if score @s sd_temp_pickaxe_tier matches 4 if score @s sd_mining_lvl matches ..8 run tellraw @s {"text":"你的挖矿等级不足以使用这把镐子！(需要等级 9)","color":"red"}
execute if score @s sd_temp_pickaxe_tier matches 4 if score @s sd_mining_lvl matches ..8 run effect give @s minecraft:mining_fatigue 1 4 true
execute if score @s sd_temp_pickaxe_tier matches 4 if score @s sd_mining_lvl matches ..8 run tag @e[tag=sd_current_target] remove sd_current_target
execute if score @s sd_temp_pickaxe_tier matches 4 if score @s sd_mining_lvl matches ..8 run return 0

# 5. 矿石硬度判定 (镐子等级 vs 矿石需求)
# 从实体NBT读取 required_pickaxe_tier 到分数 (需要宏或execute store)
execute store result score #req_tier sd_temp_val run data get entity @e[tag=sd_current_target,limit=1] stardew.required_pickaxe_tier

execute if score @s sd_temp_pickaxe_tier < #req_tier sd_temp_val run tellraw @s {"text":"这块石头太硬了，需要更好的镐子！","color":"red"}
execute if score @s sd_temp_pickaxe_tier < #req_tier sd_temp_val run playsound minecraft:block.stone.hit master @s ~ ~ ~ 1 0.5
execute if score @s sd_temp_pickaxe_tier < #req_tier sd_temp_val run effect give @s minecraft:mining_fatigue 1 4 true
execute if score @s sd_temp_pickaxe_tier < #req_tier sd_temp_val run tag @e[tag=sd_current_target] remove sd_current_target
execute if score @s sd_temp_pickaxe_tier < #req_tier sd_temp_val run return 0

# 6. 扣除血量
# 播放成功挖掘音效和粒子
playsound minecraft:block.stone.hit master @s ~ ~ ~ 1 1.2
execute at @e[tag=sd_current_target,limit=1] run particle minecraft:block{block_state:"minecraft:stone"} ~ ~0.5 ~ 0.3 0.3 0.3 0.05 5

# 扣血 (从实体的scoreboard扣除)
execute as @e[tag=sd_current_target,limit=1] run scoreboard players operation @s sd_stone_hp -= @p sd_temp_pickaxe_dmg

# 6.25 [新增] 消耗能量 - 成功造成挖掘伤害扣除1点能量
scoreboard players remove @s sd_energy 1
execute if score @s sd_energy matches ..0 run scoreboard players set @s sd_energy 0
execute if score @s sd_energy matches 0 unless entity @s[tag=sd_energy_depleted] run function stardew:energy/warn_depleted

# 6.5 设置镐子冷却时间
# 铜镐(201): 冷却20 | 铁镐(202): 冷却15 | 金镐(203): 冷却10 | 钻镐(204): 冷却5
execute if score @s sd_temp matches 201 run scoreboard players set @s sd_pickaxe_cd 20
execute if score @s sd_temp matches 201 run bossbar set stardew:pickaxe_cooldown max 20
execute if score @s sd_temp matches 202 run scoreboard players set @s sd_pickaxe_cd 15
execute if score @s sd_temp matches 202 run bossbar set stardew:pickaxe_cooldown max 15
execute if score @s sd_temp matches 203 run scoreboard players set @s sd_pickaxe_cd 10
execute if score @s sd_temp matches 203 run bossbar set stardew:pickaxe_cooldown max 10
execute if score @s sd_temp matches 204 run scoreboard players set @s sd_pickaxe_cd 5
execute if score @s sd_temp matches 204 run bossbar set stardew:pickaxe_cooldown max 5

# 7. 检测破碎 (梯子生成逻辑已移至 break_stone 中针对矿洞石头)
execute as @e[tag=sd_current_target,limit=1] if score @s sd_stone_hp matches ..0 at @s run function stardew:mining/break_stone

# 清理标记
tag @e[tag=sd_current_target] remove sd_current_target