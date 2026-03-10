# =========================================================
# 检测玩家与洒水器的交互
# =========================================================
# 执行者: interaction 实体 (@s)
# =========================================================

# 左键攻击 - 拆除洒水器
execute if data entity @s attack run execute on attacker run function stardew:utility/sprinkler/break_sprinkler

# 清除交互状态(防止重复触发)
data remove entity @s attack
data remove entity @s interaction
