# stardew:mine/floor/theme2/treasure_room/spawn_fishing_marker.mcfunction
# 在宝藏房间生成钓鱼区标记（theme2 冰川主题）
# 参数: $(z27)
# 位置: X=14, Y=64, Z+27
# 功能: 标记钓鱼区域，玩家可以在此钓鱼

# 生成钓鱼区标记（armor_stand 作为标记点）
$execute in stardew:mine run summon minecraft:armor_stand 14 64 $(z27) {Tags:["sd_mine_fishing_zone","sd_mine_entity","sd_fishing_theme2"],Invisible:1b,Invulnerable:1b,NoGravity:1b,Marker:1b}

# 生成提示文字
$execute in stardew:mine run summon minecraft:text_display 14 65.5 $(z27) {Tags:["sd_mine_entity"],text:'{"text":"🎣 钓鱼区\\n冰川池塘","color":"aqua","bold":true}',billboard:"vertical",shadow:true,transformation:{scale:[0.8f,0.8f,0.8f]}}
