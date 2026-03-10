# stardew:combat/init_monster_dynamic.mcfunction
# 动态解析怪物血量和攻击力标签
# 从 sd_hp_XXX 和 sd_atk_XXX 标签中提取数值

# 将实体标签复制到storage
data modify storage stardew:temp monster_tags set from entity @s Tags

# 遍历标签，查找 sd_hp_ 和 sd_atk_ 前缀
function stardew:combat/parse_monster_tags_loop
