summon interaction ~0.5 ~ ~0.5 {Tags:["sd_tree","tree_mahogany","new_tree"],width:2.2f,height:4f,response:1b,Invulnerable:1b}

execute as @e[type=interaction,tag=new_tree,sort=nearest,limit=1] run scoreboard players set @s sd_tree_hp 50
execute as @e[type=interaction,tag=new_tree,sort=nearest,limit=1] run scoreboard players set @s sd_tree_type 4
execute as @e[type=interaction,tag=new_tree,sort=nearest,limit=1] run scoreboard players set @s sd_shaked 0
execute as @e[type=interaction,tag=new_tree,sort=nearest,limit=1] run scoreboard players set @s sd_crop_age 28

tag @e[tag=new_tree] remove new_tree

summon item_display ~0.5 ~2.0625 ~0.5 {Tags:["sd_tree_vis"],item_display:"fixed",transformation:{scale:[5.5f,7.0f,5.5f],translation:[0.0f,0.0f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]},item:{id:"minecraft:paper",Count:1b,components:{"minecraft:custom_model_data":833}}}

fill ~ ~ ~ ~1 ~3 ~1 minecraft:barrier