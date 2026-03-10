# stardew:museum/donate_all
# 调试函数:标记玩家已捐赠所有物品
# 用于测试鉴定系统

# 宝石标记 (1-11)
scoreboard players set @s sd_donated 11

tellraw @s [{"text":"✅ ","color":"green"},{"text":"已标记所有宝石为已捐赠","color":"yellow"}]
tellraw @s [{"text":"💡 提示: 古物标记范围100-141,可用 ","color":"gray"},{"text":"/function stardew:museum/donate_all_artifacts","color":"aqua","clickEvent":{"action":"suggest_command","value":"/function stardew:museum/donate_all_artifacts"}},{"text":" 标记古物","color":"gray"}]
