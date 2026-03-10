# stardew:mine/floor/theme1/room2/spawn_exit.mcfunction
# 在 theme1/room2 生成出口梯子
# 参数: $(z3)

$execute in stardew:mine positioned 6 65 $(z3) run function stardew:mine/ladder/spawn_exit
