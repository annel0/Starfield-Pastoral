# stardew:mine/floor/clear_entities_impl.mcfunction
# 实际清理实体 (使用宏参数)
# 参数: $(z), $(dz)

# 检测石头数量 (sd_stone 标签)
$execute store result score #stone_count sd_mine_temp in stardew:mine positioned 0 60 $(z) if entity @e[tag=sd_stone,dx=50,dy=20,dz=$(dz)]

# ===== 清理实体 =====
# 石头 - 用 sd_stone 标签 (不是 sd_mine_stone)
$execute in stardew:mine positioned 0 60 $(z) run kill @e[tag=sd_stone,dx=50,dy=20,dz=$(dz)]
$execute in stardew:mine positioned 0 60 $(z) run kill @e[tag=sd_stone_display,dx=50,dy=20,dz=$(dz)]

# 桶
$execute in stardew:mine positioned 0 60 $(z) run kill @e[tag=sd_mine_barrel,dx=50,dy=20,dz=$(dz)]
$execute in stardew:mine positioned 0 60 $(z) run kill @e[tag=sd_barrel_display,dx=50,dy=20,dz=$(dz)]

# 梯子相关
$execute in stardew:mine positioned 0 60 $(z) run kill @e[tag=sd_mine_ladder_down,dx=50,dy=20,dz=$(dz)]
$execute in stardew:mine positioned 0 60 $(z) run kill @e[tag=sd_mine_ladder_text,dx=50,dy=20,dz=$(dz)]
$execute in stardew:mine positioned 0 60 $(z) run kill @e[tag=sd_mine_ladder_exit,dx=50,dy=20,dz=$(dz)]

# 下一层入口
$execute in stardew:mine positioned 0 60 $(z) run kill @e[tag=sd_mine_next_floor,dx=50,dy=20,dz=$(dz)]

# 电梯 (排除大厅电梯)
$execute in stardew:mine positioned 0 60 $(z) run kill @e[tag=sd_mine_elevator,tag=!sd_mine_lobby_elevator,dx=50,dy=20,dz=$(dz)]
$execute in stardew:mine positioned 0 60 $(z) run kill @e[tag=sd_mine_elevator_text,dx=50,dy=20,dz=$(dz)]

# 钓鱼区标记
$execute in stardew:mine positioned 0 60 $(z) run kill @e[tag=sd_mine_fishing_zone,dx=50,dy=20,dz=$(dz)]

# 怪物 - 清理所有矿洞怪物
$execute in stardew:mine positioned 0 60 $(z) run kill @e[tag=sd_monster,dx=50,dy=20,dz=$(dz)]

# 通用标签兜底
$execute in stardew:mine positioned 0 60 $(z) run kill @e[tag=sd_mine_entity,dx=50,dy=20,dz=$(dz)]
