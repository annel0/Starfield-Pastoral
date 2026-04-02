#!/usr/bin/env python3
"""Insert Abigail Chinese translations into zh_cn.json."""
import json

ZH_FILE = "src/main/resources/assets/stardewcraft/lang/zh_cn.json"

# Read existing zh_cn.json
with open(ZH_FILE, "r") as f:
    zh = json.load(f)

# Read generated translations
with open("/tmp/abigail_zh_translations.json", "r") as f:
    new_entries = json.load(f)

# Check for conflicts
conflicts = []
for k, v in new_entries.items():
    if k in zh and zh[k] != v:
        conflicts.append((k, zh[k], v))

if conflicts:
    print(f"WARNING: {len(conflicts)} conflicting keys:")
    for c in conflicts:
        print(f"  {c[0]}: existing={c[1][:40]} new={c[2][:40]}")

# Merge
before = len(zh)
zh.update(new_entries)
after = len(zh)
print(f"Before: {before} keys, After: {after} keys, Added: {after - before}")

# Write back
with open(ZH_FILE, "w") as f:
    json.dump(zh, f, ensure_ascii=False, indent=4)

# Validate
with open(ZH_FILE, "r") as f:
    json.load(f)
print("JSON validation passed.")
