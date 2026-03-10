# data/stardew/functions/fishing/loot_drop.mcfunction
# 正确的 loot table 路径（不使用 spring/summer 等子文件夹）

execute if score @s sd_fish_type matches 41000 run function stardew:fishing/api/drop_quality {id:"smallmouth_bass"}
execute if score @s sd_fish_type matches 41000 run function stardew:fishing/reveal_animation {cmd:41000}
execute if score @s sd_fish_type matches 41000 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"小嘴鲈鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 41010 run function stardew:fishing/api/drop_quality {id:"shad"}
execute if score @s sd_fish_type matches 41010 run function stardew:fishing/reveal_animation {cmd:41010}
execute if score @s sd_fish_type matches 41010 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"西鲱","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 41020 run function stardew:fishing/api/drop_quality {id:"catfish"}
execute if score @s sd_fish_type matches 41020 run function stardew:fishing/reveal_animation {cmd:41020}
execute if score @s sd_fish_type matches 41020 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"鲶鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 41030 run function stardew:fishing/api/drop_quality {id:"sunfish"}
execute if score @s sd_fish_type matches 41030 run function stardew:fishing/reveal_animation {cmd:41030}
execute if score @s sd_fish_type matches 41030 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"太阳鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 41040 run function stardew:fishing/api/drop_quality {id:"anchovy"}
execute if score @s sd_fish_type matches 41040 run function stardew:fishing/reveal_animation {cmd:41040}
execute if score @s sd_fish_type matches 41040 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"鳀鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 41050 run function stardew:fishing/api/drop_quality {id:"sardine"}
execute if score @s sd_fish_type matches 41050 run function stardew:fishing/reveal_animation {cmd:41050}
execute if score @s sd_fish_type matches 41050 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"沙丁鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 41060 run function stardew:fishing/api/drop_quality {id:"bullhead"}
execute if score @s sd_fish_type matches 41060 run function stardew:fishing/reveal_animation {cmd:41060}
execute if score @s sd_fish_type matches 41060 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"大头鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 41070 run function stardew:fishing/api/drop_quality {id:"carp"}
execute if score @s sd_fish_type matches 41070 run function stardew:fishing/reveal_animation {cmd:41070}
execute if score @s sd_fish_type matches 41070 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"鲤鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 41080 run function stardew:fishing/api/drop_quality {id:"halibut"}
execute if score @s sd_fish_type matches 41080 run function stardew:fishing/reveal_animation {cmd:41080}
execute if score @s sd_fish_type matches 41080 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"大比目鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 41090 run function stardew:fishing/api/drop_quality {id:"eel"}
execute if score @s sd_fish_type matches 41090 run function stardew:fishing/reveal_animation {cmd:41090}
execute if score @s sd_fish_type matches 41090 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"鳗鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 41990 run function stardew:fishing/api/drop_quality {id:"legend_crimson"}
execute if score @s sd_fish_type matches 41990 run function stardew:fishing/reveal_animation {cmd:41990}
execute if score @s sd_fish_type matches 41990 run tellraw @s [{"text":"🌟 ","color":"gold"},{"text":"钓到了 ","color":"yellow"},{"text":"传说深红鱼","color":"light_purple","bold":true},{"text":"!!!","color":"yellow"}]

