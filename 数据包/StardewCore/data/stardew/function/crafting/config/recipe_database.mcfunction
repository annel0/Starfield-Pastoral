# data/stardew/function/crafting/config/recipe_database.mcfunction
# 配方数据库初始化
# 用storage存储所有配方信息和解锁顺序

# 清空配方数据库
data remove storage stardew:recipes database

# 初始化配方列表（按分类）
data modify storage stardew:recipes database set value {tools:[],equipment:[],building:[],consumable:[],furniture:[]}

# === 设备分类配方 ===

# 配方201: 熔炉
# {id:配方ID, name:显示名称, cmd:物品CMD, materials:[{name, cmd, count}], order:解锁顺序(越小越靠前)}
data modify storage stardew:recipes database.equipment append value {id:201,name:"熔炉",cmd:3001,materials:[{name:"石头",cmd:7001,count:25},{name:"铜粒",cmd:7003,count:20}]}

# === 未来可以添加更多配方 ===
# data modify storage stardew:recipes database.equipment append value {id:202,name:"xxx",cmd:xxxx,materials:[...]}
