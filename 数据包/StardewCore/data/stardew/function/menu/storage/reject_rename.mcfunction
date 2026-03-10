# data/stardew/function/menu/storage/reject_rename.mcfunction
# 拒绝超长名称

# 提示玩家
tellraw @s {"text":"名称不能超过5个字符！请重新输入。","color":"red"}

# 不退出重命名状态，不回收书与笔