execute if score @s sd_fish_type matches 42000 run function stardew:fishing/api/drop_quality {id:"rainbow_trout"}
execute if score @s sd_fish_type matches 42000 run function stardew:fishing/reveal_animation {cmd:42000}
execute if score @s sd_fish_type matches 42000 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"虹鳟鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 42010 run function stardew:fishing/api/drop_quality {id:"tilapia"}
execute if score @s sd_fish_type matches 42010 run function stardew:fishing/reveal_animation {cmd:42010}
execute if score @s sd_fish_type matches 42010 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"罗非鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 42020 run function stardew:fishing/api/drop_quality {id:"red_mullet"}
execute if score @s sd_fish_type matches 42020 run function stardew:fishing/reveal_animation {cmd:42020}
execute if score @s sd_fish_type matches 42020 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"红鲻鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 42030 run function stardew:fishing/api/drop_quality {id:"pike"}
execute if score @s sd_fish_type matches 42030 run function stardew:fishing/reveal_animation {cmd:42030}
execute if score @s sd_fish_type matches 42030 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"狗鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 42040 run function stardew:fishing/api/drop_quality {id:"tuna"}
execute if score @s sd_fish_type matches 42040 run function stardew:fishing/reveal_animation {cmd:42040}
execute if score @s sd_fish_type matches 42040 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"金枪鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 42050 run function stardew:fishing/api/drop_quality {id:"sturgeon"}
execute if score @s sd_fish_type matches 42050 run function stardew:fishing/reveal_animation {cmd:42050}
execute if score @s sd_fish_type matches 42050 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"鲟鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 42060 run function stardew:fishing/api/drop_quality {id:"pufferfish"}
execute if score @s sd_fish_type matches 42060 run function stardew:fishing/reveal_animation {cmd:42060}
execute if score @s sd_fish_type matches 42060 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"河豚","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 42070 run function stardew:fishing/api/drop_quality {id:"octopus"}
execute if score @s sd_fish_type matches 42070 run function stardew:fishing/reveal_animation {cmd:42070}
execute if score @s sd_fish_type matches 42070 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"章鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 42080 run function stardew:fishing/api/drop_quality {id:"super_cucumber"}
execute if score @s sd_fish_type matches 42080 run function stardew:fishing/reveal_animation {cmd:42080}
execute if score @s sd_fish_type matches 42080 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"超级海参","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 42090 run function stardew:fishing/api/drop_quality {id:"dorado"}
execute if score @s sd_fish_type matches 42090 run function stardew:fishing/reveal_animation {cmd:42090}
execute if score @s sd_fish_type matches 42090 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"黄金鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 42990 run function stardew:fishing/api/drop_quality {id:"legend_angler"}
execute if score @s sd_fish_type matches 42990 run function stardew:fishing/reveal_animation {cmd:42990}
execute if score @s sd_fish_type matches 42990 run tellraw @s [{"text":"🌟 ","color":"gold"},{"text":"钓到了 ","color":"yellow"},{"text":"传说琵琶鱼","color":"light_purple","bold":true},{"text":"!!!","color":"yellow"}]

execute if score @s sd_fish_type matches 43000 run function stardew:fishing/api/drop_quality {id:"salmon"}
execute if score @s sd_fish_type matches 43000 run function stardew:fishing/reveal_animation {cmd:43000}
execute if score @s sd_fish_type matches 43000 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"鲑鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 43010 run function stardew:fishing/api/drop_quality {id:"tiger_trout"}
execute if score @s sd_fish_type matches 43010 run function stardew:fishing/reveal_animation {cmd:43010}
execute if score @s sd_fish_type matches 43010 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"虎纹鳟鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 43020 run function stardew:fishing/api/drop_quality {id:"largemouth_bass"}
execute if score @s sd_fish_type matches 43020 run function stardew:fishing/reveal_animation {cmd:43020}
execute if score @s sd_fish_type matches 43020 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"大嘴鲈鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 43030 run function stardew:fishing/api/drop_quality {id:"red_snapper"}
execute if score @s sd_fish_type matches 43030 run function stardew:fishing/reveal_animation {cmd:43030}
execute if score @s sd_fish_type matches 43030 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"红鲷鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 43040 run function stardew:fishing/api/drop_quality {id:"sea_cucumber"}
execute if score @s sd_fish_type matches 43040 run function stardew:fishing/reveal_animation {cmd:43040}
execute if score @s sd_fish_type matches 43040 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"海参","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 43050 run function stardew:fishing/api/drop_quality {id:"walleye"}
execute if score @s sd_fish_type matches 43050 run function stardew:fishing/reveal_animation {cmd:43050}
execute if score @s sd_fish_type matches 43050 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"大眼鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 43060 run function stardew:fishing/api/drop_quality {id:"midnight_carp"}
execute if score @s sd_fish_type matches 43060 run function stardew:fishing/reveal_animation {cmd:43060}
execute if score @s sd_fish_type matches 43060 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"午夜鲤鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 43070 run function stardew:fishing/api/drop_quality {id:"sea_eel"}
execute if score @s sd_fish_type matches 43070 run function stardew:fishing/reveal_animation {cmd:43070}
execute if score @s sd_fish_type matches 43070 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"海鳗","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 43990 run function stardew:fishing/api/drop_quality {id:"legend_angler"}
execute if score @s sd_fish_type matches 43990 run function stardew:fishing/reveal_animation {cmd:43990}
execute if score @s sd_fish_type matches 43990 run tellraw @s [{"text":"🌟 ","color":"gold"},{"text":"钓到了 ","color":"yellow"},{"text":"传说琵琶鱼","color":"light_purple","bold":true},{"text":"!!!","color":"yellow"}]

