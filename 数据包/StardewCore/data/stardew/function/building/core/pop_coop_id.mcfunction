# ================================================================
# 星露谷物语 - 从栈中弹出鸡舍ID
# ================================================================
# 用途：从ID栈中取出一个可用ID分配给新建筑
# 调用：从init_building调用，作为建筑marker执行

# 找到栈顶（任意一个）
tag @e[type=marker,tag=stardew.id_stack.coop,limit=1,sort=random] add stardew.temp.pop

# 复制ID
scoreboard players operation @s stardew.building.id = @e[type=marker,tag=stardew.temp.pop,limit=1] stardew.building.id

# 删除栈节点
kill @e[tag=stardew.temp.pop]

tellraw @a[tag=debug] [{"text":"[ID栈] ","color":"aqua"},{"text":"从栈中分配鸡舍ID: "},{"score":{"name":"@s","objective":"stardew.building.id"}}]
