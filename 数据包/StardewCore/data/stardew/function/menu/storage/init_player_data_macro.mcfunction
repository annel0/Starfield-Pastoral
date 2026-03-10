# data/stardew/function/menu/storage/init_player_data_macro.mcfunction
# 初始化7个背包到storage

# 清空并创建7个默认背包
data modify storage stardew:storage bags set value []
data modify storage stardew:storage bags append value {name:"背包 #1",color:0,items:[]}
data modify storage stardew:storage bags append value {name:"背包 #2",color:0,items:[]}
data modify storage stardew:storage bags append value {name:"背包 #3",color:0,items:[]}
data modify storage stardew:storage bags append value {name:"背包 #4",color:0,items:[]}
data modify storage stardew:storage bags append value {name:"背包 #5",color:0,items:[]}
data modify storage stardew:storage bags append value {name:"背包 #6",color:0,items:[]}
data modify storage stardew:storage bags append value {name:"背包 #7",color:0,items:[]}