execute if score @s sd_fish_type matches 44000 run function stardew:fishing/api/drop_quality {id:"perch"}
execute if score @s sd_fish_type matches 44000 run function stardew:fishing/reveal_animation {cmd:44000}
execute if score @s sd_fish_type matches 44000 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"河鲈","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 44010 run function stardew:fishing/api/drop_quality {id:"squid"}
execute if score @s sd_fish_type matches 44010 run function stardew:fishing/reveal_animation {cmd:44010}
execute if score @s sd_fish_type matches 44010 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"鱿鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 44020 run function stardew:fishing/api/drop_quality {id:"albacore"}
execute if score @s sd_fish_type matches 44020 run function stardew:fishing/reveal_animation {cmd:44020}
execute if score @s sd_fish_type matches 44020 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"青花鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 44030 run function stardew:fishing/api/drop_quality {id:"lingcod"}
execute if score @s sd_fish_type matches 44030 run function stardew:fishing/reveal_animation {cmd:44030}
execute if score @s sd_fish_type matches 44030 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"狭鳕","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 44990 run function stardew:fishing/api/drop_quality {id:"legend_glacier"}
execute if score @s sd_fish_type matches 44990 run function stardew:fishing/reveal_animation {cmd:44990}
execute if score @s sd_fish_type matches 44990 run tellraw @s [{"text":"🌟 ","color":"gold"},{"text":"钓到了 ","color":"yellow"},{"text":"传说冰川鱼","color":"light_purple","bold":true},{"text":"!!!","color":"yellow"}]

