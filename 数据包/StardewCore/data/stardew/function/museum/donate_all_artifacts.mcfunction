# stardew:museum/donate_all_artifacts
# 调试函数:标记玩家已捐赠所有古物

# 古物标记 (100-141)
scoreboard players set @s sd_donated 141

tellraw @s [{"text":"✅ ","color":"green"},{"text":"已标记所有古物为已捐赠","color":"yellow"}]
