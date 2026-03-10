# stardew:mine/init.mcfunction
# 矿洞系统初始化 - 在数据包加载时执行

# ===== 记分板 =====
# 玩家层数相关
scoreboard objectives add sd_mine_floor dummy "当前层数"
scoreboard objectives add sd_mine_deepest dummy "最深到达层"

# 本层状态
scoreboard objectives add sd_mine_stones dummy "剩余石头数"
scoreboard objectives add sd_mine_ladder dummy "梯子已生成"

# 宝箱领取记录 (存储上次领取的游戏日)
scoreboard objectives add sd_chest_day_25 dummy "25层宝箱领取日"
scoreboard objectives add sd_chest_day_50 dummy "50层宝箱领取日"
scoreboard objectives add sd_chest_day_75 dummy "75层宝箱领取日"
scoreboard objectives add sd_chest_day_100 dummy "100层宝箱领取日"

# 临时变量
scoreboard objectives add sd_mine_temp dummy

# ===== Storage 初始化 =====
data merge storage stardew:mine {initialized: 1b}

# 楼层日期记录 (记录每层最后访问的游戏日)
# floor_days: {1: day, 2: day, ...}
execute unless data storage stardew:mine floor_days run data merge storage stardew:mine {floor_days: {}}

# ===== 生成 0 层入口区域 =====
# (首次加载时生成，后续检测是否已存在)
execute in stardew:mine run function stardew:mine/floor/generate_entrance
