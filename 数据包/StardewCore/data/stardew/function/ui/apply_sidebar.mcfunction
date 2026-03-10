# data/stardew/function/ui/apply_sidebar.mcfunction
# 宏参数: Line1, Line2, Line3 (这些是 JSON 字符串)

$team modify sd_ui_1 suffix $(Line1)
$team modify sd_ui_2 suffix $(Line2)
$team modify sd_ui_3 suffix $(Line3)