execute if score @s sd_fish_type matches 45000 run function stardew:fishing/api/drop_quality {id:"ghostfish"}
execute if score @s sd_fish_type matches 45000 run function stardew:fishing/reveal_animation {cmd:45000}
execute if score @s sd_fish_type matches 45000 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"幽灵鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 45010 run function stardew:fishing/api/drop_quality {id:"stonefish"}
execute if score @s sd_fish_type matches 45010 run function stardew:fishing/reveal_animation {cmd:45010}
execute if score @s sd_fish_type matches 45010 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"石鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 45020 run function stardew:fishing/api/drop_quality {id:"ice_pip"}
execute if score @s sd_fish_type matches 45020 run function stardew:fishing/reveal_animation {cmd:45020}
execute if score @s sd_fish_type matches 45020 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"冰嘟嘟","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 45030 run function stardew:fishing/api/drop_quality {id:"lava_eel"}
execute if score @s sd_fish_type matches 45030 run function stardew:fishing/reveal_animation {cmd:45030}
execute if score @s sd_fish_type matches 45030 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"熔岩鳗鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 45040 run function stardew:fishing/api/drop_quality {id:"sandfish"}
execute if score @s sd_fish_type matches 45040 run function stardew:fishing/reveal_animation {cmd:45040}
execute if score @s sd_fish_type matches 45040 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"沙鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 45050 run function stardew:fishing/api/drop_quality {id:"scorpion_carp"}
execute if score @s sd_fish_type matches 45050 run function stardew:fishing/reveal_animation {cmd:45050}
execute if score @s sd_fish_type matches 45050 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"蝎鲤","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 46000 run function stardew:fishing/api/drop_quality {id:"bream"}
execute if score @s sd_fish_type matches 46000 run function stardew:fishing/reveal_animation {cmd:46000}
execute if score @s sd_fish_type matches 46000 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"鲂鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 46010 run function stardew:fishing/api/drop_quality {id:"chub"}
execute if score @s sd_fish_type matches 46010 run function stardew:fishing/reveal_animation {cmd:46010}
execute if score @s sd_fish_type matches 46010 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"鳟鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 46030 run function stardew:fishing/api/drop_quality {id:"flounder"}
execute if score @s sd_fish_type matches 46030 run function stardew:fishing/reveal_animation {cmd:46030}
execute if score @s sd_fish_type matches 46030 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"比目鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 46040 run function stardew:fishing/api/drop_quality {id:"woodskip"}
execute if score @s sd_fish_type matches 46040 run function stardew:fishing/reveal_animation {cmd:46040}
execute if score @s sd_fish_type matches 46040 run tellraw @s [{"text":"🐟 ","color":"aqua"},{"text":"钓到了 ","color":"gray"},{"text":"木跃鱼","color":"green","bold":true},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 40010 run function stardew:fishing/api/drop_simple {id:"trash/driftwood"}
execute if score @s sd_fish_type matches 40010 run tellraw @s [{"text":"🗑️ ","color":"gray"},{"text":"钓到了 ","color":"dark_gray"},{"text":"漂流木","color":"gray"},{"text":"...","color":"dark_gray"}]

execute if score @s sd_fish_type matches 40020 run function stardew:fishing/api/drop_simple {id:"trash/garbage"}
execute if score @s sd_fish_type matches 40020 run tellraw @s [{"text":"🗑️ ","color":"gray"},{"text":"钓到了 ","color":"dark_gray"},{"text":"垃圾","color":"gray"},{"text":"...","color":"dark_gray"}]

execute if score @s sd_fish_type matches 40030 run function stardew:fishing/api/drop_simple {id:"trash/newspaper"}
execute if score @s sd_fish_type matches 40030 run tellraw @s [{"text":"🗑️ ","color":"gray"},{"text":"钓到了 ","color":"dark_gray"},{"text":"报纸","color":"gray"},{"text":"...","color":"dark_gray"}]

execute if score @s sd_fish_type matches 40040 run function stardew:fishing/api/drop_simple {id:"trash/glasses"}
execute if score @s sd_fish_type matches 40040 run tellraw @s [{"text":"🗑️ ","color":"gray"},{"text":"钓到了 ","color":"dark_gray"},{"text":"眼镜","color":"gray"},{"text":"...","color":"dark_gray"}]

execute if score @s sd_fish_type matches 40050 run function stardew:fishing/api/drop_simple {id:"trash/cola"}
execute if score @s sd_fish_type matches 40050 run tellraw @s [{"text":"🗑️ ","color":"gray"},{"text":"钓到了 ","color":"dark_gray"},{"text":"可乐","color":"gray"},{"text":"...","color":"dark_gray"}]

execute if score @s sd_fish_type matches 40060 run function stardew:fishing/api/drop_simple {id:"trash/plant"}
execute if score @s sd_fish_type matches 40060 run tellraw @s [{"text":"🗑️ ","color":"gray"},{"text":"钓到了 ","color":"dark_gray"},{"text":"植物","color":"gray"},{"text":"...","color":"dark_gray"}]

execute if score @s sd_fish_type matches 40100 run function stardew:fishing/api/drop_simple {id:"resource/green_algae"}
execute if score @s sd_fish_type matches 40100 run tellraw @s [{"text":"🌿 ","color":"green"},{"text":"钓到了 ","color":"gray"},{"text":"绿藻","color":"green"},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 40110 run function stardew:fishing/api/drop_simple {id:"resource/white_algae"}
execute if score @s sd_fish_type matches 40110 run tellraw @s [{"text":"🌿 ","color":"white"},{"text":"钓到了 ","color":"gray"},{"text":"白藻","color":"white"},{"text":"!","color":"gray"}]

execute if score @s sd_fish_type matches 40120 run function stardew:fishing/api/drop_simple {id:"resource/seaweed"}
execute if score @s sd_fish_type matches 40120 run tellraw @s [{"text":"🌿 ","color":"dark_green"},{"text":"钓到了 ","color":"gray"},{"text":"海草","color":"green"},{"text":"!","color":"gray"}]

# ==========================================
# 经验值与清理 (保持原样)
# ==========================================
# 公式: 基础经验 (20) + 鱼力 (sd_fish_power_fish * 5)
scoreboard players operation @s sd_fishing_xp += #20 sd_const
scoreboard players operation @s sd_fishing_xp += @s sd_fish_power_fish
scoreboard players set @s sd_const 20



# ==========================================
# 结算与清理
# ==========================================
tag @e[tag=new_drop] remove new_drop
execute as @e[type=fishing_bobber,distance=..32,limit=1] run kill @s