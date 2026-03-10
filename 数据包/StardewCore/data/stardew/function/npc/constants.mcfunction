# NPC系统常量定义
# 所有NPC共用的常量值

# ============ 维度ID定义 ============
# 用于 stardew.npc.dimension scoreboard
scoreboard players set #dim.overworld stardew.const 0
scoreboard players set #dim.pierre_house stardew.const 1
scoreboard players set #dim.saloon_interior stardew.const 2
scoreboard players set #dim.mine stardew.const 3
# 未来可以继续添加...

# ============ 地点ID定义 ============
# 用于 stardew.npc.schedule scoreboard
scoreboard players set #loc.home stardew.const 1
scoreboard players set #loc.town_square stardew.const 2
scoreboard players set #loc.graveyard stardew.const 3
scoreboard players set #loc.saloon stardew.const 4
scoreboard players set #loc.mine_entrance stardew.const 5
# 未来可以继续添加...

# ============ 特殊状态 ============
scoreboard players set #loc.moving stardew.const -1
scoreboard players set #loc.unknown stardew.const 0

# 提示信息
tellraw @a[tag=debug.npc] {"text":"[NPC系统] 常量已加载","color":"gray"}
