#!/usr/bin/env python3
"""Compare vanilla Abigail dialogue source vs mod dialogue data."""
import json, sys

with open("源文件/Content/Characters/Dialogue/Abigail.json", encoding="utf-8") as f:
    vanilla = json.load(f)

with open("src/main/resources/data/stardewcraft/npc/dialogue/abigail.json", encoding="utf-8") as f:
    mod_raw = json.load(f)

mod_entries = mod_raw["entries"]
mod_map = {}
for key, val in mod_entries.items():
    if isinstance(val, dict) and "fallback" in val:
        mod_map[key] = val["fallback"]
    elif isinstance(val, str):
        mod_map[key] = val
    else:
        mod_map[key] = str(val)

vanilla_keys = set(vanilla.keys())
mod_keys = set(mod_map.keys())

missing_in_mod = sorted(vanilla_keys - mod_keys)
extra_in_mod = sorted(mod_keys - vanilla_keys)

print("=== MISSING in mod (vanilla has, mod lacks) ===")
for k in missing_in_mod:
    print(f"  {k}")
print(f"  Total: {len(missing_in_mod)}")

print("\n=== EXTRA in mod (mod has, vanilla lacks) ===")
for k in extra_in_mod:
    print(f"  {k}")
print(f"  Total: {len(extra_in_mod)}")

print("\n=== DIFF fallback text ===")
common = sorted(vanilla_keys & mod_keys)
diff_count = 0
for k in common:
    v = vanilla[k]
    m = mod_map[k]
    if v != m:
        diff_count += 1
        vt = v[:120] + "..." if len(v) > 120 else v
        mt = m[:120] + "..." if len(m) > 120 else m
        print(f"  {k}")
        print(f"    vanilla: {vt}")
        print(f"    mod:     {mt}")
print(f"  Total diffs: {diff_count}")

print(f"\n--- Summary ---")
print(f"Vanilla keys: {len(vanilla_keys)}")
print(f"Mod keys:     {len(mod_keys)}")
print(f"Missing: {len(missing_in_mod)}, Extra: {len(extra_in_mod)}, Text diff: {diff_count}")

# Also check source code usage
print("\n=== Checking NpcInteractionService for hardcoded strings ===")
import re
with open("src/main/java/com/stardew/craft/npc/runtime/NpcInteractionService.java", encoding="utf-8") as f:
    code = f.read()

# Find dialogue picking logic
hardcoded = re.findall(r'"([^"]{20,})"', code)
for h in hardcoded:
    if any(c.isalpha() for c in h) and not h.startswith("stardewcraft.") and "%" not in h and "/" not in h:
        # skip java identifiers, format strings, paths
        if " " in h or "'" in h:
            print(f"  Possible hardcoded: {h[:80]}")
