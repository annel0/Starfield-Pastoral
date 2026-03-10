# data/stardew/function/menu/storage/apply_rename_macro.mcfunction
# 使用宏应用重命名

$data modify storage stardew:storage bags[$(bag_id)].name set from storage stardew:temp rename_text
