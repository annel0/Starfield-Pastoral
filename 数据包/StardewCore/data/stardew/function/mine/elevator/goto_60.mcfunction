# stardew:mine/elevator/goto_60.mcfunction
function stardew:mine/elevator/clear_chat
data modify storage stardew:mine target_floor set value 60
function stardew:mine/enter/to_floor
