# ================================================================
# 星露谷物语 - 建筑系统初始化
# ================================================================
# 用途：初始化建筑系统的scoreboard和常量
# 调用：从主init调用

# ================================================================
# 建筑系统计分板
# ================================================================

# 建筑基础数据
scoreboard objectives add stardew.building.id dummy "建筑ID"
scoreboard objectives add stardew.building.type dummy "建筑类型(1=鸡舍,2=畜棚)"
scoreboard objectives add stardew.building.tier dummy "建筑等级(1/2/3)"
scoreboard objectives add stardew.building.capacity dummy "建筑容量"
scoreboard objectives add stardew.building.animal_count dummy "当前动物数"
scoreboard objectives add stardew.building.door_open dummy "门状态(0=关,1=开)"

# 建筑ID管理（栈模式）
scoreboard objectives add stardew.building.next_coop_id dummy "下一个鸡舍ID(1-10)"
scoreboard objectives add stardew.building.next_barn_id dummy "下一个畜棚ID(11-20)"

# 动物归属和位置
scoreboard objectives add stardew.animal.building_id dummy "所属建筑ID"
scoreboard objectives add stardew.animal.is_outside dummy "是否在外面(0/1)"
scoreboard objectives add stardew.animal.going_home dummy "正在回家(0/1)"
scoreboard objectives add stardew.animal.tp_cooldown dummy "传送冷却(tick)"

# 临时变量
scoreboard objectives add stardew.building.temp dummy "临时变量"

# ================================================================
# 初始化常量
# ================================================================

# ID范围常量
scoreboard players set #1 stardew.building.temp 1
scoreboard players set #10 stardew.building.temp 10
scoreboard players set #11 stardew.building.temp 11
scoreboard players set #20 stardew.building.temp 20

# 容量常量
scoreboard players set #tier1_cap stardew.building.temp 4
scoreboard players set #tier2_cap stardew.building.temp 8
scoreboard players set #tier3_cap stardew.building.temp 12

# 距离常量
scoreboard players set #range stardew.building.temp 10
scoreboard players set #range_close stardew.building.temp 20

# 初始化ID计数器（如果不存在）
execute unless score #NextCoopID stardew.building.next_coop_id = #NextCoopID stardew.building.next_coop_id run scoreboard players set #NextCoopID stardew.building.next_coop_id 1
execute unless score #NextBarnID stardew.building.next_barn_id = #NextBarnID stardew.building.next_barn_id run scoreboard players set #NextBarnID stardew.building.next_barn_id 11