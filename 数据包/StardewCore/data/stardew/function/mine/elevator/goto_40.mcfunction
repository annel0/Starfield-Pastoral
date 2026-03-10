# stardew:mine/elevator/goto_40.mcfunction
function stardew:mine/elevator/clear_chat
data modify storage stardew:mine target_floor set value 40
function stardew:mine/enter/to_floor
