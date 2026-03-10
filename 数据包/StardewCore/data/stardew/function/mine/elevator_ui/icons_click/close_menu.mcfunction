tag @s add sd_elevator_closing

execute as @e[type=interaction,tag=interact_target,distance=..1] at @s positioned ^ ^ ^1 run function stardew:mine/elevator_ui/icons_click/close_menu_animation

playsound ui.button.click block @s ~ ~ ~ 0.5 1.2
playsound ui.toast.out block @s ~ ~ ~ 1 1.2

schedule function stardew:mine/elevator_ui/icons_click/close_menu_schedule 7t
