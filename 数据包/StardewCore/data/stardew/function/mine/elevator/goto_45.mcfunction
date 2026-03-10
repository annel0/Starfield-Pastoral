# stardew:mine/elevator/goto_45.mcfunction
function stardew:mine/elevator/clear_chat
data modify storage stardew:mine target_floor set value 45
function stardew:mine/enter/to_floor
