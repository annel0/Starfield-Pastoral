(Get-Content fix_gizmo.py) -replace '\\"\\"\\"', '\"\"\"' | Set-Content fix_gizmo.py
(Get-Content fix_gizmo2.py) -replace '\\"\\"\\"', '\"\"\"' | Set-Content fix_gizmo2.py
