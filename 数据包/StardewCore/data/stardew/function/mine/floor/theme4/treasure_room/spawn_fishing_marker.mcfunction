# 生成 Theme4 Treasure Room 的钓鱼区域标记
# 参数: $(z27)
# 固定位置: X=14 Z+27

# 生成钓鱼标记点，使用紫色（theme4配色）
$execute in stardew:mine run summon marker 14.5 64 $(z27) {Tags:["sd_mine_entity","sd_fishing_marker","sd_fishing_theme4"]}

# 生成视觉效果 - 紫色粒子效果区域
$execute in stardew:mine run summon text_display 14.5 65.5 $(z27) {Tags:["sd_mine_entity","sd_fishing_visual"],alignment:"center",brightness:{sky:15,block:15},text:'{"text":"🎣 钓鱼区域","color":"light_purple","bold":true}',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.5f,1.5f,1.5f]},background:0,billboard:"center"}
