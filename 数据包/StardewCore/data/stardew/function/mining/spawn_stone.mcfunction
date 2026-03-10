# stardew:mining/spawn_stone.mcfunction
# 玩家右键生成工具，使用射线检测找到目标方块位置，生成石头/矿石实体
# 射线系统：从玩家视线发射，检测到固体方块后在其上方生成实体

# 初始化射线
scoreboard players set @s sd_ray_steps 0

# 启动射线循环（从玩家眼睛位置开始）
execute at @s anchored eyes positioned ^ ^ ^ run function stardew:mining/raycast_spawn_loop


# 2. 具体生成逻辑（分五种石头CMD，矿石CMD见下方）
# 由spawn_stone_dispatch.mcfunction分发，spawn_stone_impl.mcfunction实现
# 例如：CMD 9101-9105为五种石头生成工具，CMD 9111-9117为矿石生成工具
# 视觉实体CMD分配如下：
# 普通石头：7201-7205
# 煤矿：7206
# 铜矿：7207
# 铁矿：7208
# 金矿：7209
# 钻石矿：7210
# 宝石矿：7211-7217

# 3. 由debug胡萝卜吊杆右键触发，传递参数（stone_type、model_cmd、hp、pickaxe_tier、ore_type）
# 具体实现见spawn_stone_impl.mcfunction

# 4. 生成interaction实体（用于碰撞箱和挖掘逻辑，无文字展示）
# 生成item_display实体（用于视觉展示，无文字展示）
# interaction实体需占据一整个方块（hitbox=1方块），item_display实体用CMD分配模型

# 5. 绑定血量分数，初始化
# 由spawn_stone_impl.mcfunction完成

# 6. 允许正常挖掘逻辑（镐子伤害、等级判定、血量扣除、掉落物、经验、疲劳提示）
# 具体见on_mine_impl.mcfunction

# 7. 五种矿井主体石头CMD自动分配如下：
# 1号矿井石头：7201
# 2号矿井石头：7202
# 3号矿井石头：7203
# 4号矿井石头：7204
# 5号矿井石头：7205
# 对应debug胡萝卜吊杆：9101-9105

# 8. 其他矿石CMD分配：煤矿7206、铜矿7207、铁矿7208、金矿7209、钻石矿7210、宝石矿7211-7217
# 对应debug胡萝卜吊杆：9111-9117

# 9. 交互入口由debug胡萝卜吊杆右键触发，自动调用本函数

# 具体生成与参数传递见spawn_stone_impl.mcfunction
