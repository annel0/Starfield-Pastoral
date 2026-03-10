summon interaction ~ ~ ~ {Tags:["sd_tree","tree_maple","new_tree"],width:1.2f,height:3f,response:1b,Invulnerable:1b}

execute as @e[type=interaction,tag=new_tree,sort=nearest,limit=1] run scoreboard players set @s sd_tree_hp 30
execute as @e[type=interaction,tag=new_tree,sort=nearest,limit=1] run scoreboard players set @s sd_tree_type 2
execute as @e[type=interaction,tag=new_tree,sort=nearest,limit=1] run scoreboard players set @s sd_shaked 0
execute as @e[type=interaction,tag=new_tree,sort=nearest,limit=1] run scoreboard players set @s sd_crop_age 28

tag @e[tag=new_tree] remove new_tree

summon item_display ~ ~1.125 ~ {Tags:["sd_tree_vis"],item_display:"fixed",transformation:{scale:[3.4f,5.0f,3.4f],translation:[0.0f,0.0f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]},item:{id:"minecraft:paper",Count:1b,components:{"minecraft:custom_model_data":813}}}

setblock ~ ~ ~ minecraft:barrier
setblock ~ ~1 ~ minecraft:barrier
setblock ~ ~2 ~ minecraft:barrier