# stardew:fishing/treasure_chest/display_animation_start
# 播放宝箱物品展示动画(逆时针绕玩家圆周运动)
# macro参数: display_cmd, display_item_id (从storage.stardew:treasure读取)

# 在玩家位置生成item_display,使用anchored eyes固定高度,rotated ~ 0保持水平
# 初始translation在右后方(2.2,-0.2,-2.2),scale为0实现瞬间出现效果
# 关键：使用正确的物品ID和CMD
$execute anchored eyes positioned ^ ^ ^ rotated ~ 0 run summon item_display ^ ^ ^ {Tags:["sd_treasure_item_display"],billboard:"fixed",item:{id:"$(display_item_id)",count:1,components:{"minecraft:custom_model_data":$(display_cmd)}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[2.2f,-0.2f,-2.2f],scale:[0f,0f,0f]}}

# 关键：让实体在玩家位置，继承玩家水平朝向
execute as @e[tag=sd_treasure_item_display,limit=1] at @p rotated as @p run tp @s ~ ~1.62 ~ ~ 0

# 5阶段动画 (调整后时间线)
# phase1a: 1t开始 (持续3t) -> 到4t
# phase1b: 5t开始 (持续4t) -> 到9t  
# phase1c: 10t开始 (持续10t) -> 到20t (正前方停留展示)
# phase2a: 21t开始 (持续3t) -> 到24t
# phase2b: 25t开始 (持续3t) -> 到28t
schedule function stardew:fishing/treasure_chest/anim_phase1a 1t
schedule function stardew:fishing/treasure_chest/anim_phase1b 5t
schedule function stardew:fishing/treasure_chest/anim_phase1c 10t
schedule function stardew:fishing/treasure_chest/anim_phase2a 21t
schedule function stardew:fishing/treasure_chest/anim_phase2b 25t

# 第10tick播放粒子和给予物品 (正前方展示时)
schedule function stardew:fishing/treasure_chest/give_items 10t

# 30tick后清除
schedule function stardew:fishing/treasure_chest/cleanup_display 30t
