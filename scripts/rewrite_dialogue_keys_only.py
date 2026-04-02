#!/usr/bin/env python3
"""Rewrite dialogue JSON to key-only format and extract English to en_us.json."""
import json

DIALOGUE_FILE = "src/main/resources/data/stardewcraft/npc/dialogue/abigail.json"
EN_FILE = "src/main/resources/assets/stardewcraft/lang/en_us.json"

# 1. Read dialogue JSON
with open(DIALOGUE_FILE) as f:
    d = json.load(f)

entries = d.get("entries", {})

new_entries = {}
en_entries = {}

for vanilla_key, value in entries.items():
    if isinstance(value, dict) and "translate" in value:
        tr_key = value["translate"]
        fallback = value.get("fallback", "...")
        new_entries[vanilla_key] = tr_key
        en_entries[tr_key] = fallback
    elif isinstance(value, str):
        new_entries[vanilla_key] = value
    else:
        new_entries[vanilla_key] = str(value)

# 2. Write new dialogue JSON (key-only)
new_dialogue = {
    "npc_id": d.get("npc_id", "abigail"),
    "entries": new_entries
}
with open(DIALOGUE_FILE, "w") as f:
    json.dump(new_dialogue, f, ensure_ascii=False, indent=2)

print(f"Dialogue: {len(new_entries)} entries rewritten to key-only")

# 3. Merge English entries into en_us.json
with open(EN_FILE) as f:
    en = json.load(f)

before = len(en)
en.update(en_entries)
after = len(en)
print(f"en_us.json: {before} -> {after} keys (+{after - before} new)")

with open(EN_FILE, "w") as f:
    json.dump(en, f, ensure_ascii=False, indent=4)

# Validate
with open(EN_FILE) as f:
    json.load(f)
with open(DIALOGUE_FILE) as f:
    json.load(f)
print("JSON validation passed.")
