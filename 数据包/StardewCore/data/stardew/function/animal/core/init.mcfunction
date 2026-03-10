# ================================================================
# 星露谷物语 - 畜牧业系统初始化
# ================================================================
# 用途：初始化畜牧业系统的scoreboard和常量
# 调用：仅在数据包加载时调用一次

# ================================================================
# 计分板初始化
# ================================================================

# 动物基础数据
scoreboard objectives add stardew.animal.id dummy "动物ID"
scoreboard objectives add stardew.animal.type dummy "动物类型"
scoreboard objectives add stardew.animal.building dummy "所属建筑ID"
scoreboard objectives add stardew.animal.age dummy "动物年龄(天)"

# 好感度系统 (0-1000)
scoreboard objectives add stardew.animal.friendship dummy "好感度"
scoreboard objectives add stardew.animal.friendship_today dummy "今日是否抚摸"

# 心情系统 (0-255)
scoreboard objectives add stardew.animal.mood dummy "心情值"
scoreboard objectives add stardew.animal.fed_today dummy "今日是否喂食"
scoreboard objectives add stardew.animal.ate_grass dummy "今日是否吃草"

# 产品系统
scoreboard objectives add stardew.animal.produce_days dummy "产品生成天数"
scoreboard objectives add stardew.animal.produce_ready dummy "产品是否就绪"
scoreboard objectives add stardew.animal.product_quality dummy "产品质量"
scoreboard objectives add stardew.animal.has_produce dummy "是否有产物可收集"
scoreboard objectives add stardew.animal.produce_cmd dummy "产物物品CMD"

# 繁殖系统
scoreboard objectives add stardew.animal.pregnancy dummy "怀孕状态"
scoreboard objectives add stardew.animal.pregnancy_days dummy "怀孕天数"
scoreboard objectives add stardew.animal.can_breed dummy "是否可繁殖"

# 建筑系统
scoreboard objectives add stardew.building.id dummy "建筑ID"
scoreboard objectives add stardew.building.type dummy "建筑类型"
scoreboard objectives add stardew.building.capacity dummy "建筑容量"
scoreboard objectives add stardew.building.animals dummy "当前动物数"

# 临时变量
scoreboard objectives add stardew.animal.temp dummy
scoreboard objectives add stardew.animal.just_petted dummy
scoreboard objectives add stardew.animal.selected_id dummy

# Animated Java 动画状态
scoreboard objectives add stardew.animal.anim_state dummy "动画状态"

# 鸡蛋产出系统
scoreboard objectives add stardew.temp.produce dummy "是否产出"
scoreboard objectives add stardew.temp.is_large dummy "是否大鸡蛋"
scoreboard objectives add stardew.temp.is_feather dummy "是否鸭毛"
scoreboard objectives add stardew.temp.quality dummy "产品品质"
scoreboard objectives add stardew.item.cmd dummy "物品CMD"

# 常量
scoreboard objectives add stardew.animal.const dummy
scoreboard players set #100 stardew.animal.const 100
scoreboard players set #200 stardew.animal.const 200
scoreboard players set #225 stardew.animal.const 225
scoreboard players set #300 stardew.animal.const 300
scoreboard players set #1000 stardew.animal.const 1000
scoreboard objectives add stardew.constant dummy "常量"
scoreboard players set #1500 stardew.constant 1500
scoreboard objectives add stardew.building.level dummy "建筑等级"

# 临时计算变量
scoreboard objectives add stardew.animal.temp dummy "临时变量"
scoreboard objectives add stardew.animal.random dummy "随机数"

# 初始化ID计数器
execute unless score #NextAnimalID stardew.animal.id = #NextAnimalID stardew.animal.id run scoreboard players set #NextAnimalID stardew.animal.id 1

# ================================================================
# 常量定义 - 动物类型
# ================================================================
# 1xx = 鸡舍动物
# 2xx = 畜棚动物

# 鸡舍动物
scoreboard players set #CHICKEN stardew.animal.type 101
scoreboard players set #DUCK stardew.animal.type 102
scoreboard players set #RABBIT stardew.animal.type 103
scoreboard players set #DINOSAUR stardew.animal.type 104
scoreboard players set #VOID_CHICKEN stardew.animal.type 105
scoreboard players set #GOLDEN_CHICKEN stardew.animal.type 106

# 畜棚动物
scoreboard players set #COW stardew.animal.type 201
scoreboard players set #GOAT stardew.animal.type 202
scoreboard players set #SHEEP stardew.animal.type 203
scoreboard players set #PIG stardew.animal.type 204
scoreboard players set #OSTRICH stardew.animal.type 205

# ================================================================
# 常量定义 - 建筑类型
# ================================================================
scoreboard players set #COOP stardew.building.type 1
scoreboard players set #BIG_COOP stardew.building.type 2
scoreboard players set #DELUXE_COOP stardew.building.type 3
scoreboard players set #BARN stardew.building.type 11
scoreboard players set #BIG_BARN stardew.building.type 12
scoreboard players set #DELUXE_BARN stardew.building.type 13

# ================================================================
# 常量定义 - 数值范围
# ================================================================
scoreboard players set #1200 stardew.animal.temp 1200
scoreboard players set #1000 stardew.animal.temp 1000
scoreboard players set #255 stardew.animal.temp 255
scoreboard players set #225 stardew.animal.temp 225
scoreboard players set #200 stardew.animal.temp 200
scoreboard players set #150 stardew.animal.temp 150
scoreboard players set #100 stardew.animal.temp 100
scoreboard players set #95 stardew.animal.temp 95
scoreboard players set #70 stardew.animal.temp 70
scoreboard players set #50 stardew.animal.temp 50
scoreboard players set #32 stardew.animal.temp 32
scoreboard players set #20 stardew.animal.temp 20
scoreboard players set #15 stardew.animal.temp 15
scoreboard players set #10 stardew.animal.temp 10
scoreboard players set #8 stardew.animal.temp 8
scoreboard players set #5 stardew.animal.temp 5
scoreboard players set #4 stardew.animal.temp 4
scoreboard players set #3 stardew.animal.temp 3
scoreboard players set #2 stardew.animal.temp 2
scoreboard players set #1 stardew.animal.temp 1
scoreboard players set #-1 stardew.animal.temp -1

# ================================================================
# 初始化建筑系统
# ================================================================
function stardew:building/core/init
