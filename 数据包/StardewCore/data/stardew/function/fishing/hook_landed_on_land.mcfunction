# hook_landed_on_land.mcfunction
# Executor: fishing_bobber

tellraw @a[distance=..16] {"text":"[钓鱼] 鱼钩没有落在水里！","color":"red"}
kill @s
