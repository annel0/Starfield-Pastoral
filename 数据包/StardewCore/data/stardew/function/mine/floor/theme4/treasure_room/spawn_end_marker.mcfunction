# 在原来坑的位置生成"已到达矿洞最深处"的文本显示
# 参数: $(z13)
# 固定位置: X=9 Z+13 (原来的坑位置)

$execute in stardew:mine run summon text_display 9.5 65.5 $(z13) {Tags:["sd_mine_entity","sd_end_marker"],alignment:"center",brightness:{sky:15,block:15},text:'{"text":"⭐ 已到达矿洞最深处 ⭐","color":"gold","bold":true}',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[2f,2f,2f]},background:0,billboard:"center"}
