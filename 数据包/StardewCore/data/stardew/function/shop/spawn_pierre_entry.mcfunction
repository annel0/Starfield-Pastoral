# 生成Pierre商店入口interaction实体
# 用法: 在想要放置商店入口的位置执行此命令
# 示例: execute positioned <x> <y> <z> run function stardew:shop/spawn_pierre_entry

# 召唤interaction实体
summon interaction ~ ~ ~ {width:1f,height:2f,Tags:["shop_entry","pierre_shop","persistent"],CustomName:'{"text":"Pierre的杂货店","color":"gold"}'}

# 可选: 召唤文字显示提示
summon text_display ~ ~2.5 ~ {text:'{"text":"Pierre的杂货店\\n右键进入","color":"yellow","bold":true}',alignment:"center",background:0,billboard:"center",Tags:["shop_sign","pierre_shop"]}

# 提示管理员
tellraw @a[tag=admin] [{"text":"[商店系统] ","color":"gold"},{"text":"Pierre商店入口已创建在 ","color":"green"},{"nbt":"Pos","entity":"@e[type=interaction,tag=pierre_shop,limit=1,sort=nearest]","color":"aqua"}]

# 播放生成音效
playsound minecraft:block.anvil.place master @a[distance=..10] ~ ~ ~ 1 1.2
