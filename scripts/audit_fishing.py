#!/usr/bin/env python3
"""Audit mod fishing rule chance/precedence vs SDV Locations.json source.

Mod files: src/main/resources/data/stardewcraft/fishing/locations/*.json
SDV source: 源文件/Content/Data/Locations.json (Fish array per location)
"""
from __future__ import annotations
import json, re, sys
from pathlib import Path
from collections import defaultdict

ROOT = Path(__file__).resolve().parents[1]
MOD_DIR = ROOT / "src/main/resources/data/stardewcraft/fishing/locations"
SDV_PATH = ROOT / "源文件/Content/Data/Locations.json"

# mod-id (snake_case) -> SDV InternalName(s)
# Build by scanning Objects.json for DisplayName→ItemId, but easier: maintain mapping from SDV id constants.
# Approach: SDV Locations.json fish entries use either "(O)<InternalName>" or "(O)<numericId>".
# We'll resolve mod item id "stardewcraft:<snake>" by converting snake -> PascalCase and matching.

def snake_to_pascal(s: str) -> str:
    return ''.join(p.capitalize() for p in s.split('_'))

# Map mod location filename → SDV location key(s) in Locations.json
MOD_TO_SDV_LOC = {
    "beach.json": ["Beach"],
    "town.json": ["Town"],
    "forest.json": ["Forest"],
    "mountain.json": ["Mountain"],
    "desert.json": ["Desert"],
    "sewer.json": ["Sewer"],
    "woods.json": ["Woods"],
    "witchswamp.json": ["WitchSwamp"],
    "bugland.json": ["MutantBugLair", "BugLand"],
    "submarine.json": ["Submarine"],
    "caldera.json": ["Caldera"],
    "undergroundmine.json": ["UndergroundMine"],
    "islandnorth.json": ["IslandNorth"],
    "islandsouth.json": ["IslandSouth"],
    "islandsoutheast.json": ["IslandSouthEast"],
    "islandsoutheastcave.json": ["IslandSouthEastCave"],
    "islandwest.json": ["IslandWest"],
    "beachnightmarket.json": ["BeachNightMarket"],
    "overworld.json": [],   # custom catch-all, no direct SDV equivalent
    "default.json": [],     # custom fallback
    "stardew_valley.json": [],  # mod-only umbrella
}

def load_sdv_locations() -> dict[str, list[dict]]:
    with SDV_PATH.open(encoding="utf-8") as f:
        data = json.load(f)
    out = {}
    # Locations.json structure varies — try common shapes
    if isinstance(data, dict):
        # Could be {"LocationName": {...}} or {"Locations": {...}}
        if "Locations" in data and isinstance(data["Locations"], dict):
            data = data["Locations"]
        for name, entry in data.items():
            if isinstance(entry, dict) and "Fish" in entry:
                out[name] = entry["Fish"] or []
    return out

def normalize_sdv_id(item_id: str) -> str:
    # "(O)Lingcod" -> "Lingcod"; "(O)160" -> "160"
    m = re.match(r'^\([A-Z]\)(.+)$', item_id)
    return m.group(1) if m else item_id

def load_id_to_name() -> dict[str, str]:
    """Build {numericId or internalName -> InternalName} map from Objects.json."""
    objs_path = ROOT / "src/main/resources/data/stardewcraft/npc/vanilla/data/Objects.json"
    with objs_path.open(encoding="utf-8-sig") as f:
        data = json.load(f)
    out = {}
    for key, val in data.items():
        if not isinstance(val, dict): continue
        name = val.get("Name") or key
        out[str(key)] = name      # numeric id -> name
        out[name] = name          # name -> name (passthrough)
    return out

def main():
    sdv_locs = load_sdv_locations()
    id_to_name = load_id_to_name()
    if not sdv_locs:
        print("ERROR: failed to parse SDV Locations.json", file=sys.stderr)
        sys.exit(1)

    print(f"Loaded {len(sdv_locs)} SDV locations, {len(id_to_name)} object mappings\n")

    mismatches = defaultdict(list)
    for mod_file in sorted(MOD_DIR.glob("*.json")):
        sdv_keys = MOD_TO_SDV_LOC.get(mod_file.name, [])
        if not sdv_keys:
            continue
        # Aggregate fish entries from all matching SDV locations into a dict by InternalName
        sdv_fish: dict[str, dict] = {}
        for k in sdv_keys:
            for entry in sdv_locs.get(k, []):
                raw = normalize_sdv_id(entry.get("ItemId") or entry.get("Id") or "")
                iid = id_to_name.get(raw, raw)
                if iid and iid not in sdv_fish:
                    sdv_fish[iid] = entry

        with mod_file.open(encoding="utf-8-sig") as f:
            mod_data = json.load(f)
        mod_rules = mod_data.get("rules") or mod_data.get("fish") or mod_data.get("entries") or []

        for rule in mod_rules:
            item = rule.get("item", "")
            # "stardewcraft:lingcod" -> "lingcod" -> "Lingcod"
            short = item.split(":")[-1]
            pascal = snake_to_pascal(short)
            sdv = sdv_fish.get(pascal)
            if sdv is None:
                # not a vanilla SDV fish for this location; skip silently
                continue
            mod_chance = rule.get("chance")
            sdv_chance = sdv.get("Chance")
            mod_prec = rule.get("precedence", 0)
            sdv_prec = sdv.get("Precedence", 0)
            mod_lvl = rule.get("minFishingLevel", 0)
            sdv_lvl = sdv.get("MinFishingLevel", 0)

            issues = []
            if mod_chance != sdv_chance:
                issues.append(f"chance mod={mod_chance} sdv={sdv_chance}")
            if mod_prec != sdv_prec:
                issues.append(f"precedence mod={mod_prec} sdv={sdv_prec}")
            if mod_lvl != sdv_lvl:
                issues.append(f"minFishingLevel mod={mod_lvl} sdv={sdv_lvl}")
            if issues:
                mismatches[mod_file.name].append((short, pascal, issues))

    total = 0
    for fname, items in mismatches.items():
        if not items: continue
        print(f"\n=== {fname} ===")
        for short, pascal, issues in items:
            total += 1
            print(f"  {short:30s} ({pascal})")
            for i in issues:
                print(f"     - {i}")
    print(f"\nTotal mismatched fish entries: {total}")

if __name__ == "__main__":
    main()
