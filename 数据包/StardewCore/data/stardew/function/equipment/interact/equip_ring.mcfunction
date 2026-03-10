# data/stardew/function/equipment/interact/equip_ring.mcfunction
# [执行者: 玩家] 装备戒指 (队列管理)

# 初始化玩家装备数据
function stardew:equipment/storage/init_player

# 戒指队列: 优先填充槽位1→2→3→4，装备成功后立即返回
execute if score @s sd_equip_ring1 matches 0 run return run function stardew:equipment/interact/equip_ring1_impl
execute if score @s sd_equip_ring2 matches 0 run return run function stardew:equipment/interact/equip_ring2_impl
execute if score @s sd_unlock_ring3 matches 1 if score @s sd_equip_ring3 matches 0 run return run function stardew:equipment/interact/equip_ring3_impl
execute if score @s sd_unlock_ring4 matches 1 if score @s sd_equip_ring4 matches 0 run return run function stardew:equipment/interact/equip_ring4_impl

# 如果执行到这里，说明所有槽位都满了
function stardew:equipment/interact/ring_slots_full
