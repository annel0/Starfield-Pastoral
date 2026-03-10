# stardew:mining/spawn_stone_dispatch.mcfunction
# 根据手持物品CMD分发生成参数
# 使用 1.21.2 正确的 components 格式
# 执行者: 玩家 (@s)
# 执行位置: 生成位置（方块上方）

# --- 石头 (CMD 9101-9104) 4个主题 ---
execute if items entity @s weapon.mainhand *[custom_model_data=9101] run data modify storage stardew:macro_args args set value {stone_type:"stone",stone_hp:10,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7201,name:"石头(主题1)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9102] run data modify storage stardew:macro_args args set value {stone_type:"stone",stone_hp:10,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7202,name:"石头(主题2)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9103] run data modify storage stardew:macro_args args set value {stone_type:"stone",stone_hp:10,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7203,name:"石头(主题3)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9104] run data modify storage stardew:macro_args args set value {stone_type:"stone",stone_hp:10,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7204,name:"石头(主题4)"}

# --- 煤矿 (CMD 9105-9108) 4个主题 ---
execute if items entity @s weapon.mainhand *[custom_model_data=9105] run data modify storage stardew:macro_args args set value {stone_type:"coal",stone_hp:15,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7205,name:"煤矿(主题1)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9106] run data modify storage stardew:macro_args args set value {stone_type:"coal",stone_hp:15,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7206,name:"煤矿(主题2)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9107] run data modify storage stardew:macro_args args set value {stone_type:"coal",stone_hp:15,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7207,name:"煤矿(主题3)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9108] run data modify storage stardew:macro_args args set value {stone_type:"coal",stone_hp:15,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7208,name:"煤矿(主题4)"}

# --- 铜矿 (CMD 9109-9112) 4个主题 ---
execute if items entity @s weapon.mainhand *[custom_model_data=9109] run data modify storage stardew:macro_args args set value {stone_type:"copper",stone_hp:20,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7209,name:"铜矿(主题1)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9110] run data modify storage stardew:macro_args args set value {stone_type:"copper",stone_hp:20,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7210,name:"铜矿(主题2)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9111] run data modify storage stardew:macro_args args set value {stone_type:"copper",stone_hp:20,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7211,name:"铜矿(主题3)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9112] run data modify storage stardew:macro_args args set value {stone_type:"copper",stone_hp:20,required_pickaxe_tier:1,ore_drop_type:"resource",model_cmd:7212,name:"铜矿(主题4)"}

# --- 铁矿 (CMD 9113-9116) 4个主题 ---
execute if items entity @s weapon.mainhand *[custom_model_data=9113] run data modify storage stardew:macro_args args set value {stone_type:"iron",stone_hp:30,required_pickaxe_tier:2,ore_drop_type:"resource",model_cmd:7213,name:"铁矿(主题1)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9114] run data modify storage stardew:macro_args args set value {stone_type:"iron",stone_hp:30,required_pickaxe_tier:2,ore_drop_type:"resource",model_cmd:7214,name:"铁矿(主题2)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9115] run data modify storage stardew:macro_args args set value {stone_type:"iron",stone_hp:30,required_pickaxe_tier:2,ore_drop_type:"resource",model_cmd:7215,name:"铁矿(主题3)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9116] run data modify storage stardew:macro_args args set value {stone_type:"iron",stone_hp:30,required_pickaxe_tier:2,ore_drop_type:"resource",model_cmd:7216,name:"铁矿(主题4)"}

# --- 金矿 (CMD 9117-9120) 4个主题 ---
execute if items entity @s weapon.mainhand *[custom_model_data=9117] run data modify storage stardew:macro_args args set value {stone_type:"gold",stone_hp:40,required_pickaxe_tier:3,ore_drop_type:"resource",model_cmd:7217,name:"金矿(主题1)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9118] run data modify storage stardew:macro_args args set value {stone_type:"gold",stone_hp:40,required_pickaxe_tier:3,ore_drop_type:"resource",model_cmd:7218,name:"金矿(主题2)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9119] run data modify storage stardew:macro_args args set value {stone_type:"gold",stone_hp:40,required_pickaxe_tier:3,ore_drop_type:"resource",model_cmd:7219,name:"金矿(主题3)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9120] run data modify storage stardew:macro_args args set value {stone_type:"gold",stone_hp:40,required_pickaxe_tier:3,ore_drop_type:"resource",model_cmd:7220,name:"金矿(主题4)"}

# --- 钻石矿 (CMD 9121-9124) 4个主题 ---
execute if items entity @s weapon.mainhand *[custom_model_data=9121] run data modify storage stardew:macro_args args set value {stone_type:"diamond",stone_hp:60,required_pickaxe_tier:4,ore_drop_type:"resource",model_cmd:7221,name:"钻石矿(主题1)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9122] run data modify storage stardew:macro_args args set value {stone_type:"diamond",stone_hp:60,required_pickaxe_tier:4,ore_drop_type:"resource",model_cmd:7222,name:"钻石矿(主题2)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9123] run data modify storage stardew:macro_args args set value {stone_type:"diamond",stone_hp:60,required_pickaxe_tier:4,ore_drop_type:"resource",model_cmd:7223,name:"钻石矿(主题3)"}
execute if items entity @s weapon.mainhand *[custom_model_data=9124] run data modify storage stardew:macro_args args set value {stone_type:"diamond",stone_hp:60,required_pickaxe_tier:4,ore_drop_type:"resource",model_cmd:7224,name:"钻石矿(主题4)"}

# --- 宝石矿 (CMD 9125-9131) 单一外观 ---
execute if items entity @s weapon.mainhand *[custom_model_data=9125] run data modify storage stardew:macro_args args set value {stone_type:"quartz",stone_hp:25,required_pickaxe_tier:2,ore_drop_type:"gem",model_cmd:7225,name:"石英矿"}
execute if items entity @s weapon.mainhand *[custom_model_data=9126] run data modify storage stardew:macro_args args set value {stone_type:"earth_crystal",stone_hp:25,required_pickaxe_tier:2,ore_drop_type:"gem",model_cmd:7226,name:"地晶矿"}
execute if items entity @s weapon.mainhand *[custom_model_data=9127] run data modify storage stardew:macro_args args set value {stone_type:"frozen_tear",stone_hp:30,required_pickaxe_tier:2,ore_drop_type:"gem",model_cmd:7227,name:"泪晶矿"}
execute if items entity @s weapon.mainhand *[custom_model_data=9128] run data modify storage stardew:macro_args args set value {stone_type:"jade",stone_hp:40,required_pickaxe_tier:3,ore_drop_type:"gem",model_cmd:7228,name:"翡翠矿"}
execute if items entity @s weapon.mainhand *[custom_model_data=9129] run data modify storage stardew:macro_args args set value {stone_type:"ruby",stone_hp:45,required_pickaxe_tier:3,ore_drop_type:"gem",model_cmd:7229,name:"红宝石矿"}
execute if items entity @s weapon.mainhand *[custom_model_data=9130] run data modify storage stardew:macro_args args set value {stone_type:"amethyst",stone_hp:45,required_pickaxe_tier:3,ore_drop_type:"gem",model_cmd:7230,name:"紫水晶矿"}
execute if items entity @s weapon.mainhand *[custom_model_data=9131] run data modify storage stardew:macro_args args set value {stone_type:"prismatic_shard",stone_hp:80,required_pickaxe_tier:4,ore_drop_type:"gem",model_cmd:7231,name:"五彩碎片矿"}

# 执行宏函数
execute if data storage stardew:macro_args args run function stardew:mining/spawn_stone_impl with storage stardew:macro_args args

# 标记宝石矿（用于右键收集）
execute if data storage stardew:macro_args args{ore_drop_type:"gem"} run tag @e[type=interaction,tag=sd_stone,limit=1,sort=nearest,distance=..2] add sd_gem_ore

# 清理参数
data remove storage stardew:macro_args args
