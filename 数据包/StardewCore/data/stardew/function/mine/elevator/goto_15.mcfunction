# stardew:mine/elevator/goto_15.mcfunction
function stardew:mine/elevator/clear_chat
data modify storage stardew:mine target_floor set value 15
function stardew:mine/enter/to_floor
