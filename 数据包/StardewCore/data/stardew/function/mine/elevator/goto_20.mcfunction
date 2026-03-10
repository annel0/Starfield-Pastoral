# stardew:mine/elevator/goto_20.mcfunction
function stardew:mine/elevator/clear_chat
data modify storage stardew:mine target_floor set value 20
function stardew:mine/enter/to_floor
