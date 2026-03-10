# stardew:mining/spawn_stone_impl.mcfunction
# 宏函数：生成指定类型的石头实体
# 执行位置：已对齐到方块中心底部 (X.5, Y, Z.5)
# 参数：
# $(stone_type) - 石头类型ID
# $(stone_hp) - 初始血量
# $(required_pickaxe_tier) - 所需镐子等级
# $(ore_drop_type) - 掉落类型
# $(model_cmd) - 模型CMD
# $(name) - 调试用名称

# 1. 生成屏障方块 (物理碰撞)
setblock ~ ~ ~ minecraft:barrier

# 2. 生成 interaction 实体 (交互检测)
# 位置：当前位置就是方块中心底部
# width=1.01, height=1.01 略大于屏障，确保玩家能交互到
# response: true 启用交互响应
# 不使用引号包裹字符串,直接使用$(stone_type)
$summon minecraft:interaction ~ ~ ~ {Tags:["sd_stone","sd_mining_target","sd_new_stone","sd_type_$(stone_type)"],width:1.01f,height:1.01f,response:true,stardew:{stone_type:"$(stone_type)",stone_hp:$(stone_hp),max_hp:$(stone_hp),required_pickaxe_tier:$(required_pickaxe_tier),ore_drop_type:"$(ore_drop_type)"}}

# 3. 初始化血量分数
$scoreboard players set @e[type=interaction,tag=sd_new_stone,limit=1,sort=nearest,distance=..1] sd_stone_hp $(stone_hp)
$scoreboard players set @e[type=interaction,tag=sd_new_stone,limit=1,sort=nearest,distance=..1] sd_stone_max_hp $(stone_hp)

# 4. 生成 item_display 实体 (视觉模型)
# 位置：方块中心 (~ ~0.5 ~)
# 使用 1.21.2 components 格式
$summon minecraft:item_display ~ ~0.5 ~ {Tags:["sd_stone_display","sd_new_stone"],item:{id:"minecraft:stone",count:1,components:{"minecraft:custom_model_data":$(model_cmd)}},transformation:{scale:[1.0f,1.0f,1.0f]}}

# 4.5 初始化视觉实体的高亮相关分数
scoreboard players set @e[type=item_display,tag=sd_new_stone,limit=1,sort=nearest,distance=..1] sd_mining_targeted 0
scoreboard players set @e[type=item_display,tag=sd_new_stone,limit=1,sort=nearest,distance=..1] sd_mining_targeted_prev 0

# 5. 移除临时标签
tag @e[tag=sd_new_stone] remove sd_new_stone
