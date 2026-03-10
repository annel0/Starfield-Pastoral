# stardew:mine/elevator/goto_70.mcfunction
function stardew:mine/elevator/clear_chat
data modify storage stardew:mine target_floor set value 70
function stardew:mine/enter/to_floor
