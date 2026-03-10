# stardew:mine/elevator/goto_75.mcfunction
function stardew:mine/elevator/clear_chat
data modify storage stardew:mine target_floor set value 75
function stardew:mine/enter/to_floor
