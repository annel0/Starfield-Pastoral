# stardew:mine/debug/generate_entrance_delayed.mcfunction
# 延迟执行的大厅生成 (玩家已经在维度内)

# 清空区域
execute in stardew:mine run fill -10 63 -10 10 70 10 minecraft:air

# 生成地面
execute in stardew:mine run fill -7 64 -7 7 64 7 minecraft:stone_bricks

# 墙壁
execute in stardew:mine run fill -8 64 -8 8 68 8 minecraft:stone_bricks hollow
execute in stardew:mine run fill -7 65 -7 7 67 7 minecraft:air

# 天花板
execute in stardew:mine run fill -7 68 -7 7 68 7 minecraft:stone_bricks

# 光源
execute in stardew:mine run setblock 0 67 0 minecraft:lantern[hanging=true]
execute in stardew:mine run setblock -4 67 -4 minecraft:lantern[hanging=true]
execute in stardew:mine run setblock 4 67 -4 minecraft:lantern[hanging=true]
execute in stardew:mine run setblock -4 67 4 minecraft:lantern[hanging=true]
execute in stardew:mine run setblock 4 67 4 minecraft:lantern[hanging=true]

# 洞口
execute in stardew:mine run fill 6 65 -1 7 66 1 minecraft:air
execute in stardew:mine run setblock 5 67 0 minecraft:lantern[hanging=true]

# 清除旧实体
execute in stardew:mine run kill @e[tag=sd_mine_entity]

# 生成交互实体
execute in stardew:mine run summon minecraft:interaction 7 65 0 {Tags:["sd_mine_next_floor","sd_mine_entity"],width:2.0f,height:2.0f}
execute in stardew:mine run summon minecraft:text_display 7 66.5 0 {Tags:["sd_mine_entity"],text:'{"text":"→ 进入矿洞","color":"yellow"}',billboard:"vertical",shadow:true,transformation:{scale:[0.8f,0.8f,0.8f]}}

execute in stardew:mine run summon minecraft:interaction -7 65 0 {Tags:["sd_mine_exit","sd_mine_entity"],width:2.0f,height:2.0f}
execute in stardew:mine run summon minecraft:text_display -7 66.5 0 {Tags:["sd_mine_entity"],text:'{"text":"← 返回地面","color":"aqua"}',billboard:"vertical",shadow:true,transformation:{scale:[0.8f,0.8f,0.8f]}}

# 电梯
execute in stardew:mine run setblock -5 65 0 minecraft:iron_block
execute in stardew:mine run setblock -5 66 0 minecraft:iron_block
execute in stardew:mine run summon minecraft:interaction -5 65 0 {Tags:["sd_mine_elevator","sd_mine_entity"],width:1.5f,height:2.5f}
execute in stardew:mine run summon minecraft:text_display -5 67.3 0 {Tags:["sd_mine_entity"],text:'{"text":"⬍ 电梯","color":"aqua","bold":true}',billboard:"vertical",shadow:true,transformation:{scale:[0.7f,0.7f,0.7f]}}

# 把玩家放回正确位置
execute in stardew:mine run tp @a[distance=..100] 0 65 0

tellraw @a[distance=..100] {"text":"[调试] 0层入口已生成！","color":"green"}
