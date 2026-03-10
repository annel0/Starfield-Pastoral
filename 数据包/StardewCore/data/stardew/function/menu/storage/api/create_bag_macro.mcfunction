# data/stardew/function/menu/storage/api/create_bag_macro.mcfunction
# 使用宏创建背包数据

$data modify storage stardew:storage players append value {UUID:$(player_uuid),bags:[{id:$(bag_id),name:"背包 #$(bag_id)",color:0,items:[]}]}
