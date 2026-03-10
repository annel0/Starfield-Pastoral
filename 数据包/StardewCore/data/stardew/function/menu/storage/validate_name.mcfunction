# data/stardew/function/menu/storage/validate_name.mcfunction
# 验证名称长度并应用
# 宏参数: $(rename_text)

# 将文本存入临时位置用于长度检查
$data modify storage stardew:temp check_text set value "$(rename_text)"

# 检查长度（通过字符串长度判断）
execute store result score #NameLength sd_storage_temp run data get storage stardew:temp check_text

# 如果长度<=5，应用名称
execute if score #NameLength sd_storage_temp matches ..5 run function stardew:menu/storage/apply_rename

# 如果长度>5，拒绝并提示
execute if score #NameLength sd_storage_temp matches 6.. run function stardew:menu/storage/reject_rename
