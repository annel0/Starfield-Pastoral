# data/stardew/function/farming/level_up_action.mcfunction
# [执行者: 玩家]
# 作用：执行农耕升级动作，并消耗升级所需的经验值

# 1. 经验消耗：消耗当前等级所需的经验值
scoreboard players operation @s sd_farming_xp -= @s sd_level_xp_req

# 2. 提升等级
scoreboard players add @s sd_farming_lvl 1

# 3. 升级特效与反馈
execute at @s run particle minecraft:totem_of_undying ~ ~1 ~ 0.5 0.5 0.5 0.1 20
execute at @s run playsound minecraft:entity.player.levelup player @s ~ ~ ~ 1 1

# 4. 升级提示
tellraw @s ["",{"text":"[技能] ","color":"gold","bold":true},{"text":"恭喜！你的农耕等级提升到 ","color":"white"},{"score":{"name":"@s","objective":"sd_farming_lvl"},"color":"yellow","bold":true}]

# 5. 工具熟练度提升提示 (锄头和洒水壶能量消耗减少)
tellraw @s ["",{"text":"[提示] ","color":"green"},{"text":"锄头和洒水壶的能量消耗已减少!","color":"gray"}]

# 6. 根据等级解锁配方/功能
# Level 1: 稻草人, 基础肥料
execute if score @s sd_farming_lvl matches 1 run tellraw @s ["",{"text":"[解锁] ","color":"aqua"},{"text":"配方: 稻草人、基础肥料","color":"white"}]

# Level 2: 蛋黄酱机, 石栅栏, 洒水器
execute if score @s sd_farming_lvl matches 2 run tellraw @s ["",{"text":"[解锁] ","color":"aqua"},{"text":"配方: 蛋黄酱机、石栅栏、洒水器","color":"white"}]

# Level 3: 蜂房, 生长激素, 农夫午餐
execute if score @s sd_farming_lvl matches 3 run tellraw @s ["",{"text":"[解锁] ","color":"aqua"},{"text":"配方: 蜂房、生长激素、农夫午餐","color":"white"}]

# Level 4: 保鲜罐, 基础保水土壤, 铁栅栏
execute if score @s sd_farming_lvl matches 4 run tellraw @s ["",{"text":"[解锁] ","color":"aqua"},{"text":"配方: 保鲜罐、基础保水土壤、铁栅栏","color":"white"}]

# Level 5: 选择职业
execute if score @s sd_farming_lvl matches 5 run tellraw @s ["",{"text":"[重要] ","color":"light_purple","bold":true},{"text":"你可以选择职业了! 请查看技能菜单","color":"white","bold":false}]
# TODO: 实现职业选择系统 (Rancher vs Tiller)

# Level 6: 奶酪压榨机, 硬木栅栏, 优质洒水器
execute if score @s sd_farming_lvl matches 6 run tellraw @s ["",{"text":"[解锁] ","color":"aqua"},{"text":"配方: 奶酪压榨机、硬木栅栏、优质洒水器","color":"white"}]

# Level 7: 织布机, 优质保水土壤
execute if score @s sd_farming_lvl matches 7 run tellraw @s ["",{"text":"[解锁] ","color":"aqua"},{"text":"配方: 织布机、优质保水土壤","color":"white"}]

# Level 8: 油制机, 小桶, 高级生长激素
execute if score @s sd_farming_lvl matches 8 run tellraw @s ["",{"text":"[解锁] ","color":"aqua"},{"text":"配方: 油制机、小桶、高级生长激素","color":"white"}]

# Level 9: 种子制造机, 铱金洒水器, 优质肥料
execute if score @s sd_farming_lvl matches 9 run tellraw @s ["",{"text":"[解锁] ","color":"aqua"},{"text":"配方: 种子制造机、铱金洒水器、优质肥料","color":"white"}]

# Level 10: 选择高级职业
execute if score @s sd_farming_lvl matches 10 run tellraw @s ["",{"text":"[重要] ","color":"light_purple","bold":true},{"text":"你可以选择高级职业了! 请查看技能菜单","color":"white","bold":false}]
# TODO: 实现高级职业选择系统 (Coopmaster/Shepherd vs Artisan/Agriculturist)

# 7. 递归检查 - 升级成功后立即检查是否还能继续升级
execute if score @s sd_farming_lvl matches 1..9 run function stardew:farming/level_up_check
