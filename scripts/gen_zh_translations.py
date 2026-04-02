#!/usr/bin/env python3
"""Generate Chinese translations for Abigail dialogue keys."""
import json

# Read the mod dialogue file (English keys)
with open("src/main/resources/data/stardewcraft/npc/dialogue/abigail.json", "r") as f:
    mod_data = json.load(f)

# Read the official Chinese translations
with open("源文件/Content/Characters/Dialogue/Abigail.zh-CN.json", "r") as f:
    zh_data = json.load(f)

# Build case-insensitive lookup for Chinese translations
zh_lower = {k.lower(): v for k, v in zh_data.items()}

translations = {}
missing = []

entries = mod_data.get("entries", {})
for vanilla_key, entry in entries.items():
    if isinstance(entry, dict) and "translate" in entry:
        tr_key = entry["translate"]
        suffix = vanilla_key.lower()

        if suffix in zh_lower:
            translations[tr_key] = zh_lower[suffix]
        else:
            missing.append((tr_key, suffix, entry.get("fallback", "")))

print(f"Matched: {len(translations)}")
print(f"Missing: {len(missing)}")
for m in missing:
    print(f"  MISS: {m[0]} -> suffix={m[1]}")
    print(f"        fallback={m[2][:80]}")

# Write the translation entries as JSON snippet
output = {}
for k in sorted(translations.keys()):
    output[k] = translations[k]

with open("/tmp/abigail_zh_translations.json", "w") as f:
    json.dump(output, f, ensure_ascii=False, indent=4)

print(f"\nWrote {len(output)} entries to /tmp/abigail_zh_translations.json")
