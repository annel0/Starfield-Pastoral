
# data/stardew/function/mining/on_mine.mcfunction
# 玩家左键挖掘石头时触发
# 执行者: 玩家 (@s)
# 上下文: 由 check_interaction 通过 execute on attacker 调用

# 直接调用实现逻辑
function stardew:mining/on_mine_impl

# DEBUG输出（可删）
# tellraw @s {"text":"[DEBUG] 挖掘石头触发","color":"yellow"}
