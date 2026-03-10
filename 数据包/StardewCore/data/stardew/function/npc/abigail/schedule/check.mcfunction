# 阿比盖尔的每周日程表（基于Stardew Valley Wiki）
# @s = npc.abigail 实体
# sd_time: 每天06:00 = 360分钟 (07:00=420, 09:00=540, 12:00=720, 18:00=1080, 24:00=1440)
# schedule状态: 1=在家, 2=镇中心, 3=墓地, 4=酒馆
# sd_day_of_week: 0=第0天, 1=周一, 2=周二, 3=周三, 4=周四, 5=周五, 6=周六


# ============ 第0天 (0) - 游戏开始日 ============
# 整天在家
execute if score Global sd_day_of_week matches 0 unless score @s stardew.npc.schedule matches 1 run function stardew:npc/abigail/schedule/goto_home

# ============ 周一 (1) - Monday ============
# 06:00-09:00 (360-539分) - 在家
# 09:00-13:00 (540-779分) - 在商店/镇中心
# 13:00-17:00 (780-1019分) - 去墓地
# 17:00以后 (1020+分) - 回家
execute if score Global sd_day_of_week matches 1 if score Global sd_time matches 360..539 unless score @s stardew.npc.schedule matches 1 run function stardew:npc/abigail/schedule/goto_home
execute if score Global sd_day_of_week matches 1 if score Global sd_time matches 540..779 unless score @s stardew.npc.schedule matches 2 run function stardew:npc/abigail/schedule/goto_town_square
execute if score Global sd_day_of_week matches 1 if score Global sd_time matches 780..1019 unless score @s stardew.npc.schedule matches 3 run function stardew:npc/abigail/schedule/goto_graveyard
execute if score Global sd_day_of_week matches 1 if score Global sd_time matches 1020.. unless score @s stardew.npc.schedule matches 1 run function stardew:npc/abigail/schedule/goto_home

# ============ 周二 (2) - Tuesday ============
# 06:00-09:00 (360-539分) - 在家
# 09:00-18:00 (540-1079分) - 在商店帮忙（镇中心）
# 18:00以后 (1080+分) - 回家
execute if score Global sd_day_of_week matches 2 if score Global sd_time matches 360..539 unless score @s stardew.npc.schedule matches 1 run function stardew:npc/abigail/schedule/goto_home
execute if score Global sd_day_of_week matches 2 if score Global sd_time matches 540..1079 unless score @s stardew.npc.schedule matches 2 run function stardew:npc/abigail/schedule/goto_town_square
execute if score Global sd_day_of_week matches 2 if score Global sd_time matches 1080.. unless score @s stardew.npc.schedule matches 1 run function stardew:npc/abigail/schedule/goto_home

# ============ 周三 (3) - Wednesday ============
# 和周一相同
execute if score Global sd_day_of_week matches 3 if score Global sd_time matches 360..539 unless score @s stardew.npc.schedule matches 1 run function stardew:npc/abigail/schedule/goto_home
execute if score Global sd_day_of_week matches 3 if score Global sd_time matches 540..779 unless score @s stardew.npc.schedule matches 2 run function stardew:npc/abigail/schedule/goto_town_square
execute if score Global sd_day_of_week matches 3 if score Global sd_time matches 780..1019 unless score @s stardew.npc.schedule matches 3 run function stardew:npc/abigail/schedule/goto_graveyard
execute if score Global sd_day_of_week matches 3 if score Global sd_time matches 1020.. unless score @s stardew.npc.schedule matches 1 run function stardew:npc/abigail/schedule/goto_home

# ============ 周四 (4) - Thursday ============
# 整天在家
execute if score Global sd_day_of_week matches 4 unless score @s stardew.npc.schedule matches 1 run function stardew:npc/abigail/schedule/goto_home

# ============ 周五 (5) - Friday ============
# 06:00-17:00 (360-1019分) - 在家
# 17:00-23:30 (1020-1409分) - 去酒馆
# 23:30以后 (1410+分) - 回家
execute if score Global sd_day_of_week matches 5 if score Global sd_time matches 360..1019 unless score @s stardew.npc.schedule matches 1 run function stardew:npc/abigail/schedule/goto_home
execute if score Global sd_day_of_week matches 5 if score Global sd_time matches 1020..1409 unless score @s stardew.npc.schedule matches 4 run function stardew:npc/abigail/schedule/goto_saloon
execute if score Global sd_day_of_week matches 5 if score Global sd_time matches 1410.. unless score @s stardew.npc.schedule matches 1 run function stardew:npc/abigail/schedule/goto_home

# ============ 周六 (6) - Saturday ============
# 06:00-10:00 (360-599分) - 在家
# 10:00-18:00 (600-1079分) - 去墓地
# 18:00以后 (1080+分) - 回家
execute if score Global sd_day_of_week matches 6 if score Global sd_time matches 360..599 unless score @s stardew.npc.schedule matches 1 run function stardew:npc/abigail/schedule/goto_home
execute if score Global sd_day_of_week matches 6 if score Global sd_time matches 600..1079 unless score @s stardew.npc.schedule matches 3 run function stardew:npc/abigail/schedule/goto_graveyard
execute if score Global sd_day_of_week matches 6 if score Global sd_time matches 1080.. unless score @s stardew.npc.schedule matches 1 run function stardew:npc/abigail/schedule/goto_home
