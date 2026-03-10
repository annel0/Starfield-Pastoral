# stardew:fishing/treasure_chest/display_animation
# 播放宝箱战利品展示动画(复用鉴定系统动画)
# macro参数: cmd (custom_model_data)

# 在玩家位置生成item_display
$execute anchored eyes positioned ^ ^ ^ rotated ~ 0 run summon item_display ^ ^ ^ {Tags:["sd_treasure_item_display"],billboard:"fixed",item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":$(cmd)}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[2.2f,-0.2f,-2.2f],scale:[0f,0f,0f]}}

# 让实体在玩家位置,继承玩家水平朝向
execute as @e[tag=sd_treasure_item_display,limit=1] at @p rotated as @p run tp @s ~ ~1.62 ~ ~ 0

# 播放动画阶段（复用鉴定系统的动画阶段）
schedule function stardew:fishing/treasure_chest/anim_phase1a 1t
schedule function stardew:fishing/treasure_chest/anim_phase1b 4t
schedule function stardew:fishing/treasure_chest/anim_phase1c 6t

# 11tick后继续后半段动画
schedule function stardew:fishing/treasure_chest/anim_phase2a 11t
schedule function stardew:fishing/treasure_chest/anim_phase2b 14t

# 18tick后清除实体和标签
schedule function stardew:fishing/treasure_chest/cleanup_display 18t
